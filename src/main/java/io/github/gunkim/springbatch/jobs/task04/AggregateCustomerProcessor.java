package io.github.gunkim.springbatch.jobs.task04;

import io.github.gunkim.springbatch.jobs.task04.model.Customer;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.ConcurrentHashMap;

public class AggregateCustomerProcessor implements ItemProcessor<Customer, Customer> {
    private static final String TOTAL_CUSTOMERS = "TOTAL_CUSTOMERS";
    private static final String TOTAL_AGES = "TOTAL_AGES";

    private final ConcurrentHashMap<String, Integer> aggregateCustomers;

    public AggregateCustomerProcessor(ConcurrentHashMap<String, Integer> aggregateCustomers) {
        this.aggregateCustomers = aggregateCustomers;
    }

    @Override
    public Customer process(Customer item) {
        aggregateCustomers.putIfAbsent(TOTAL_CUSTOMERS, 0);
        aggregateCustomers.putIfAbsent(TOTAL_AGES, 0);

        aggregateCustomers.put(TOTAL_CUSTOMERS, aggregateCustomers.get(TOTAL_CUSTOMERS) + 1);
        aggregateCustomers.put(TOTAL_AGES, aggregateCustomers.get(TOTAL_AGES) + item.age());

        return item;
    }
}
