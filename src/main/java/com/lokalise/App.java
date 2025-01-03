package com.lokalise;

import com.lokalise.factory.ServerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        log.info("Starting service");

        var server = ServerFactory.createServer();
        server.startServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping service");
            server.stopServer();
        }));
    }
}
