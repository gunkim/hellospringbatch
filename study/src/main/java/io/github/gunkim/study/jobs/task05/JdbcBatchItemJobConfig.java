package io.github.gunkim.study.jobs.task05;

import io.github.gunkim.study.jobs.task05.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcBatchItemJobConfig {
    public static final int CHUNK_SIZE = 100;
    public static final String ENCODING = "UTF-8";
    public static final String JDBC_BATCH_WRITER_CHUNK_JOB = "jdbcBatchJob";

    private final DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<Customer> jdbcBatchWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO customer (name, age, gender) VALUES (:name, :age, :gender)")
                .itemSqlParameterSourceProvider(new CustomerItemSqlParameterSourceProvider())
                .build();
    }


    @Bean
    public Step jdbcBatchStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> customerFlatFileItemReader) {
        log.info("------------------ Init flatFileStep -----------------");
        return new StepBuilder("jdbcBatchStep", jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(customerFlatFileItemReader)
                .writer(jdbcBatchWriter())
                .build();
    }

    @Bean
    public Job jdbcBatchJob(Step jdbcBatchStep, JobRepository jobRepository) {
        log.info("------------------ Init flatFileJob -----------------");
        return new JobBuilder(JDBC_BATCH_WRITER_CHUNK_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jdbcBatchStep)
                .build();
    }
}