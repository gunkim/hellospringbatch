# 1주차 정리

## application

- ~~`@EnableBatchProcessing` 어노테이션을 사용하여 배치 어플리케이션을 활성화할 수 있다.~~ 
  - Spring Boot 3.0 이상부턴 Spring Batch 5.0 이상 버전을 사용하기 때문에 `@EnableBatchProcessing` 어노테이션을 [사용하지 않아도 된다.](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide) 스프링 배치 자동 구성 기능은 Spring Boot 기능으로 편입되었다.
- `org.springframework.batch.core` classpath에 schema-*.sql로 각 플랫폼 DB에 맞는 init 스크립트를 제공한다.
- `spring.batch.jdbc.initialize-schema` 프로퍼티를 사용하여 스키마 초기화를 활성화할 수 있다. 기본 값은 `embedded`이다.

## meta-data schema

Spring Batch는 Job의 실행을 추적하기 위해 다음과 같은 테이블을 사용한다. 자세한 [DDL은 MySQL 기준](https://github.com/spring-projects/spring-batch/blob/main/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-mysql.sql) 여기서 DDL을 확인한다.

![](https://docs.spring.io/spring-batch/reference/_images/meta-data-erd.png)

1. BATCH_JOB_INSTANCE: Job의 실행을 추적하는 테이블
2. BATCH_JOB_EXECUTION_PARAMS: Job의 실행에 필요한 파라미터를 저장하는 테이블
3. BATCH_JOB_EXECUTION: Job의 실행을 추적하는 테이블
4. BATCH_STEP_EXECUTION: Step의 실행을 추적하는 테이블
5. BATCH_JOB_EXECUTION_CONTEXT: Job의 실행 컨텍스트를 저장하는 테이블
6. BATCH_STEP_EXECUTION_CONTEXT: Step의 실행 컨텍스트를 저장하는 테이블
7. BATCH_JOB_SEQ, BATCH_JOB_EXECUTION_SEQ, BATCH_JOB_SEQ, BATCH_STEP_EXECUTION_SEQ: 시퀀스 테이블
    1. 시퀀스 테이블은 Job의 실행 ID를 부여해서 실행 순서를 관리하기 위해 사용된다.

![](/docs/img/image1.png)

**Job**은 하나의 배치 작업을 의미하며, 해당 **Job**은 여러 번 **실행(Execution)** 될 수 있다. 각 **Job Execution**은 실행 시점마다 필요한 파라미터들을 지정할 수 있다.

**Job Execution**이 진행되는 동안, 여러 단계의 **Step Execution**이 함께 수행된다. 각 **Step Execution**은 그에 따른 **Step Execution Context**를 가지며, 이는 해당 Step의 실행 상태나 결과 등을 저장한다. 마찬가지로, **Job Execution**은 **Job Execution Context**를 가지며, 이는 전체 Job의 실행 상태를 기록한다. 최종적으로 **Job Execution Context**는 각 **Step Execution Context**의 상태에 따라 성공 또는 실패가 결정된다.
