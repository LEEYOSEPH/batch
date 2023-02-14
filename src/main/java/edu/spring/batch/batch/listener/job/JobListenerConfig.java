package edu.spring.batch.batch.listener.job;

import edu.spring.batch.batch.validateParam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run : --spring.batch.job.names=listenerJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobListenerConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job listenerJob(Step listenerStep) {
        return jobBuilderFactory.get("listenerJob")
                .listener(new JobLoggerListener())
                .start(listenerStep)
                .build();
    }
    @Bean
    @JobScope
    public Step listenerStep(Tasklet listenerTasklet) {
        return stepBuilderFactory.get("listenerStep")
                .tasklet(listenerTasklet) // Tasklet 설정
                .build();
    }

    @Bean
    @StepScope
    public Tasklet listenerTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("=======================================================");
                log.info("Listener Job!!!!!!!!!!!!!!!!!!!!");
                log.info("=======================================================");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
