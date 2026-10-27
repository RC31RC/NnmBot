package com.nnm.bot.core.channel.notification.store;

import java.io.OutputStream;
import java.util.Iterator;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.storage.ObjectStorage;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.mq.message.notification.NotificationResource;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.config.BotNotificationProperties;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/** 草稿按用户授权，任务按不可变 manifest 读取；过期对象统一回收，包括提交失败和进程崩溃。 */
@Service
@Slf4j
public class NotificationResourceStorage {
    private static final String DRAFTS = "bot/notifications/drafts/";
    private static final String JOBS = "bot/notifications/jobs/";
    @Resource
    private ObjectStorage storage;
    @Resource
    private SharedStateStore states;
    @Resource
    private ReplyProperties limits;
    @Resource
    private BotNotificationProperties properties;

    public NotificationResource upload(long owner, InputStream input, long size) throws IOException {
        BusinessAssert.isTrue(size > 0 && size <= limits.getMaxImageBytes(), CommonErrorCode.PARAM_INVALID,
                "图片大小超过限制");
        UUID id = UUID.randomUUID();
        Path root = Files.createTempDirectory("nnm-notification-upload-");
        Path path = root.resolve("image");
        String objectId = DRAFTS + id;
        boolean objectMayExist = false;
        Throwable primary = null;
        try {
            copyBounded(input, path, size);
            String contentType;
            int width;
            int height;
            try (ImageInputStream image = ImageIO.createImageInputStream(path.toFile())) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
                BusinessAssert.isTrue(readers.hasNext(), CommonErrorCode.PARAM_INVALID, "仅支持可识别的 PNG、JPEG 或 GIF 图片");
                ImageReader reader = readers.next();
                try {
                    reader.setInput(image, true, true);
                    String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                    BusinessAssert.isTrue(Set.of("png", "jpeg", "jpg", "gif").contains(format),
                            CommonErrorCode.PARAM_INVALID, "不支持的图片格式");
                    width = reader.getWidth(0);
                    height = reader.getHeight(0);
                    BusinessAssert.isTrue(width > 0 && height > 0 && (long) width * height <= limits.getMaxImagePixels(),
                            CommonErrorCode.PARAM_INVALID, "图片像素超过限制");
                    contentType = "image/" + (format.equals("jpg") ? "jpeg" : format);
                } finally {
                    reader.dispose();
                }
            }
            try (InputStream file = Files.newInputStream(path)) {
                objectMayExist = true;
                storage.put(objectId, file, size, contentType);
                NotificationResource resource = new NotificationResource(id, owner, objectId, contentType, size, width, height,
                        Instant.now().plus(properties.resourceRetention()));
                states.put(key(id), resource, properties.resourceRetention());
                return resource;
            }
        } catch (RuntimeException | IOException failure) {
            primary = failure;
            if (objectMayExist) {
                try {
                    storage.delete(objectId);
                } catch (RuntimeException cleanup) {
                    failure.addSuppressed(cleanup);
                }
            }
            throw failure;
        } finally {
            try {
                Files.deleteIfExists(path);
                Files.deleteIfExists(root);
            } catch (IOException cleanup) {
                if (primary != null) primary.addSuppressed(cleanup);
                else throw cleanup;
            }
        }
    }

    public NotificationResource owned(long owner, UUID id) {
        NotificationResource resource = states.<NotificationResource>get(key(id), NotificationResource.class).orElse(null);
        BusinessAssert.isTrue(resource != null && resource.ownerId() == owner
                        && Instant.now().isBefore(resource.expiresAt()), CommonErrorCode.PARAM_INVALID, "图片资源不存在、已过期或无权访问");
        return resource;
    }

    public InputStream openOwned(long owner, UUID id) throws IOException {
        return storage.open(owned(owner, id).objectId());
    }

    public void deleteOwned(long owner, UUID id) {
        NotificationResource resource = owned(owner, id);
        storage.delete(resource.objectId());
        states.evict(key(id));
    }

    public NotificationResourceManifest retain(long owner, UUID submission, Collection<UUID> ids, Instant expiry) {
        BusinessAssert.isTrue(expiry.isAfter(Instant.now())
                        && !expiry.isAfter(Instant.now().plus(properties.maxScheduleDelay()).plus(properties.resourceRetention())),
                CommonErrorCode.PARAM_INVALID, "任务资源保留时间超出限制");
        LinkedHashMap<UUID,NotificationResource> resources = new LinkedHashMap<UUID, NotificationResource>();
        ids.forEach(id -> resources.put(id, owned(owner, id)));
        LinkedHashMap<UUID,NotificationResource> retained = new LinkedHashMap<UUID, NotificationResource>();
        try {
        resources.forEach((id, resource) -> {
            String objectId = JOBS + submission + "/" + id;
            storage.copy(resource.objectId(), objectId);
            retained.put(id, new NotificationResource(id, owner, objectId, resource.contentType(), resource.size(),
                    resource.width(), resource.height(), expiry));
        });
        } catch (RuntimeException failure) {
            retained.values().forEach(resource -> {
                try { storage.delete(resource.objectId()); } catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
            });
            throw failure;
        }
        return new NotificationResourceManifest(submission, retained, expiry);
    }

    public NotificationResourceManifest drafts(long owner, Collection<UUID> ids) {
        Map<UUID, NotificationResource> resources = new LinkedHashMap<>();
        ids.forEach(id -> resources.put(id, owned(owner, id)));
        Instant expiresAt = resources.values().stream().map(NotificationResource::expiresAt)
                .min(Instant::compareTo).orElseGet(() -> Instant.now().plus(properties.resourceRetention()));
        return new NotificationResourceManifest(UUID.randomUUID(), resources, expiresAt);
    }

    public CommandAttachment materialize(NotificationResourceManifest manifest, UUID id) {
        NotificationResource resource = manifest.resources().get(id);
        BusinessAssert.isTrue(resource != null && Instant.now().isBefore(manifest.expiresAt()),
                CommonErrorCode.PARAM_INVALID, "任务资源不存在或已过期");
        try {
            Path root = Files.createTempDirectory("nnm-notification-delivery-");
            Path file = root.resolve("image");
            try (InputStream input = storage.open(resource.objectId())) {
                copyBounded(input, file, resource.size());
                return CommandAttachment.temporaryFile("notification-" + id, resource.contentType(), file, root);
            } catch (IOException | RuntimeException exception) {
                try {
                    Files.deleteIfExists(file);
                    Files.deleteIfExists(root);
                } catch (IOException cleanup) {
                    exception.addSuppressed(cleanup);
                }
                throw exception;
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("读取跨节点通知图片失败", exception);
        }
    }

    public void deleteExpired() {
        Instant cutoff = Instant.now().minus(properties.resourceRetention());
        try {
            storage.deleteExpired(DRAFTS, cutoff);
        } catch (RuntimeException failure) {
            log.warn("Notification draft cleanup failed", failure);
        }
        try {
            storage.deleteExpired(JOBS, cutoff.minus(properties.maxScheduleDelay()));
        } catch (RuntimeException failure) {
            log.warn("Notification job resource cleanup failed", failure);
        }
    }

    private void copyBounded(InputStream input, Path target, long expected) throws IOException {
        if (expected < 1 || expected > limits.getMaxImageBytes()) throw new IOException("Invalid notification image size");
        try (OutputStream output = Files.newOutputStream(target)) {
            byte[] buffer = new byte[8192];
            long copied = 0;
            int count;
            while ((count = input.read(buffer)) != -1) {
                copied += count;
                if (copied > expected) throw new IOException("Notification image exceeds its declared size");
                output.write(buffer, 0, count);
            }
            if (copied != expected) throw new IOException("Incomplete notification image");
        }
    }

    private String key(UUID id) { return "notification-resource:" + id; }
}
