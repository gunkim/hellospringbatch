package io.github.gunkim.study.jobs.task05.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String name;
    private int age;
    private String gender;
}
