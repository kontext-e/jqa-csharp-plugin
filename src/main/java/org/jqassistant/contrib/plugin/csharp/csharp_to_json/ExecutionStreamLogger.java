package org.jqassistant.contrib.plugin.csharp.csharp_to_json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

class ExecutionStreamLogger extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionStreamLogger.class);

    private final InputStream inputStream;
    private final String type;

    ExecutionStreamLogger(InputStream inputStream, String type) {
        this.inputStream = inputStream;
        this.type = type;
    }

    public void run() {

        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException ioe) {
            if (Objects.equals(type, "ERROR")) {
                LOGGER.error("Error while processing execution stream", ioe);
            } else  {
                LOGGER.info("Error while processing execution stream", ioe);
            }
        }
    }
}
