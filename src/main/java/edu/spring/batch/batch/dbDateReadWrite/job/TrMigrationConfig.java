package edu.spring.batch.batch.dbDateReadWrite.job;

import edu.spring.batch.batch.listener.job.JobLoggerListener;
import edu.spring.batch.core.domain.accounts.Accounts;
import edu.spring.batch.core.domain.accounts.AccountsRepository;
import edu.spring.batch.core.domain.orders.Orders;
import edu.spring.batch.core.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public Step trMigrationStep(ItemReader trOrderReader, ItemProcessor trOrderProcessor,ItemWriter trOrdersWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5) // 5개의 데이터 단위로 처리
                .reader(trOrderReader) //iteamReadr 작성
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor) // 처리
                .writer(trOrdersWriter)
                .build();
    }

   /* @StepScope
    @Bean
    public RepositoryItemWriter<Accounts>trOrdersWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }*/

    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrdersWriter() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders,Accounts> trOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }



    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrderReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrderReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

}
