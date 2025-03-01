package org.example.kafkaconsumer.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExportSummaryRepository extends MongoRepository<ExportSummary, String> {
}
