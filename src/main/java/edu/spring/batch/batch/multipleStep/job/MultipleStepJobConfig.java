package edu.spring.batch.batch.multipleStep.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * desc: 주문 테이블 -> 정산테이블로 이관
 * run : --spring.batch.job.names=multipleStepJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MultipleStepJobConfig {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multipleStepJob (Step multipleStep1,Step multipleStep2,Step multipleStep3) {
        return jobBuilderFactory.get("multipleStepJob")
                .start(multipleStep1)
                .next(multipleStep2)
                .next(multipleStep3)
                .build();

    }

    @Bean
    @JobScope
    public Step multipleStep1() {
        return stepBuilderFactory.get("multipleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info("========================================");
                    log.info("step1");
                    log.info("========================================");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step multipleStep2() {
        return stepBuilderFactory.get("multipleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("========================================");
                    log.info("step2");
                    log.info("========================================");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.put("someKey","hello!!");

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step multipleStep3() {
        return stepBuilderFactory.get("multipleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("========================================");
                    log.info("step3");
                    log.info("========================================");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    log.info("===================================================");
                    log.info(executionContext.get("someKey")+": someKey");
                    log.info("===================================================");

                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
