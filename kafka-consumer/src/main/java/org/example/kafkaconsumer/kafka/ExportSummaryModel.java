package org.example.kafkaconsumer.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportSummaryModel {
    private long activeTasks;
    private long completedTasks;
}
