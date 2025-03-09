package org.example.factoryservice;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoadTestExtension implements BeforeAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(LoadTestExtension.class);
    private static final String RESULTS_FOLDER = "target/jmeter-reports";

    @Override
    public void beforeAll(ExtensionContext context) {
        File reportsDir = new File(RESULTS_FOLDER);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        System.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", RESULTS_FOLDER);
    }
}
