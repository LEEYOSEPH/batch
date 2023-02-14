package edu.spring.batch.batch.sample.job;

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
*
* run : --spring.batch.job.names=sampleJob
*
* */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    /*
    * @JobScope, @StepScope 사용
    * - Bean의 생성시점이 스프링 애플리케이션이 실행되는 시점이 아닌 @JobScope/@StepScope가 명시된 메소드의 실행될 때까지 지연시킨 다는 것(LateBinding)을 의미
    *
    * 사용 이점
    * 1. JobParameter를 특정메서드가 실행하는 시점까지 지연시켜 할당시킬 수 있다.
    *   - 애플리케이션이 구동되는 시점이 아니라 비즈니스로직이 구현되는 어디든 JobParameter를 할당함으로 유연한 설계가 가능
    * 2. 병렬처리에 안전
    *   -
    *
    * */

    //JobBuilderFactory를 통해서 sampleJob을 생성
    @Bean
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
                .start(sampleStep())
                .build();
    }

    //StepBuilderFactory를 통해서 sampleStep 생성
    @Bean
    @JobScope
    public Step sampleStep() {
        return stepBuilderFactory.get("sampleStep")
                .tasklet(sampleTasklet()) // Tasklet 설정
                .build();
    }

    @Bean
    @StepScope
    public Tasklet sampleTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("=======================================================");
                log.info("Spring Batch Sample!!!!!!!!!!!!!!!!!!!!");
                log.info("=======================================================");
                return RepeatStatus.FINISHED;
            }
        };
    }


}
