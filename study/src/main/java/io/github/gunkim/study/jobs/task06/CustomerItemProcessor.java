package io.github.gunkim.study.jobs.task06;

import io.github.gunkim.study.jobs.task06.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer item) {
        log.info("Item Processor ------------------- {}", item);
        return item;
    }
}