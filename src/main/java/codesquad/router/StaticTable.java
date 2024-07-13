package codesquad.router;

import codesquad.config.RouterConfig;
import codesquad.http.ContentType;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Solo
public class StaticTable {

    private final Logger logger = LoggerFactory.getLogger(StaticTable.class);
    private final RouterConfig config;

    public StaticTable(RouterConfig config) {
        this.config = config;
        this.config.addRouteTable(findStaticResources());
    }

    private List<RouteTableRow> findStaticResources() {
        var staticResources = new ArrayList<RouteTableRow>();
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
                    if (entryName.startsWith(directory + "/") && !entry.isDirectory()) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            byte[] bytes = inputStream.readAllBytes();

                            String urlTemplate = entryName.replaceFirst(directory, "");
                            staticResources.add(RouteTableRow
                                    .get(urlTemplate)
                                    .handle(new StaticResourceHandler(getContentType(urlTemplate), bytes))
                            );
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
                        provideFile(new File(resource.getFile()), staticResources, directory);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return staticResources;
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

    private void provideFile(File file, List<RouteTableRow> staticResources, String directory) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                provideFile(subFile, staticResources, directory);
            }
            return;
        }

        String path = file.getPath().split(directory)[1];
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();

            String urlTemplate = path;
            staticResources.add(RouteTableRow
                    .get(urlTemplate)
                    .handle(new StaticResourceHandler(getContentType(urlTemplate), bytes))
            );
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", path);
            throw new RuntimeException(e);
        }
    }
}
