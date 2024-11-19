package io.github.gunkim.study.jobs.task07;

import io.github.gunkim.study.jobs.task07.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper {
    @Select("""
            		SELECT id, name, age, gender
            		FROM customer
            		LIMIT #{_skiprows}, #{_pagesize}
            """)
    List<Customer> selectCustomers();
}
