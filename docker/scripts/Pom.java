package scripts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Pom {
    public static void main(String[] args) throws IOException {
        // Replace modules in the parent pom.xml so only one module exists
        var pomPath = Path.of("pom.xml");
        var xmlContent = Files.readString(pomPath);
        var newXml = xmlContent.replaceAll("<modules>.*[\\s\\S]+.*</modules>", "<modules><module>" + args[0] + "</module></modules>");
        Files.writeString(pomPath, newXml);
    }
}