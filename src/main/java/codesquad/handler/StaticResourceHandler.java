package codesquad.handler;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StaticResourceHandler implements HttpHandler {

    private static final File staticResource = new File("src/main/resources/static");
    private final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public boolean match(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        File file = new File(staticResource, request.path);
        if (file.isFile() && file.exists()) {
            HttpResponse response = HttpResponse.of(HttpStatus.OK);
            ContentType contentType = getContentType(file);
            response.addHeader("Content-Type", contentType.fullType);
            writeFileToBody(file, response);
            return response;
        }
        return HttpResponse.of(HttpStatus.NOT_FOUND);
    }

    private ContentType getContentType(File file) {
        // extract file extension
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String extension = fileName.substring(dotIndex + 1);

        // content type
        return switch (dotIndex) {
            case -1 -> ContentType.TEXT_PLAIN;
            default -> ContentType.of(extension);
        };
    }

    private void writeFileToBody(File file, HttpResponse response) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            response.setBody(sb.toString());

            logger.debug("Reading from file and writing to output");
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", file.getAbsolutePath());
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error reading from file: {}", file.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }
}
