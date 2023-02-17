package edu.spring.batch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ParallelBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job parallerJob() {
        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(step1())
                .build();
        Flow flow2 = new FlowBuilder<Flow>("flow3")
                .start(step2())
                .build();
        Flow flow3 = new FlowBuilder<Flow>("flow3")
                .start(step3())
                .build();
        Flow parallerStepFlow = new FlowBuilder<Flow>("parallerStepFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2, flow3)
                .build();

        return jobBuilderFactory.get("parallerFlowJob")
                .incrementer(new RunIdIncrementer())
                .start(parallerStepFlow)
                .next(step4())
                .end()
                .build();

    }
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    for (int i = 0; i < 10000; i++) {
                        System.out.println("step1 = " + i);
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    for (int i = 0; i < 10000; i++) {
                        System.out.println("step2 = " + i);
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    for (int i = 0; i < 10000; i++) {
                        System.out.println("step3 = " + i);
                    }
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("END STEP!!!!!!!!!!!!!!!!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
