package io.github.gunkim.study.jobs.task04;

import io.github.gunkim.study.jobs.task04.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class CustomerFlatFileItemJobConfig {
    private static final int CHUNK_SIZE = 100;
    private static final String ENCODING = "UTF-8";
    private static final String FLAT_FILE_CHUNK_JOB = "flatFileChunkJob";
    private static final String FLAT_FILE_CHUNK_STEP = "flatFileChunkStep";

    private final ConcurrentHashMap<String, Integer> aggregateInfos = new ConcurrentHashMap<>();
    private final ItemProcessor<Customer, Customer> itemProcessor = new AggregateCustomerProcessor(aggregateInfos);

    @Bean
    public FlatFileItemReader<Customer> customerFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("FlatFileItemReader")
                .resource(new ClassPathResource("./customer.csv"))
                .encoding(ENCODING)
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("name", "age", "gender")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer> customerFlatFileItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriter")
                .resource(new FileSystemResource("./output/customer_new.csv"))
                .encoding(ENCODING)
                .delimited().delimiter("\t")
                .names("Name", "Age", "Gender")
                .append(false)
                .lineAggregator(new CustomerLineAggregator())
                .headerCallback(new CustomerHeader())
                .footerCallback(new CustomerFooter(aggregateInfos))
                .build();
    }

    @Bean
    public Step customerFlatFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        logInit("flatFileStep");
        return new StepBuilder(FLAT_FILE_CHUNK_STEP, jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(customerFlatFileItemReader())
                .writer(customerFlatFileItemWriter())
                .processor(itemProcessor)
                .build();
    }

    @Bean
    public Job customerFlatFileJob(Step customerFlatFileStep, JobRepository jobRepository) {
        logInit("flatFileJob");
        return new JobBuilder(FLAT_FILE_CHUNK_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerFlatFileStep)
                .build();
    }

    private void logInit(String component) {
        log.info("------------------ Init {} -----------------", component);
    }
}
