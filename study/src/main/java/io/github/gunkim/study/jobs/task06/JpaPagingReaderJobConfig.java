package io.github.gunkim.study.jobs.task06;

import io.github.gunkim.study.jobs.task06.model.Customer;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaPagingReaderJobConfig {
    public static final int CHUNK_SIZE = 2;
    public static final String ENCODING = "UTF-8";
    public static final String JPA_PAGING_CHUNK_JOB = "JPA_PAGING_CHUNK_JOB";

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaPagingItemReader<Customer> customerJpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customerJpaPagingItemReader")
                .queryString("SELECT c FROM Customer c WHERE c.age > :age order by id desc")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(entityManagerFactory)
                .parameterValues(Map.of("age", 20))
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer> customerJpaFlatFileItemWriter() {

        return new FlatFileItemWriterBuilder<Customer>()
                .name("customerJpaFlatFileItemWriter")
                .resource(new FileSystemResource("./output/customer_new_v2.csv"))
                .encoding(ENCODING)
                .delimited().delimiter("\t")
                .names("Name", "Age", "Gender")
                .build();
    }


    @Bean
    public Step customerJpaPagingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        log.info("------------------ Init customerJpaPagingStep -----------------");

        return new StepBuilder("customerJpaPagingStep", jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(customerJpaPagingItemReader())
                .processor(new CustomerItemProcessor())
                .writer(customerJpaFlatFileItemWriter())
                .build();
    }

    @Bean
    public Job customerJpaPagingJob(Step customerJpaPagingStep, JobRepository jobRepository) {
        log.info("------------------ Init customerJpaPagingJob -----------------");
        return new JobBuilder(JPA_PAGING_CHUNK_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerJpaPagingStep)
                .build();
    }
}
