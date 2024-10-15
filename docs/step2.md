# 2주차 정리

## Spring Batch Model

- **Tasklet Model**
    - 단일 작업을 처리할 때 사용
- **Chunk Model**
    - 대용량 데이터를 처리할 때 사용하며, Chunk 단위로 데이터를 읽고, 가공하고, 쓰는 방식

## Architecture

![](/docs/img/image2.png)

- **Job**
    - 배치 처리의 단위로 여러 Step으로 구성됨
- **Step**
    - Job을 구성하는 단위로, Tasklet 또는 Chunk로 구성됨
- **JobLauncher**
    - Job을 실행하는 인터페이스로, Job 실행 시 `JobExecution`을 반환
- **JobRepository**
    - Job과 관련된 메타데이터를 저장하며, `JobExecution`과 `Step`을 관리
- **ItemReader**
    - Chunk 모델에서 사용, 데이터를 읽어오는 인터페이스
- **ItemProcessor**
    - Chunk 모델에서 사용, 데이터를 가공하는 인터페이스
- **ItemWriter**
    - Chunk 모델에서 사용, 데이터를 쓰는 인터페이스
- **Tasklet**
    - 단순하고 유연한 배치 처리를 수행하는 단위

## 관점에 따른 흐름

![](/docs/img/image3.png)

### 처리 흐름 관점

- **JobScheduler**가 배치를 트리거하면, **JobLauncher**가 **Job**을 실행
- Job 실행 시, **JobExecution**이 수행되고 **ExecutionContext** 정보를 활용
- Job은 자신에게 정의된 **Step**을 실행하며, StepExecution이 수행됨
- Step은 **Tasklet** 또는 **Chunk** 모델을 통해 처리
- Chunk 모델의 경우, **ItemReader**를 통해 소스 데이터를 읽고, **ItemProcessor**로 데이터를 가공한 뒤 **ItemWriter**를 통해 데이터를 저장 또는 파일로 기록

### Job 정보의 흐름 관점

- **JobLauncher**는 **JobRepository**를 통해 JobInstance 정보를 데이터베이스에 등록
- **JobExecution**을 통해 실행 정보를 데이터베이스에 저장
- Step 실행 시 **JobRepository**에 I/O 레코드와 상태 정보를 저장하며, Job이 완료되면 완료 정보가 저장됨

## 스프링 배치에서 저장되는 정보

### JobInstance

- JobInstance는 Job 이름과 전달된 파라미터를 정의
- 중단된 Job은 재실행 시 중단된 지점부터 재개 가능
- 재실행을 지원하지 않거나 성공적으로 완료된 Job은 중복 수행을 방지하기 위해 종료됨

### JobExecution / ExecutionContext

- **JobExecution**
    - 잡의 물리적인 실행을 나타내며, 동일한 Job이 여러 번 실행될 수 있음 (1:N 관계)
- **ExecutionContext**
    - 각각의 JobExecution에서 처리 단계 등 메타 정보를 공유하며, Serializable 객체로 저장됨

### StepExecution / ExecutionContext

- **StepExecution**
    - Step의 물리적 실행을 나타내며, Job은 여러 Step을 가질 수 있음 (1:N 관계)
- **ExecutionContext**
    - Step 내에서 공유되어야 하는 데이터를 저장하며, 여러 단계에 걸쳐 공유할 필요가 없는 데이터는 Step 단계 내의 ExecutionContext를 사용

### JobRepository

- **JobRepository**는 JobExecution, StepExecution 등 배치 실행 정보나 상태, 결과 정보를 데이터베이스에 저장
- 이 정보를 바탕으로 스프링 배치는 중단된 Job의 재실행 또는 중단된 지점에서 다시 시작할 수 있도록 지원
- 