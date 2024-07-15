package codesquad.util.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceFileManager {

    public static InputStream getInputStream(String path) throws IOException {
        URL resource = ResourceFileManager.class.getClassLoader().getResource(path);

        if (resource.getProtocol().equals("jar")) {
            return resource.openStream();
        } else {
            return new FileInputStream(resource.getFile());
        }
    }
}
