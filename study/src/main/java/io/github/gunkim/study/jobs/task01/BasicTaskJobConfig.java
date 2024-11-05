package io.github.gunkim.study.jobs.task01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BasicTaskJobConfig {
    @Bean
    public Tasklet greetingTasklet() {
        return new GreetingTask();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, @Qualifier("greetingTasklet") Tasklet greetingTasklet) {
        log.info("------------------------ Step -------------------");

        return new StepBuilder("myStep", jobRepository)
                .tasklet(greetingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job myJob(Step step, JobRepository jobRepository) {
        log.info("------------------------ Job -------------------");

        return new JobBuilder("myJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}