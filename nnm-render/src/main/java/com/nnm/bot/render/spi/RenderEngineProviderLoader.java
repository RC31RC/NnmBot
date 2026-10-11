package com.nnm.bot.render.spi;

import com.nnm.bot.render.RenderEngine;
import java.util.Locale;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.render.engine.OpenHtmlToPdfEngine;
import com.nnm.bot.render.engine.PlaywrightEngine;
import com.nnm.bot.render.config.RenderPluginProperties;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceConfigurationError;
import java.util.stream.Stream;

/** 负责内置与外部渲染提供者的发现、去重和确定性排序。 */
@Slf4j
@Component
public class RenderEngineProviderLoader {

    private static final int MAX_DISCOVERY_ERRORS = 100;

    private final List<URLClassLoader> classLoaders = new ArrayList<>();

    public List<RenderEngineProvider> load(RenderPluginProperties properties) {
        properties.validate();
        LinkedHashMap<String, RenderEngineProvider> providers = new LinkedHashMap<>();
        add(providers, new OpenHtmlToPdfProvider());
        add(providers, new PlaywrightProvider());
        if (properties.isEnabled() && properties.isExternalEnabled()) {
            loadExternal(properties.getDirectory(), providers);
        }
        return providers.values().stream()
                .sorted(Comparator.comparingInt(RenderEngineProvider::order)
                        .thenComparing(RenderEngineProvider::id))
                .toList();
    }

    private void loadExternal(Path directory, LinkedHashMap<String, RenderEngineProvider> providers) {
        if (!Files.isDirectory(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.list(directory)) {
            List<URL> jars = paths.filter(path -> path.getFileName().toString().endsWith(".jar"))
                    .sorted().map(this::toUrl).toList();
            if (jars.isEmpty()) {
                return;
            }
            URLClassLoader loader = new URLClassLoader(jars.toArray(URL[]::new),
                    RenderEngineProvider.class.getClassLoader());
            classLoaders.add(loader);
            Iterator<ServiceLoader.Provider<RenderEngineProvider>> discovered =
                    ServiceLoader.load(RenderEngineProvider.class, loader).stream().iterator();
            int discoveryErrors = 0;
            while (discoveryErrors < MAX_DISCOVERY_ERRORS) {
                try {
                    if (!discovered.hasNext()) {
                        break;
                    }
                    add(providers, discovered.next().get());
                } catch (RuntimeException | LinkageError | ServiceConfigurationError e) {
                    discoveryErrors++;
                    log.warn("Ignoring broken render provider from {}: {}", directory, e.getMessage());
                }
            }
            if (discoveryErrors == MAX_DISCOVERY_ERRORS) {
                log.warn("Stopped render provider discovery after {} errors from {}",
                        MAX_DISCOVERY_ERRORS, directory);
            }
        } catch (Exception e) {
            log.warn("Failed to load render providers from {}: {}", directory, e.getMessage());
        }
    }

    private URL toUrl(Path path) {
        try {
            return path.toUri().toURL();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid render plugin path: " + path, e);
        }
    }

    private void add(LinkedHashMap<String, RenderEngineProvider> providers, RenderEngineProvider provider) {
        if (ObjectUtil.isNull(provider)) {
            log.warn("Ignoring null render provider");
            return;
        }
        try {
            String rawId = provider.id();
            if (StrUtil.isBlank(rawId)) {
                log.warn("Ignoring render provider with blank id");
                return;
            }
            String id = rawId.trim().toLowerCase(Locale.ROOT);
            RenderEngineProvider registered = new RegisteredProvider(id, provider.order(), provider);
            if (ObjectUtil.isNotNull(providers.putIfAbsent(id, registered))) {
                log.warn("Ignoring duplicate render provider id [{}]", id);
            }
        } catch (RuntimeException | LinkageError e) {
            log.warn("Ignoring invalid render provider [{}]: {}", provider.getClass().getName(), e.getMessage());
        }
    }

    @PreDestroy
    void closeClassLoaders() {
        for (URLClassLoader classLoader : classLoaders) {
            try {
                classLoader.close();
            } catch (IOException e) {
                log.debug("Failed to close render plugin class loader: {}", e.getMessage());
            }
        }
        classLoaders.clear();
    }

    private static final class OpenHtmlToPdfProvider implements RenderEngineProvider {
        @Override
        public String id() {
            return "openhtmltopdf";
        }

        @Override
        public int order() {
            return 20;
        }

        @Override
        public boolean isAvailable(RenderEngineProviderContext context) {
            return true;
        }

        @Override
        public RenderEngine create(RenderEngineProviderContext context) {
            return new OpenHtmlToPdfEngine(context.templateBaseUri(), context.fontPaths());
        }
    }

    private static final class PlaywrightProvider implements RenderEngineProvider {
        @Override
        public String id() {
            return "playwright";
        }

        @Override
        public int order() {
            return 10;
        }

        @Override
        public boolean isAvailable(RenderEngineProviderContext context) {
            return new PlaywrightEngine(context.chromiumArgs()).isAvailable();
        }

        @Override
        public RenderEngine create(RenderEngineProviderContext context) {
            return new PlaywrightEngine(context.chromiumArgs());
        }
    }

    private record RegisteredProvider(String id, int order, RenderEngineProvider delegate)
            implements RenderEngineProvider {

        @Override
        public boolean isAvailable(RenderEngineProviderContext context) {
            return delegate.isAvailable(context);
        }

        @Override
        public RenderEngine create(RenderEngineProviderContext context) {
            return delegate.create(context);
        }
    }
}
