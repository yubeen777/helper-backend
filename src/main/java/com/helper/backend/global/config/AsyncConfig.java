package com.helper.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}

/*
## AsyncConfig 역할


`@Async` 어노테이션을 쓰려면 Spring한테 "비동기 기능 켜줘" 라고 알려야 함

---

```java
@Configuration  // Spring 설정 파일이라는 표시
@EnableAsync    // 비동기 기능 활성화
public class AsyncConfig {
}
```

---

## 비유

```
@Async        → 이 메서드는 별도 스레드에서 실행해줘 (신청)
@EnableAsync  → 비동기 기능 사용 허가증 (허가)
```

허가증 없이 신청만 하면 `@Async`가 있어도 그냥 동기로 실행가능

---

## 없으면 어떻게 되나?

```java
@Async
public void processAiFeedback(...) { // AI API 호출 }
```

`AsyncConfig` 없으면 `@Async` 무시되고 동기로 실행됨 → 사용자가 AI 응답 올 때까지 기다려야 함.
*/