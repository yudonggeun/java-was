package codesquad.router.table;

import codesquad.config.RouterConfig;
import codesquad.context.SessionContextManager;
import codesquad.http.ContentType;
import codesquad.router.RouteTableRow;
import codesquad.router.handler.TemplateResourceHandler;
import codesquad.template.HtmlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//@Solo
public class TemplateTable {

    private final Logger logger = LoggerFactory.getLogger(TemplateTable.class);
    private final RouterConfig config;
    private final HtmlManager htmlManager;
    private final SessionContextManager sessionContextManager;

    public TemplateTable(RouterConfig config, HtmlManager htmlManager, SessionContextManager sessionContextManager) {
        this.config = config;
        this.htmlManager = htmlManager;
        this.sessionContextManager = sessionContextManager;

        try {
            this.config.addRouteTable(findTemplateResources());
        } catch (IOException | URISyntaxException e) {
            logger.error("Error finding template resources: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<RouteTableRow> findTemplateResources() throws IOException, URISyntaxException {
        var templateResources = new ArrayList<RouteTableRow>();
        String directory = "templates";
        boolean isJar = getClass().getClassLoader().getResource(directory).toString()
                .startsWith("jar");
        if (isJar) {
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
                        String html = new String(bytes);

                        String urlTemplate = entryName.replaceFirst(directory, "");
                        templateResources.add(RouteTableRow
                                .get(urlTemplate)
                                .handle(new TemplateResourceHandler(getContentType(urlTemplate), html, htmlManager, sessionContextManager))
                        );
                    }
                }
            }
            jarFile.close();
            return templateResources;
        } else {
            Enumeration<URL> resources = getClass().getClassLoader().getResources(directory);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                try {
                    provideFileTemplate(new File(resource.getFile()), templateResources, directory);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return templateResources;
    }

    private void provideFileTemplate(File file, List<RouteTableRow> templateResources, String directory) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                provideFileTemplate(subFile, templateResources, directory);
            }
            return;
        }

        String urlTemplate = file.getPath().split(directory)[1];
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();
            String html = new String(bytes);

            templateResources.add(RouteTableRow
                    .get(urlTemplate)
                    .handle(new TemplateResourceHandler(getContentType(urlTemplate), html, htmlManager, sessionContextManager))
            );
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", urlTemplate);
            throw new RuntimeException(e);
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
