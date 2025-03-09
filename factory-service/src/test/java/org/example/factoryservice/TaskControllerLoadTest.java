package org.example.factoryservice;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

import java.time.Duration;
import java.util.random.RandomGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.example.factoryservice.dto.TaskRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

@ExtendWith(LoadTestExtension.class)
public class TaskControllerLoadTest {
    private final Logger logger = LoggerFactory.getLogger(TaskControllerLoadTest.class);

    private static final String BASE_URL = "http://localhost:8085/v1";
    private static final int MAX_THREADS = 32;
    private static final double MAX_RPS = 20000.0;
    private static final Duration MAX_P99_RESPONSE_TIME = Duration.ofMillis(100);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    @Tag("loadtest")
    public void testCreateTaskEndpointPerformance() throws Exception {
        TestPlanStats stats = testPlan(
            rpsThreadGroup()
                .maxThreads(MAX_THREADS)
                .rampTo(1000.0, Duration.ofSeconds(5))
                .rampTo(5000.0, Duration.ofSeconds(5))
                .rampTo(10000.0, Duration.ofSeconds(10))
                .rampTo(MAX_RPS, Duration.ofSeconds(10))
                .children(
                    httpSampler("Create Task", BASE_URL + "/tasks")
                        .post((ctx) -> {
                            try {
                                return objectMapper.writeValueAsString(generateTaskRequest());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }, ContentType.APPLICATION_JSON)
                ),
            jtlWriter("target/jmeter/task-test-results.jtl"),
            htmlReporter("target/jmeter/task-report")
        ).run();

        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(MAX_P99_RESPONSE_TIME);
        Assertions.assertEquals(0, stats.overall().errorsCount());

        logger.info("avg response time: {} ms", stats.overall().sampleTime().mean());
        logger.info("99% percentile: {} ms", stats.overall().sampleTimePercentile99());
        logger.info("handled requests: {}", stats.overall().samplesCount());
    }

    @Test
    @Order(2)
    @Tag("loadtest")
    public void testCompleteTaskEndpointPerformance() throws Exception {
        TestPlanStats stats = testPlan(
            rpsThreadGroup()
                .maxThreads(MAX_THREADS)
                .rampTo(10.0, Duration.ofSeconds(5))
                .rampTo(50.0, Duration.ofSeconds(5))
                .rampTo(100.0, Duration.ofSeconds(10))
                .rampTo(MAX_RPS, Duration.ofSeconds(10))
                .children(
                    httpSampler("Complete Task", BASE_URL + "/tasks/" + RandomGenerator.getDefault().nextLong(1000) + "/complete")
                        .method(HTTPConstants.PATCH)
                        .contentType(ContentType.APPLICATION_JSON)),

            jtlWriter("target/jmeter/task-complete-test-results.jtl"),
            htmlReporter("target/jmeter/task-complete-report")
        ).run();

        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(MAX_P99_RESPONSE_TIME);
        Assertions.assertEquals(0, stats.overall().errorsCount());

        logger.info("avg response time: {} ms", stats.overall().sampleTime().mean());
        logger.info("99% percentile: {} ms", stats.overall().sampleTimePercentile99());
        logger.info("handled requests: {}", stats.overall().samplesCount());
    }

    private TaskRequestDto generateTaskRequest() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setMachineId(RandomGenerator.getDefault().nextLong(1000));
        dto.setStatus("PENDING");
        dto.setDescription(RandomStringUtils.random(50, true, true));
        return dto;
    }
}
