package com.heavybox.jtix.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    // TODO: set application properties from here.
    protected void loadProfile(final String profile) {
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            // Get a property
            String physThreads = properties.getProperty("physics2d.threads");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
