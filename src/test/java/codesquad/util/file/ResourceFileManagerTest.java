package codesquad.util.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.fail;

class ResourceFileManagerTest {

    @Test
    public void readFile() {
        try {
            InputStream inputStream = ResourceFileManager.getInputStream("templates/index.html");
            System.out.println(new String(inputStream.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}