package scripts;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartAllDocker {


    public static void main(String[] args) throws IOException, InterruptedException {

        Common.configureLogger();


        String[] commands = new String[]{
                // Create network for our usage
                "docker network create --ipv6=false -d bridge MyBridgeNetwork",

                // Run H2 Database
                "docker container run -d --name h2-server --network=MyBridgeNetwork -d -p 9092:1521 -p 81:81 --mount type=volume,src=h2-data,dst=/opt/h2-data -e H2_OPTIONS=-ifNotExists oscarfonts/h2",

                // Run tracing and monitoring servers
                "docker container run -d --name zipkin-server --network MyBridgeNetwork -p 9411:9411 openzipkin/zipkin",
                "docker container run -d --name promtail-server --network MyBridgeNetwork -v $(PWD)/docker/promtail/promtail-config.yaml:/mnt/config/promtail-config.yaml -v logVolume:/var/log/services grafana/promtail:2.8.0 --config.file=/mnt/config/promtail-config.yaml",
                "docker container run -d --name loki-server --network MyBridgeNetwork -v  $(PWD)/docker/loki/loki-config.yaml:/mnt/config/loki-config.yaml -p 3100:3100 grafana/loki:2.8.0 --config.file=/mnt/config/loki-config.yaml",
                "docker container run -d --name prometheus-server --network MyBridgeNetwork -p 9090:9090 -v  $(PWD)/docker/prometheus/:/etc/prometheus/ prom/prometheus",
                "docker container run -d --name=grafana-server --network MyBridgeNetwork -p 3000:3000 -v  $(PWD)/docker/grafana/provisioning:/etc/grafana/provisioning -v  $(PWD)/docker/grafana/grafana.ini:/etc/grafana/grafana.ini -v  $(PWD)/docker/grafana/dashboards:/var/lib/grafana/dashboards grafana/grafana-oss",

                // Build Spring boot apps
                "docker buildx build --progress=plain -f ./docker/spring-boot/Dockerfile -t spring-petclinic-config-server --build-arg SERVICE_NAME=spring-petclinic-config-server .",
                "docker buildx build --progress=plain -f ./docker/spring-boot/Dockerfile -t spring-petclinic-discovery-server --build-arg SERVICE_NAME=spring-petclinic-discovery-server .",
                "docker buildx build --progress=plain -f ./docker/spring-boot/Dockerfile -t spring-petclinic-admin-server --build-arg SERVICE_NAME=spring-petclinic-admin-server .",
                "docker buildx build --progress=plain -f ./docker/spring-boot/Dockerfile -t spring-petclinic-customer-service --build-arg SERVICE_NAME=spring-petclinic-customer-service .",
                "docker buildx build --progress=plain -f ./docker/flutter-api-gateway/Dockerfile -t spring-petclinic-api-gateway --build-arg SERVICE_NAME=spring-petclinic-api-gateway .",

                // Manipulate some runtime configurations
                // Give permission to volume for correct user to write to logs files
                "docker container run --rm -v logVolume:/var/log/services busybox /bin/sh -c \"touch .initialized && chown -R 10000:10001 /var/log/services\"",

                // Run Spring boot apps
                "docker container run -d --name=config-server --network MyBridgeNetwork -p 7771:7771 -v logVolume:/workspace/app/log spring-petclinic-config-server"
        };

        Common.execute(commands);

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        String discoveryServerCommand = "docker container run -d --name=discovery-server --network MyBridgeNetwork -p 7772:7772 -v logVolume:/workspace/app/log --env CONFIG_SERVER_URL=http://config-server:7771/ spring-petclinic-discovery-server";

        Common.execute(new String[]{discoveryServerCommand});

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        commands = new String[]{
                "docker container run -d --name=admin-server --network MyBridgeNetwork -p 7776:7776 -v logVolume:/workspace/app/log --env CONFIG_SERVER_URL=http://config-server:7771/ --env DISCOVERY_SERVER_HOST=discovery-server spring-petclinic-admin-server",
                "docker container run -d --name=customers-service --network MyBridgeNetwork -p 7773:7773 -v logVolume:/workspace/app/log --env CONFIG_SERVER_URL=http://config-server:7771/ --env H2HOST=h2-server --env H2PORT=1521 --env DISCOVERY_SERVER_HOST=discovery-server --env ZIPKIN_URL=http://zipkin-server:9411/api/v2/spans --env SPRING_PROFILES_ACTIVE=default,h2 spring-petclinic-customer-service",
                "docker container run -d --name=api-gateway --network MyBridgeNetwork -p 7778:7778 -v logVolume:/workspace/app/log --env CONFIG_SERVER_URL=http://config-server:7771/ --env DISCOVERY_SERVER_HOST=discovery-server --env ZIPKIN_URL=http://zipkin-server:9411/api/v2/spans --env BACKEND_HOST=http://localhost:7778 spring-petclinic-api-gateway"
        };

        Common.execute(commands);

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        String[] testCommands = new String[]{
                "docker run --name=firefox -d --network MyBridgeNetwork -p 4444:4444 -p 7900:7900 --shm-size=2g selenium/standalone-firefox:latest",
                "docker container run --name=test --network MyBridgeNetwork --rm -v $(PWD):/opt/app -v m2:/root/.m2 -e BROWSER_HOST=firefox maven:3-eclipse-temurin-17-alpine /bin/sh -c \"cd /opt/app && mvn -pl spring-petclinic-test-service clean verify site -Dmaven.plugin.validation=VERBOSE\""
        };

        Common.execute(testCommands);
    }


}


