package org.example.kafkaconsumer.kafka;

import org.example.kafkaconsumer.mongodb.ExportSummary;
import org.example.kafkaconsumer.mongodb.ExportSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KafkaMessagingService {
    private final Logger logger = LoggerFactory.getLogger(KafkaMessagingService.class);

    private static final String TOPIC_EXPORT_SUMMARY = "${topic.export-summary}";
    private static final String KAFKA_CONSUMER_GROUP_ID = "${spring.kafka.consumer.group-id}";
    private final ExportSummaryRepository exportSummaryRepository;

    KafkaMessagingService(@Autowired ExportSummaryRepository exportSummaryRepository) {
        this.exportSummaryRepository = exportSummaryRepository;
    }

    @Transactional
    @KafkaListener(
        topics = TOPIC_EXPORT_SUMMARY,
        groupId = KAFKA_CONSUMER_GROUP_ID,
        properties = {"spring.json.value.default.type=org.example.kafkaconsumer.kafka.ExportSummaryModel"})
    public ExportSummaryModel createCallTransaction(ExportSummaryModel exportSummaryModel) {
        logger.debug("Message export summary consumed: {}", exportSummaryModel);
        exportSummaryRepository.save(mapToDocument(exportSummaryModel));
        return exportSummaryModel;
    }

    private ExportSummary mapToDocument(ExportSummaryModel exportSummaryModel) {
        return new ExportSummary(
            UUID.randomUUID().toString(),
            exportSummaryModel.getActiveTasks(),
            exportSummaryModel.getCompletedTasks(),
            LocalDateTime.now()
        );
    }
}
