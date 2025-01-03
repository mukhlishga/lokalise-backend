package com.lokalise.db.migration.flyway;

import com.lokalise.config.ApplicationConfiguration;
import org.flywaydb.core.Flyway;

public class Migration {

    public static void main(String[] args) {
        var configuration = new ApplicationConfiguration();

        var flyway = Flyway.configure().dataSource(
            String.format(
                "jdbc:postgresql://%s:%s/%s",
                configuration.getValueAsString("DB_HOST"),
                configuration.getValueAsString("DB_PORT"),
                configuration.getValueAsString("DB_NAME")
            ),
            configuration.getValueAsString("DB_USERNAME"),
            configuration.getValueAsString("DB_PASSWORD")
        ).load();

        flyway.migrate();
    }
}