class Common {

    // ANSI Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private static final Logger logger = Logger.getLogger(Common.class.getName());

    private Common() {
    }

    public static void configureLogger() throws IOException {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        FileHandler fileHandler = new FileHandler("java%u%g.log");
        SimpleFormatter formatter = new SimpleFormatter() {

            @Override
            public String format(LogRecord log) {
                String source;
                if (log.getSourceClassName() != null) {
                    source = log.getSourceClassName();
                    if (log.getSourceMethodName() != null) {
                        source += " " + log.getSourceMethodName();
                    }
                } else {
                    source = log.getLoggerName();
                }
                String message = formatMessage(log);
                String throwable = "";
                if (log.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    pw.println();
                    log.getThrown().printStackTrace(pw);
                    pw.close();
                    throwable = sw.toString();
                }
                return String.format("%2$s - %1$s: %3$s%4$s%n",
                        log.getLevel().getLocalizedName(),
                        source,
                        message,
                        throwable);
            }
        };
        consoleHandler.setFormatter(formatter);
        fileHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
    }

    public static void execute(String[] commands) throws IOException {
        for (String command : commands) {
            String processedCommand = modify(command);
            info(processedCommand);
            Process process = build(processedCommand).start();
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                logger.info("Process Interrupted: " + ex.getMessage());
                Thread.currentThread().interrupt();
            }

            // If the process is still running, destroy it
            if (process.isAlive()) {
                process.destroy();
            }

            if (process.exitValue() == 0) {
                infoColored("Success", ANSI_GREEN);
            } else {
                infoColored("Failure", ANSI_YELLOW);
            }
        }
    }

    public static ProcessBuilder build(String command) {
        if (command.isEmpty())
            throw new IllegalArgumentException("Empty command");

        String regex = "\"([^\"]*)\"|(\\S+)";
        Matcher m = Pattern.compile(regex).matcher(command);
        ArrayList<String> cmdArray = new ArrayList<>();
        while (m.find())
            cmdArray.add(m.group(1) == null ? m.group(2) : m.group(1));
        if (command.startsWith("docker"))
            return new ProcessBuilder().inheritIO().command(cmdArray).redirectErrorStream(true);
        else
            return new ProcessBuilder().command(cmdArray);
    }

    private static String modify(String command) {
        return command.replace("$(PWD)", System.getProperty("user.dir"));
    }

    private static void info(String message) {
        logger.info(message);
    }

    private static void infoColored(String message, String ansiColor) {
        logger.info(() -> ansiColor + message + ANSI_RESET);
    }

}
