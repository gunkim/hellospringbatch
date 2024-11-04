package io.github.gunkim.springbatch.jobs.task04;

import io.github.gunkim.springbatch.jobs.task04.model.Customer;
import org.springframework.batch.item.file.transform.LineAggregator;

public class CustomerLineAggregator implements LineAggregator<Customer> {
    @Override
    public String aggregate(Customer item) {
        return "%s,%d".formatted(item.name(), item.age());
    }
}
