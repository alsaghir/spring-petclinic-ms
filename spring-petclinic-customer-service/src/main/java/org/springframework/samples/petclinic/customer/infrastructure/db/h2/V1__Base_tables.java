package org.springframework.samples.petclinic.customer.infrastructure.db.h2;


import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class V1__Base_tables extends BaseJavaMigration {

    /**
     * First create database in specific location using h2 jar
     * and command
     * java -cp .\h2-2.1.214.jar org.h2.tools.Shell
     * <p>
     * Use JDBC URL with local file mode for db creationg like
     * jdbc:h2:file:D:/Code/db
     * jdbc:h2:file:~/db
     * </p>
     * <p>
     * Use the following command to start h2 server
     * java -cp .\h2-2.1.214.jar org.h2.tools.Server
     * </p>
     * <p>
     *
     * @param context The context relevant for this migration, containing things like the JDBC connection to use and the
     *                current Flyway configuration.
     * @throws SQLException for any sql error
     */
    @Override
    public void migrate(Context context) throws SQLException, IOException {

        Connection connection = context.getConnection();

         byte[] encoded = Files.readAllBytes(ResourceUtils.getFile("classpath:db/h2/V1__Initial_Setup.sql").toPath());
         String sqlDdl = new String(encoded, StandardCharsets.UTF_8);

        try (PreparedStatement statement = connection.prepareStatement(sqlDdl)) {
            statement.execute();
        }

        encoded = Files.readAllBytes(ResourceUtils.getFile("classpath:db/h2/initial_data.sql").toPath());
        String sqlDml = new String(encoded, StandardCharsets.UTF_8);

        try (PreparedStatement statement = connection.prepareStatement(sqlDml)) {
            statement.execute();
        }
    }
}
