package scripts;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StartAllLocal {

    public static void main(String[] args) throws IOException, InterruptedException {

        Commons.configureLogger();

        String[] commands = new String[]{
                // Create network for our usage
                "docker network create --ipv6=false -d bridge MyBridgeNetwork",

                // Run H2 Database
                "docker container run -d --name h2-server --network=MyBridgeNetwork -d -p 9092:1521 -p 81:81 --mount type=volume,src=h2-data,dst=/opt/h2-data -e H2_OPTIONS=-ifNotExists oscarfonts/h2",

                // Run tracing and monitoring servers
                "docker container run -d --name zipkin-server --network MyBridgeNetwork -p 9411:9411 openzipkin/zipkin",
                "docker container run -d --name promtail-server --network MyBridgeNetwork -v  $(PWD)/docker/promtail/promtail-config.yaml:/mnt/config/promtail-config.yaml -v  $(PWD)/spring-petclinic-api-gateway/log:/var/log/services/spring-petclinic-api-gateway -v  $(PWD)/spring-petclinic-customer-service/log:/var/log/services/spring-petclinic-customer-service grafana/promtail:2.8.0 --config.file=/mnt/config/promtail-config.yaml",
                "docker container run -d --name loki-server --network MyBridgeNetwork -v  $(PWD)/docker/loki/loki-config.yaml:/mnt/config/loki-config.yaml -p 3100:3100 grafana/loki:2.8.0 --config.file=/mnt/config/loki-config.yaml",
                "docker container run -d --name prometheus-server --network MyBridgeNetwork -p 9090:9090 -v  $(PWD)/docker/prometheus/:/etc/prometheus/ prom/prometheus",
                "docker container run -d --name=grafana-server --network MyBridgeNetwork -p 3000:3000 -v  $(PWD)/docker/grafana/provisioning:/etc/grafana/provisioning -v  $(PWD)/docker/grafana/grafana.ini:/etc/grafana/grafana.ini -v  $(PWD)/docker/grafana/dashboards:/var/lib/grafana/dashboards grafana/grafana-oss",

                // Run Spring boot apps
                "mvn -pl spring-petclinic-config-server clean spring-boot:run",
        };

        Commons.execute(commands);

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));

        String discoveryServerCommand = "mvn -pl spring-petclinic-discovery-server clean spring-boot:run";

        Commons.execute(new String[]{discoveryServerCommand});

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));

        commands = new String[]{
                "mvn -pl spring-petclinic-admin-server clean spring-boot:run",
                "mvn -pl spring-petclinic-api-gateway clean spring-boot:run",
                "mvn -pl spring-petclinic-customer-service clean spring-boot:run -'Dspring-boot.run.profiles=default,h2'",
        };

        Commons.execute(commands);

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));

        String[] testCommands = new String[]{
                // Validate everything is working correctly by running
                // the test service and open spring-petclinic-test-service/target/site/index.html
                // in a browser
                "mvn -'Dglobal.host=localhost' -'Dbrowser.port=7778' -pl spring-petclinic-test-service clean verify site -'Dmaven.plugin.validation=VERBOSE'"
        };

        Commons.execute(testCommands);
    }
}

class Commons {

    // ANSI Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private static String oS = null;

    private static final Logger logger = Logger.getLogger(Commons.class.getName());

    private Commons() {
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
            if (process.exitValue() == 0) {
                infoColored("Success", ANSI_GREEN);
            } else {
                infoColored("Failure", ANSI_YELLOW);
            }
            // If the process is still running, destroy it
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    public static ProcessBuilder build(String command) {
        if (command.isEmpty())
            throw new IllegalArgumentException("Empty command");

        StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        if (command.startsWith("docker"))
            return new ProcessBuilder().inheritIO().command(cmdarray).redirectErrorStream(true);
        else
            return new ProcessBuilder().command(cmdarray);
    }

    private static String modify(String command) {
        // Current directory resolving
        StringBuilder modifiedCommand = new StringBuilder(command.replace("$(PWD)", System.getProperty("user.dir")));

        if (!modifiedCommand.toString().startsWith("docker")) {
            if (isWindows()) {
                return "cmd /c start PowerShell -NoExit -Command \"" + modifiedCommand + "\"";
            } else {
                throw new UnsupportedOperationException("OS is not currently supported");
            }
        } else {
            return modifiedCommand.toString();
        }

    }

    private static String getOsName() {
        if (oS == null) {
            oS = System.getProperty("os.name");
        }
        return oS;
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    private static void info(String message) {
        logger.info(message);
    }

    private static void infoColored(String message, String ansiColor) {
        logger.info(() -> ansiColor + message + ANSI_RESET);
    }

}
