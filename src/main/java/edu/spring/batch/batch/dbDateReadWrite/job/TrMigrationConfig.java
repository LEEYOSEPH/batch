package edu.spring.batch.batch.dbDateReadWrite.job;

import edu.spring.batch.batch.listener.job.JobLoggerListener;
import edu.spring.batch.core.domain.accounts.AccountsRepository;
import edu.spring.batch.core.domain.orders.Orders;
import edu.spring.batch.core.domain.orders.OrdersRepository;
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
 * desc: 주문 테이블 -> 정산테이블로 이관
 * run : --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;

    private final AccountsRepository accountsRepository;


    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .listener(new JobLoggerListener())
                .start(trMigrationStep)
                .build();
    }
    @Bean
    @JobScope
    public Step trMigrationStep(Tasklet trMigrationTasklet) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Orders>chunk(5) // 5개의 데이터 단위로 처리
                .build();
    }

}
