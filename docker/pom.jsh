// Replace modules in the parent pom.xml so only one module exists
var pomPath = Path.of("pom.xml");
var xmlContent = Files.readString(pomPath);
var newXml = xmlContent.replaceAll("<modules>.*[\\s\\S]+.*</modules>", "<modules><module>"+System.getProperty("project")+"</module></modules>");
Files.writeString(pomPath, newXml);