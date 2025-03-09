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

import org.example.factoryservice.dto.MachineRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

@ExtendWith(LoadTestExtension.class)
public class MachineControllerLoadTest {
    private final Logger logger = LoggerFactory.getLogger(MachineControllerLoadTest.class);

    private static final String BASE_URL = "http://localhost:8085/v1";
    private static final int MAX_THREADS = 32;
    private static final double MAX_RPS = 20000.0;
    private static final Duration MAX_P99_RESPONSE_TIME = Duration.ofMillis(100);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    @Tag("loadtest")
    public void testMachineEndpointsPerformance() throws Exception {
        TestPlanStats stats = testPlan(
            rpsThreadGroup()
                .maxThreads(MAX_THREADS)
                .rampTo(1000.0, Duration.ofSeconds(5))
                .rampTo(3000, Duration.ofSeconds(5))
                .rampTo(4000.0, Duration.ofSeconds(10))
                .rampTo(MAX_RPS, Duration.ofSeconds(10))
                .children(
                    httpSampler("Create Machine", BASE_URL + "/machines")
                        .post((ctx) -> {
                            try {
                                return objectMapper.writeValueAsString(generateMachineRequest());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }, ContentType.APPLICATION_JSON)
                ),
            jtlWriter("target/jmeter/machine-test-results.jtl"),
            htmlReporter("target/jmeter/machine-report")
        ).run();

        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(MAX_P99_RESPONSE_TIME);

        logger.info("avg response time: {} ms", stats.overall().sampleTime().mean());
        logger.info("99% percentile: {} ms", stats.overall().sampleTimePercentile99());
        logger.info("handled requests: {}", stats.overall().samplesCount());
    }

    @Test
    @Order(2)
    @Tag("loadtest")
    public void testGetMachineEndpointsPerformance() throws Exception {
        TestPlanStats stats = testPlan(
            rpsThreadGroup()
                .maxThreads(MAX_THREADS)
                .rampTo(1000.0, Duration.ofSeconds(5))
                .rampTo(2500.0, Duration.ofSeconds(5))
                .rampTo(4000.0, Duration.ofSeconds(10))
                .rampTo(MAX_RPS, Duration.ofSeconds(10))
                .children(
                    httpSampler("Get Machine", BASE_URL + "/machines/" + RandomGenerator.getDefault().nextLong(1000))
                        .method(HTTPConstants.GET)
                        .contentType(ContentType.APPLICATION_JSON)),

            jtlWriter("target/jmeter/machine-test-results.jtl"),
            htmlReporter("target/jmeter/machine-report")
        ).run();

        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(MAX_P99_RESPONSE_TIME);

        logger.info("avg response time: {} ms", stats.overall().sampleTime().mean());
        logger.info("99% percentile: {} ms", stats.overall().sampleTimePercentile99());
        logger.info("handled requests: {}", stats.overall().samplesCount());
    }

    private MachineRequestDto generateMachineRequest() {
        MachineRequestDto dto = new MachineRequestDto();
        dto.setName(RandomStringUtils.random(16, true, true));
        dto.setStatus("RUNNING");
        return dto;
    }
}
