# Healthper Backend

> **헬스 운동 기록 및 AI 피드백 서비스 백엔드**

운동 기록을 관리하고 AI가 분석해주는 REST API 서버입니다.  
세트별 무게·횟수·RPE를 기록하고, GPT 기반 AI가 운동 데이터를 분석해 맞춤형 피드백을 제공합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x |
| ORM | Spring Data JPA (Hibernate) |
| DB | MariaDB |
| 인증 | Spring Security + JWT (jjwt 0.12.3) |
| AI | OpenAI GPT-4o-mini (Claude 3.5 Sonnet 전환 가능) |
| 비동기 | Spring @Async |
| 기타 | Lombok, Jackson |

---

## 주요 기능

### 회원 인증
- 이메일/비밀번호 기반 회원가입 및 로그인
- JWT 액세스 토큰 발급 (만료: 30분)
- 닉네임·비밀번호 수정

### 운동 기록 관리
- 날짜별 운동 세션(Workout) 생성·조회·수정·삭제
- 세션당 복수의 운동 세트(WorkoutSet) 관리
  - 운동 종목(부위별), 세트 번호, 무게, 횟수 필수 입력
  - 세트 간 휴식 시간(초), RPE(1~10) 선택 입력
- 소프트 딜리트 기반 데이터 보존

### 운동 통계
- **주간 통계**: 이번 주 운동 횟수·총 볼륨·평균 RPE
- **부위별 볼륨**: 가슴/등/하체/어깨/팔 부위별 누적 볼륨
- **1RM 추세**: 종목별 추정 1RM 변화 추이
- **운동 일관성**: 목표 대비 달성률, 연속 운동일 계산

### AI 피드백
- **SESSION 타입**: 단일 운동 세션에 대한 즉시 피드백
- **WEEKLY 타입**: 최근 4주 데이터 기반 종합 분석
- 비동기 처리: 요청 즉시 `PENDING` 상태로 응답 → 완료 시 `COMPLETED`
- 피드백 항목: 요약, 분석, 루틴 제안, 영양 조언, 다음 주 목표

---

## 프로젝트 구조

```
src/main/java/com/helper/backend/
├── common/entity/        # BaseEntity (생성일, 수정일, 삭제일)
├── domain/
│   ├── user/             # 회원 인증 (UserController, MeController)
│   ├── exercise/         # 운동 종목 (ExerciseController)
│   ├── workout/          # 운동 기록 (WorkoutController)
│   ├── stats/            # 통계 (StatsController)
│   └── feedback/         # AI 피드백 (AiFeedbackController)
└── global/
    ├── config/           # SecurityConfig, AsyncConfig
    ├── exception/        # GlobalExceptionHandler, ErrorCode
    └── jwt/              # JwtUtil, JwtAuthenticationFilter
```

---

## 실행 방법

### 사전 요구사항
- Java 21+
- MySQL 8.x 실행 중
- OpenAI API Key (또는 Claude API Key)

### 1. 데이터베이스 생성

```sql
CREATE DATABASE helper
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2. 환경변수 설정

`src/main/resources/application-secret.yml` 파일을 생성합니다.

```yaml
ai:
  api:
    key: sk-xxxxxxxxxxxxxxxxxxxx   # OpenAI API Key

spring:
  datasource:
    password: your_mysql_password  # 필요 시 오버라이드
```

> `application.yml`의 `spring.profiles.include: secret` 설정으로 자동 로드됩니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IDE에서 `HelperApplication.java`를 직접 실행합니다.

서버가 기동되면 `http://localhost:8080`으로 접근 가능합니다.  
최초 실행 시 `import.sql`의 운동 종목 데이터가 자동으로 삽입됩니다.

### 4. 운영 환경 실행

```bash
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

> `application-prod.yml`에 운영 DB 및 보안 설정을 별도 관리하세요.  
> 운영 환경에서는 `ddl-auto`를 반드시 `validate` 또는 `none`으로 변경하세요.

---

## API 엔드포인트

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| POST | `/api/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) | 불필요 |
| GET | `/api/me` | 내 정보 조회 | 필요 |
| PUT | `/api/me` | 내 정보 수정 | 필요 |
| GET | `/api/exercises` | 운동 종목 목록 | 필요 |
| POST | `/api/workouts` | 운동 세션 생성 | 필요 |
| GET | `/api/workouts` | 운동 세션 목록 | 필요 |
| GET | `/api/workouts/{id}` | 운동 세션 상세 | 필요 |
| PUT | `/api/workouts/{id}` | 운동 세션 수정 | 필요 |
| DELETE | `/api/workouts/{id}` | 운동 세션 삭제 | 필요 |
| POST | `/api/workouts/{id}/sets` | 운동 세트 추가 | 필요 |
| PUT | `/api/workouts/{id}/sets/{setId}` | 운동 세트 수정 | 필요 |
| DELETE | `/api/workouts/{id}/sets/{setId}` | 운동 세트 삭제 | 필요 |
| GET | `/api/stats/weekly` | 주간 통계 | 필요 |
| GET | `/api/stats/volume` | 부위별 볼륨 | 필요 |
| GET | `/api/stats/1rm-trend` | 1RM 추세 | 필요 |
| GET | `/api/stats/consistency` | 운동 일관성 | 필요 |
| POST | `/api/feedback` | AI 피드백 요청 | 필요 |
| GET | `/api/feedback` | 피드백 목록 | 필요 |
| GET | `/api/feedback/{id}` | 피드백 상세 | 필요 |

> 인증이 필요한 요청은 헤더에 `Authorization: Bearer {accessToken}`을 포함해야 합니다.

---

## 트러블슈팅

### AI 피드백이 계속 PENDING 상태인 경우
`application-secret.yml`에 `ai.api.key`가 올바르게 설정되어 있는지 확인합니다.  
키가 없으면 비동기 처리 중 예외가 발생하여 `FAILED` 상태로 전환됩니다.

### Claude API로 전환하고 싶은 경우
`application.yml`의 AI 설정을 변경합니다.
```yaml
ai:
  api:
    url: https://api.anthropic.com/v1/messages
    model: claude-3-5-sonnet-20241022
```
`AiFeedbackService`의 `callAiApi()` 메서드 내 GPT 코드를 주석 처리하고 Claude 코드를 활성화합니다.

### 재시작 시 데이터가 초기화되는 경우
`application.yml`의 `ddl-auto`가 `create`로 설정되어 있어 재시작마다 테이블이 재생성됩니다.  
데이터를 유지하려면 `validate`로 변경하세요.
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

### MySQL 연결 오류 (`Communications link failure`)
- MySQL 서버가 실행 중인지 확인합니다.
- `application.yml`의 DB URL·포트·비밀번호가 올바른지 확인합니다.
- `helper` 데이터베이스가 생성되어 있는지 확인합니다.

### JWT 서명 오류 (`SignatureException`)
`application.yml`의 `jwt.secret` 값이 변경되면 기존 발급된 토큰이 모두 무효화됩니다.  
운영 환경에서는 `application-secret.yml`로 분리하여 고정 값을 유지하세요.

### 비동기 처리 시 `LazyInitializationException`
`@Async` 메서드는 별도 스레드에서 실행되므로 기존 트랜잭션의 영속성 컨텍스트가 분리됩니다.  
`processAiFeedback()`에 `@Transactional`이 선언되어 있는지 확인하세요.
