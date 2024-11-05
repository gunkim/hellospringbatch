package io.github.gunkim.study.jobs.task04;

import org.springframework.batch.item.file.FlatFileFooterCallback;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerFooter implements FlatFileFooterCallback {
    private static final String TOTAL_CUSTOMERS = "TOTAL_CUSTOMERS";
    private static final String TOTAL_AGE = "TOTAL_AGES";

    private final ConcurrentHashMap<String, Integer> aggregateCustomers;

    public CustomerFooter(ConcurrentHashMap<String, Integer> aggregateCustomers) {
        this.aggregateCustomers = aggregateCustomers;
    }

    @Override
    public void writeFooter(Writer writer) throws IOException {
        writer.write("총 고객 수 : %d".formatted(aggregateCustomers.get(TOTAL_CUSTOMERS)));
        writer.write(System.lineSeparator());
        writer.write("총 나이 : %d".formatted(aggregateCustomers.get(TOTAL_AGE)));
    }
}
