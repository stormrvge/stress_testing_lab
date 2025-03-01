package org.example.kafkaconsumer.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collation = "export_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportSummary {
    @Id
    private String id;
    private Long activeTasks;
    private Long completedTasks;
    private LocalDateTime createdAt;
}
