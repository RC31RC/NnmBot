package com.nnm.bot.warframe.storage.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumStoredFileDTO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.ResourceResolverChain;

/** 将论坛公开附件地址解析为受文件服务校验的对象存储资源。 */
public class ForumFileResourceResolver extends AbstractResourceResolver {

    private final ForumFileService fileService;
    private final boolean temporary;

    /**
     * 指定文件服务和附件生命周期。
     *
     * @param fileService 负责路径校验与对象读取的文件服务。
     * @param temporary 是否读取临时附件。
     */
    public ForumFileResourceResolver(ForumFileService fileService, boolean temporary) {
        this.fileService = fileService;
        this.temporary = temporary;
    }

    /**
     * 按年份、月份和文件名解析公开附件，禁止将任意存储路径交给客户端。
     *
     * @param request 当前 HTTP 请求；资源预解析时可以为空。
     * @param requestPath 相对附件路径。
     * @param locations 已注册的资源位置。
     * @param chain 后续资源解析链。
     * @return 可读取的资源；路径层级不合法时返回空。
     */
    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
        List<? extends Resource> locations, ResourceResolverChain chain) {
        String[] segments = requestPath.split("/", -1);
        if (segments.length != 3) {
            return null;
        }
        ForumStoredFileDTO file = temporary ? fileService.loadTemporary(segments[0], segments[1], segments[2])
            : fileService.loadPermanent(segments[0], segments[1], segments[2]);
        return new StoredForumResource(file, segments[2]);
    }

    /**
     * 保留文件服务生成的稳定公开路径。
     *
     * @param resourceUrlPath 相对资源地址。
     * @param locations 已注册的资源位置。
     * @param chain 后续资源解析链。
     * @return 资源相对地址。
     */
    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
        ResourceResolverChain chain) {
        return resourceUrlPath;
    }

    private static final class StoredForumResource extends AbstractResource implements HttpResource {
        private final ForumStoredFileDTO file;
        private final String fileName;

        /**
         * 保存资源元数据，读取时才打开对象流。
         *
         * @param file 文件服务验证后的资源。
         * @param fileName 对外文件名。
         */
        private StoredForumResource(ForumStoredFileDTO file, String fileName) {
            this.file = file;
            this.fileName = fileName;
        }

        /** @return 对象存储资源说明。 */
        @Override
        public String getDescription() {
            return file.resource().getDescription();
        }

        /** @return 用于媒体类型识别的文件名。 */
        @Override
        public String getFilename() {
            return fileName;
        }

        /** @return 元数据中的文件长度，避免为探测大小而提前打开文件流。 */
        @Override
        public long contentLength() {
            return file.size();
        }

        /**
         * 打开附件输入流。
         *
         * @return 对象存储输入流。
         * @throws IOException 对象存储读取失败时抛出。
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return file.resource().getInputStream();
        }

        /** @return 沿用上传文件类型并禁止浏览器 MIME 嗅探的响应头。 */
        @Override
        public HttpHeaders getResponseHeaders() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.contentType()));
            headers.set("X-Content-Type-Options", "nosniff");
            return headers;
        }
    }
}
