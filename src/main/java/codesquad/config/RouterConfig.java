package codesquad.config;

import codesquad.handler.*;
import codesquad.http.ContentType;
import codesquad.template.HtmlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static codesquad.http.Method.GET;

/**
 * http 요청을 매핑 설정입니다.
 */
public class RouterConfig {

    private final Logger logger = LoggerFactory.getLogger(RouterConfig.class);
    private final HtmlManager htmlManager = new HtmlManager();
    private final Map<URLMatcher, HttpHandler> handlerMap = new HashMap<>();

    public RouterConfig() {
        handlerMap.putAll(staticResourceHandlerMap());
        handlerMap.putAll(templateResourceHandlerMap());
    }

    public Map<URLMatcher, HttpHandler> getHandlerMap() {
        return handlerMap;
    }

    public void setRoute(List<RouteEntry> route) {
        for (RouteEntry entry : route) {
            handlerMap.put(entry.urlMatcher(), entry.httpHandler());
        }
    }

    private Map<URLMatcher, HttpHandler> staticResourceHandlerMap() {
        Map<URLMatcher, HttpHandler> staticResourceHandlerMap = new HashMap<>();
        String directory = "static";
        boolean isJar = getClass().getClassLoader().getResource(directory).toString()
                .startsWith("jar");
        if (isJar) {
            try {
                String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
                        .getPath();
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith("static/") && !entry.isDirectory()) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            byte[] bytes = inputStream.readAllBytes();

                            String urlTemplate = entryName.replaceFirst(directory, "");
                            URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                            HttpHandler httpHandler = new StaticResourceHandler(getContentType(urlTemplate), bytes);
                            staticResourceHandlerMap.put(urlMatcher, httpHandler);
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            try {
                Enumeration<URL> resources = getClass().getClassLoader().getResources(directory);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    try {
                        provideFile(new File(resource.getFile()), staticResourceHandlerMap, directory);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return staticResourceHandlerMap;
    }

    private void provideFile(File file, Map<URLMatcher, HttpHandler> map, String directory) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                provideFile(subFile, map, directory);
            }
        } else {
            String path = file.getPath().split(directory)[1];
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] bytes = inputStream.readAllBytes();

                String urlTemplate = path;
                URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                HttpHandler httpHandler = new StaticResourceHandler(getContentType(urlTemplate), bytes);
                map.put(urlMatcher, httpHandler);
            } catch (IOException e) {
                logger.error("Error reading from binary file: {}", path);
                throw new RuntimeException(e);
            }
        }
    }

    private Map<URLMatcher, HttpHandler> templateResourceHandlerMap() {
        Map<URLMatcher, HttpHandler> templateResourceMap = new HashMap<>();
        String directory = "templates";
        boolean isJar = getClass().getClassLoader().getResource(directory).toString()
                .startsWith("jar");
        if (isJar) {
            try {
                String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
                        .getPath();
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(directory + "/") && !entry.isDirectory()) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            byte[] bytes = inputStream.readAllBytes();

                            String urlTemplate = entryName.replaceFirst(directory, "");
                            URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                            HttpHandler httpHandler = new TemplateResourceHandler(getContentType(urlTemplate), bytes, htmlManager);
                            templateResourceMap.put(urlMatcher, httpHandler);
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            try {
                Enumeration<URL> resources = getClass().getClassLoader().getResources(directory);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    try {
                        provideFileTemplate(new File(resource.getFile()), templateResourceMap, directory);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return templateResourceMap;
    }

    private void provideFileTemplate(File file, Map<URLMatcher, HttpHandler> map, String directory) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                provideFileTemplate(subFile, map, directory);
            }
        } else {
            String path = file.getPath().split(directory)[1];
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] bytes = inputStream.readAllBytes();

                String urlTemplate = path;
                URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                HttpHandler httpHandler = new TemplateResourceHandler(getContentType(urlTemplate), bytes, htmlManager);
                map.put(urlMatcher, httpHandler);
            } catch (IOException e) {
                logger.error("Error reading from binary file: {}", path);
                throw new RuntimeException(e);
            }
        }
    }

    private ContentType getContentType(String fileName) {
        // extract file extension
        int dotIndex = fileName.lastIndexOf('.');
        String extension = fileName.substring(dotIndex + 1);

        // content type
        return switch (dotIndex) {
            case -1 -> ContentType.TEXT_PLAIN;
            default -> ContentType.of(extension);
        };
    }
}
