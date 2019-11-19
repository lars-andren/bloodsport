import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BloodsportTest {

    @Ignore
    @Test
    public void file_toString_toFile() throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get("credentials.csv"));
        String content = new String(encoded, StandardCharsets.UTF_8);

        System.out.println(content);

        FileUtils.writeStringToFile(new File("credentials_test.csv"), content, StandardCharsets.UTF_8);

    }

}
