package com.lokalise.persistence.repository;

import com.amazonaws.util.Throwables;
import com.lokalise.config.ApplicationConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.skife.jdbi.v2.logging.SLF4JLog.Level;
import org.slf4j.LoggerFactory;

@Slf4j
public class Repository<T> {
    private DataSource source;
    private static ConcurrentHashMap<String, HikariDataSource> hikariDataSourceConcurrentHashMap = new ConcurrentHashMap();
    private ApplicationConfiguration config;

    public Repository(ApplicationConfiguration config) {
        this.config = config;
        initializeConnectionPool(config);
    }

    protected <ReturnedType> ReturnedType withDBInterface(Class<T> dBInterfaceClass, Function<T, ReturnedType> sqlExecution) {
        initializeConnectionPool(this.config);
        DBI connection = new DBI(this.source);
        connection.setSQLLog(new SLF4JLog(LoggerFactory.getLogger(DBI.class), Level.valueOf(this.config.getValueAsString("JDBI_LOG_LEVEL","TRACE").toUpperCase())));
        T dbInterface = connection.open(dBInterfaceClass);

        ReturnedType var4;
        try {
            var4 = sqlExecution.apply(dbInterface);
        } catch (UnableToExecuteStatementException var8) {
            if (Throwables.getRootCause(var8) instanceof SocketTimeoutException) {
                log.error("Database socket timeout exception", var8);
            }

            throw var8;
        } finally {
            connection.close(dbInterface);
        }

        return var4;
    }

    private void initializeConnectionPool(ApplicationConfiguration config) {
        if (this.source != null) {return;}

        Integer dbPort = config.getValueAsInt("DB_PORT", 5432);
        String dbUsername = config.getValueAsString("DB_USERNAME");
        String dbPassword = config.getValueAsString("DB_PASSWORD");
        Integer dbPoolSize = config.getValueAsInt("DB_POOL_SIZE");
        Integer dbTimeout = config.getValueAsInt("DB_TIMEOUT", 1);
        initializeDBSource(config.getValueAsString("DB_HOST"), dbPort, config.getValueAsString("DB_NAME"), dbUsername, dbPassword, dbPoolSize, dbTimeout);
    }

    private void initializeDBSource(String dbHost, Integer dbPort, String dbName, String dbUsername, String dbPassword, Integer dbPoolSize, Integer dbTimeout) {
        if (this.config.getValueAsBoolean("USE_HIKARI_DB_POOL")) {
            this.source = getHikariDataSource(dbUsername, dbPassword, dbPoolSize, dbTimeout, dbHost, dbPort, dbName);
            log.info("initialized hikari db pool");
        } else {
            this.source = getPgPoolingDataSource(dbUsername, dbPassword, dbPoolSize, dbTimeout, dbHost, dbPort, dbName);
            log.info("initialized postgres db pool");
        }
    }

    private PGPoolingDataSource getPgPoolingDataSource(String dbUsername, String dbPassword, Integer dbPoolSize, Integer dbTimeout, String dbHost, Integer dbPort, String dbName) {
        PGPoolingDataSource pgPoolingDataSource = new PGPoolingDataSource();
        pgPoolingDataSource.setServerName(dbHost);
        pgPoolingDataSource.setPortNumber(dbPort);
        pgPoolingDataSource.setDatabaseName(dbName);
        pgPoolingDataSource.setUser(dbUsername);
        pgPoolingDataSource.setPassword(dbPassword);
        pgPoolingDataSource.setMaxConnections(dbPoolSize);
        pgPoolingDataSource.setSocketTimeout(dbTimeout);
        pgPoolingDataSource.setConnectTimeout(dbTimeout);
        return pgPoolingDataSource;
    }

    private HikariDataSource getHikariDataSource(String dbUsername, String dbPassword, Integer dbPoolSize, Integer dbTimeoutSeconds, String dbHost, Integer dbPort, String dbName) {
        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setServerName(dbHost);
        pgSimpleDataSource.setPortNumber(dbPort);
        pgSimpleDataSource.setDatabaseName(dbName);
        pgSimpleDataSource.setUser(dbUsername);
        pgSimpleDataSource.setPassword(dbPassword);
        pgSimpleDataSource.setSocketTimeout(dbTimeoutSeconds);
        pgSimpleDataSource.setConnectTimeout(dbTimeoutSeconds);
        String datasourceName = dbHost + "-" + dbName;
        HikariDataSource hikariDataSource = hikariDataSourceConcurrentHashMap.get(datasourceName);
        if (hikariDataSource != null) {
            return hikariDataSource;
        } else {
            hikariDataSource = new HikariDataSource();
            hikariDataSource.setDataSource(pgSimpleDataSource);
            hikariDataSource.setMaximumPoolSize(dbPoolSize);
            hikariDataSource.setPoolName(dbName);
            hikariDataSource.setConnectionTimeout((long)(dbTimeoutSeconds * 1000));
            if (this.config.getValueAsBoolean("HIKARI_EAGER_POOL_INIT", false)) {
                List<Connection> connectionList = new ArrayList();
                int size = Math.min(this.config.getValueAsInt("HIKARI_EAGER_POOL_SIZE", 2), dbPoolSize);

                for(int i = 0; i < size; ++i) {
                    try {
                        Connection connection = hikariDataSource.getConnection();
                        connectionList.add(connection);
                    } catch (SQLException var18) {
                        log.error("Error eager initializing database connection: {}", var18.toString());
                        break;
                    }
                }

                connectionList.forEach((conn) -> {
                    try {
                        conn.close();
                    } catch (SQLException var3) {
                        log.error("Error closing database connection: {}", var3.toString());
                    }

                });
                log.info("Hikari eager pool initialized");
            }

            hikariDataSourceConcurrentHashMap.put(datasourceName, hikariDataSource);
            return hikariDataSource;
        }
    }
}
