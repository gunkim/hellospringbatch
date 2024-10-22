# 3주차 정리

## Chunk Model

- Chunk Model은 큰 데이터를 일정한 단위로 나누어 처리하는 방식이다.
- `ChunkOrientiedTasklet`은 청크 처리를 위한 Tasklet이다.
- Chunk에 포함될 데이터 레코드 수(Chunk size)는 `ChunkOrientiedTasklet` 클래스의 `commit-interval` 속성을 통해 설정한다.
- Chunk 단위 처리를 위해 `ItemReader`, `ItemProcessor`, `ItemWriter` 인터페이스를 사용한다.

## ItemProcessor

- `ItemProcessor`는 구현체를 작성하여 스프링 배치에 등록하면 된다.
- `ItemProcessor`는 `ItemReader`로부터 읽어온 데이터를 가공하거나 필터링하는 역할을 한다.
    - 여기서 가공이나 필터링 작업은 데이터 변경 혹은 외부 API 호출 등 다양한 비즈니스 로직을 수행할 수 있다.
- `ItemProcessor`는 Chunk Model에서 필수는 아니며, 선택적으로 사용할 수 있다.

## ItemReader

- `ItemReader`는 커스텀 구현이 가능하지만, 스프링 배치에서 제공하는 기본 구현체들을 이용할 수 있다.
- 구현체 종류:
    - `FlatFileItemReader`
        - 구조화되지 않은 파일(플랫 파일)을 읽어 들이는 Reader로, 대표적으로 CSV 파일을 처리한다.
        - 읽어 들인 데이터를 객체로 매핑한다.
    - `StaxEventItemReader`
        - StAX 기반으로 XML 파일을 읽어 들인다.
    - `JdbcPagingItemReader` / `JdbcCursorItemReader`
        - 데이터베이스에서 JDBC를 통해 데이터를 읽어 들인다.
        - 많은 데이터를 처리할 경우 모든 데이터를 메모리에 로드하지 않고 필요한 데이터만 페이징 방식으로 읽어 처리한다.
        - `JdbcPagingItemReader`는 `JdbcTemplate`을 이용하여 각 페이지마다 `SELECT SQL`을 나눠서 처리하고, `JdbcCursorItemReader`는 커서를 통해 단일 `SELECT SQL`로 데이터를 읽는다.
    - `MyBatisCursorItemReader` / `MyBatisPagingItemReader`
        - MyBatis를 사용해 데이터베이스의 레코드를 읽는다.
        - MyBatis의 Cursor와 Paging 방식의 차이는 JDBC 방식과 유사하다.
    - `JmsItemReader` / `AmqpItemReader`
        - 메시지를 `JMS` 혹은 `AMQP`에서 읽어 들인다.

## ItemProcessor 구현체

- `ItemProcessor` 또한 스프링 배치에서 기본 구현체들을 제공한다.
    - `PassThroughItemProcessor`
        - 아무 작업도 수행하지 않고, 입력된 데이터를 그대로 전달한다.
    - `ValidatingItemProcessor`
        - 입력된 데이터를 유효성 검사하는 Processor이다.
        - Spring Batch 전용 `Validator` 인터페이스를 구현해 사용할 수 있다.
    - `CompositeItemProcessor`
        - 여러 `ItemProcessor`를 순차적으로 실행하여 동일한 입력 데이터에 여러 처리를 적용할 수 있다.

## ItemWriter

- `ItemWriter`는 처리된 데이터를 외부로 출력하는 역할을 하며, 기본 구현체가 제공된다.
    - `FlatFileItemWriter`
        - 처리된 자바 객체를 CSV 파일과 같은 플랫 파일로 작성한다.
    - `StaxEventItemWriter`
        - 처리된 자바 객체를 XML 파일로 작성한다.
    - `JdbcBatchItemWriter`
        - JDBC를 사용해 자바 객체를 데이터베이스에 쓰기하는 Writer이다.
        - 내부적으로 `JdbcTemplate`을 사용한다.
    - `MyBatisBatchItemWriter`
        - MyBatis를 통해 자바 객체를 데이터베이스에 쓰기한다.
    - `JmsItemWriter` / `AmqpItemWriter`
        - 자바 객체를 `JMS` 혹은 `AMQP` 메시지로 전송하는 Writer이다.

## Tasklet Model

- Chunk Model은 큰 데이터를 분할해 `ItemReader` → `ItemProcessor` → `ItemWriter` 순으로 처리할 때 유용하다.
- 하지만 청크 단위로 나누는 것이 맞지 않을 때는 Tasklet Model을 사용하는 것이 적합하다.
    - 예를 들어, 한 번에 하나의 레코드를 읽어서 쓰는 경우 Tasklet Model이 더 적합하다.
- 사용자는 `Tasklet` 인터페이스를 구현하여 Tasklet Model을 사용할 수 있다.

## Tasklet 구현 클래스

- `SystemCommandTasklet`
    - 시스템 명령어를 비동기적으로 실행하는 Tasklet이다.
    - 명령어 속성에 수행할 시스템 명령을 지정할 수 있다.
    - 타임아웃 설정을 통해 명령 실행을 중단할 수 있다.
- `MethodInvokingTaskletAdapter`
    - POJO 클래스의 특정 메서드를 실행하기 위한 Tasklet이다.
    - `targetObject` 속성에 대상 클래스의 빈을 지정하고, `targetMethod` 속성에 실행할 메서드를 지정한다.
    - 반환값으로 `ExitStatus`를 설정해야 한다. 그렇지 않으면 반환값과 관계없이 "정상 종료(ExitStatus:COMPLETED)"로 간주된다.