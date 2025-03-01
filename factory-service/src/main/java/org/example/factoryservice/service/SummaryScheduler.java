package org.example.factoryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.factoryservice.model.ExportSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SummaryScheduler {
    private final Logger logger = LoggerFactory.getLogger(SummaryScheduler.class);

    private final TaskService taskService;
    private final String exportPath;
    private final ObjectMapper objectMapper;

    @Autowired
    public SummaryScheduler(
        TaskService taskService,
        @Value("${summary-scheduler.export-path}") String exportPath,
        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.taskService = taskService;
        this.exportPath = exportPath;
    }

    @Scheduled(fixedRate = 60000)
    public void exportSummaryToFile() {
        try {
            Files.createDirectories(Paths.get(exportPath));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String fileName = exportPath + "summary_" + timestamp.replace(":", "-") + ".txt";

            ExportSummary summary = taskService.generateSummary();
            Path file = Paths.get(fileName);
            Files.write(file, objectMapper.writeValueAsString(summary).getBytes());
        } catch (IOException e) {
            logger.error("Error during file export", e);
        }
    }
}
