package codesquad.util.file;

import codesquad.config.RouterConfig;
import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.router.RouteTableRow;
import codesquad.util.scan.Solo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Solo
public class FileStorageManager {

    public final String storagePath = "./appdata/";
    public final RouterConfig config;

    public FileStorageManager(RouterConfig config) {
        this.config = config;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void saveFile(ContentType contentType, String filename, byte[] content) {
        // saveFile
        File file = new File(storagePath + filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // register router table
        config.addRouteTable(RouteTableRow.get("/image/" + filename).handle(request -> {
            try (FileInputStream input = new FileInputStream(storagePath + filename)) {
                byte[] fileContent = input.readAllBytes();
                HttpResponse response = HttpResponse.of(HttpStatus.OK);
                response.setContentType(contentType);
                response.setBody(fileContent);
                return response;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
