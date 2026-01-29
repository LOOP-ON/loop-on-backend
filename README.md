# LOOP:ON Backend

## 📖 프로젝트 소개

본 프로젝트는 RESTful API 서버로 구축되었으며, 최신 Spring Boot 기술 스택을 기반으로 합니다.

---

## 👥 팀 멤버

| 항목 | 최승원 / 레이드 | 장예은 / 옌찌                                                  | 김경주 / 갱주                                                  | 박재호 / 조조                                                  | 박창현 / 조조                                                  |
|------|----------------|-----------------------------------------------------------|-----------------------------------------------------------|-----------------------------------------------------------|-----------------------------------------------------------|
| GitHub | [Seungwon-Choi](https://github.com/chltjsdl0119) | [jangyeeunee](https://github.com/jangyeeunee)                 | [StarvingOrange](https://github.com/starvingorange)                 | [joeeepark](https://github.com/joeeepark)                 | [shining-b-02](https://github.com/shining-b-02)                 |
| 프로필 | <img src="https://github.com/chltjsdl0119.png" width="100"/> | <img src="https://github.com/jangyeeunee.png" width="100"/> | <img src="https://github.com/starvingorange.png" width="100"/> | <img src="https://github.com/joeeepark.png" width="100"/> | <img src="https://github.com/shining-b-02.png" width="100"/> |
| 역할 | Backend | Backend                                                   | Backend                                                   | Backend                                                   | Backend                                                   |

## 🛠 기술 스택

### Core

- **Language**: Java 25
- **Framework**: Spring Boot 4.x
- **Build Tool**: Gradle 9.x

### Database & Storage

- **RDBMS**: MySQL 8.0+
- **NoSQL**: Redis (Caching)
- **Object Storage**: AWS S3 (Image & File Upload)
- **Migration**: Liquibase (Database Schema Management)

### Security & Auth

- **Security**: Spring Security
- **Authentication**: JWT (JSON Web Token)
- **OAuth2**: Social Login Support

### Documentation

- **API Docs**: Swagger / SpringDoc OpenAPI

### Testing

- **Test Framework**: JUnit 6, Mockito
- **Integration Test**: Spring Boot Test

---

## 📋 Git Convention

### Branch 전략

기본 브랜치

| 브랜치       | 역할               |
|-----------|------------------|
| `main`    | 실제 배포 가능한 안정 브랜치 |
| `develop` | 다음 배포를 위한 통합 브랜치 |

브랜치 네이밍

```
{type}/{issue-number}-{short-description}
```

type

| type     | 설명              |
|----------|-----------------|
| feature  | 신규 기능 개발        |
| bug      | 버그 수정           |
| refactor | 리팩토링 (기능 변경 없음) |
| chore    | 설정, 빌드, 문서 등    |
| hotfix   | 운영 긴급 수정        |

### Commit 메시지 규칙

형식

```
[type]: subject
```

type

| type     | 설명            |
|----------|---------------|
| feat     | 새로운 기능 추가     |
| fix      | 버그 수정         |
| refactor | 리팩터링          |
| docs     | 문서 추가/수정      |
| chore    | 빌드, 설정, 기타 작업 |
| test     | 테스트 코드        |

subject 규칙

- 현재형, 명령문
- 50자 이내
- 마침표 사용 금지

### Pull Request 규칙

PR 제목

```
[type] 이슈 제목
```

규칙

- 백엔드 리뷰어 지정
- approve 1개 이상 시 merge

---

## 📂 프로젝트 구조

프로젝트는 도메인형 디렉토리 구조를 따르고 있습니다.

규칙: 도메인별로 패키지를 분리하며, 의존성은 `Outer` → `Inner` 로만 향해야 합니다.

```
com.loopon
├── user                           ← [Domain] 도메인 기준 최상위 디렉토리
│   ├── domain                     ← [Core] 순수 비즈니스 모델 & 인터페이스
│   │   ├── User.java              (Entity: 핵심 로직 포함, Setter 금지)
│   │   ├── UserStatus.java        (Enum/VO)
│   │   │
│   │   ├── repository             (Repository Interface)
│   │   │   └── UserRepository.java
│   │   └── service                (Service Interface: 비즈니스 명세 정의)
│   │       ├── UserCommandService.java
│   │       └── UserQueryService.java
│   │
│   ├── application                ← [Business Logic] 유스케이스 구현 & 트랜잭션
│   │   ├── service                (Service Implementation)
│   │   │   ├── UserCommandServiceImpl.java
│   │   │   └── UserQueryServiceImpl.java
│   │   └── dto                    (Request/Response DTO)
│   │       ├── request
│   │       │   └── UserSignUpRequest.java
│   │       ├── command
│   │       │   └── UserSignUpCommand.java
│   │       └── response
│   │           └── UserResponse.java
│   │
│   ├── infrastructure             ← [Implementation] 기술 구현체 (DB, Ext. API)
│   │   ├── UserRepositoryImpl.java (QueryDSL 등 복잡한 구현)
│   │   └── UserJpaRepository.java  (Spring Data JPA)
│   │
│   └── presentation               ← [Web] API 계층
│       ├── docs                   (Swagger/API Docs)
│       │   └── UserApiDocs
│       └── UserController.java    (Endpoint)
│
├── journey                        ← 타 도메인 (동일 구조)
│   └── ...
│
└── global                         ← 전역 공통 (Exception, Common DTO, Config)
     ├── dto
     │   ├── CommonResponse.java
     │   └── PageResponse.java
     ├── exception
     │   ├── BusinessException.java
     │   └── ErrorCode.java
     └── config
         └── SecurityConfig.java
```

---

## API 문서 (API Documentation)

서버가 실행된 후, 아래 주소에서 API 문서를 확인할 수 있습니다.

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

## CI/CD 파이프라인 (CI/CD Pipeline)

- *추후 업데이트 예정*
