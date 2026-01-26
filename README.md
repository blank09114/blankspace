# BLANKSPACE

**BLANKSPACE**는 **프로젝트 결과**, **소설**, 그리고 **프로젝트 과정에서의 기록**을 정리하기 위한 웹 플랫폼입니다.

🔗 [배포 주소](https://blankspace.io.kr/)


---

## 📌 프로젝트 개요
- **개발 형태**: Spring Boot 기반 웹 애플리케이션(MVC 구조)
- **개발 인원**: 약 2주
- **핵심 목표**
  - Spring Boot MVC+Thymeleaf 기반으로 **서비스를 end-to-end로 설계/구현/배포**
  - 인증/인가, 관리자 기능, 업로드, 로그 등 **운영 관점 기능**을 포함한 웹 플랫폼 구축
  - Docker + GitHub Actions로 **이미지 빌드/배포 자동화(CI/CD)** 파이프라인 구축

---

## 🛠️ 기술 스택
| 분야 | 사용 기술 |
|------|-----------|
| Front-end | HTML, CSS, JavaScript, Thymeleaf |
| Back-end | Java, Spring Boot, Spring Data JPA, Spring Security, Lombok |
| Database | MySQL |
| Infra | AWS EC2, AWS S3 |
| DevOps/CI·CD | Docker, GitHub Actions |
| Dev Tools | IntelliJ IDEA, VS Code, Git/GitHub |

---

## 🧩 주요 기능
- **인증/계정**
  - 메일 인증 기반 회원가입, 계정 찾기, 회원 탈퇴
  - 로그인/로그아웃, 비밀번호 변경
- **게시판/기록 관리**
  - 게시판별 게시글 CRUD, 카테고리 관리(관리자)
  - 썸네일 업로드 및 목록 카드 UI
  - TinyMCE 적용
- **소설/설정/회차 관리**
  - 소설 등록/수정/삭제/완결 처리(관리자)
  - 소설별 설정/회차 관리
- **댓글 시스템**
  - 게시글/회차 공용 댓글+대댓글(멘션) 지원
  - 권한에 따른 삭제(본인/관리자)
- **방명록**
  - 방명록 작성/삭제(작성자), 관리자 답변/삭제
- **관리 기능**
  - 회원 목록 조회(관리자), 사용자 차단/해제 및 사유 기록
  - 로그인 기록 조회(세션/로그아웃 추적+지역 정보)

---

## ⚙️ 기술적 구현
- **Spring Security 기반 접근 제어**
  - 비로그인 전용 페이지(로그인/회원가입 등), 로그인 필요 기능, 관리자 전용 기능을 URL/메서드 단위로 분리
- **보안/운영 관점 처리**
  - 비밀번호 변경 시 기존 세션 무효화(변경 시점 기준으로 강제 로그아웃)
  - 요청 쿨다운(간단한 레이트 리밋)으로 과도한 요청을 제한
- **로그인 기록(감사 로그)**
  - 로그인 시 IP/지역(GeoIP) 저장, 세션 종료 이벤트 기반 로그아웃 처리
  - 오래된 로그인 기록 자동 정리(스케줄러)
- **파일 업로드**
  - S3 업로드 지원(썸네일/에디터 이미지)
  - 클라이언트 선검증(확장자/용량 제한)+서버 단에서 재검증+업로드 후 URL 저장
- **프론트엔드**
  - 공통 fetch 래퍼(fetchJson/postJson), 토스트 UI, 페이지네이션 유틸 등 공통 모듈화
  - 목록 페이지는 템플릿 의존을 줄이고 DOM 생성 방식으로 렌더링
- **CI/CD&배포**
  - PR/Push(dev) 시 Docker 이미지 빌드 후 GHCR 푸시(CI)
  - CI 성공 시 AWS SSM으로 EC2에서 배포 스크립트 실행(CD)
  - Docker 컨테이너는 non-root 사용자로 실행

---

## 📁 디렉터리 구조
```
infected-requiem/
├── .github/workflows # CI/CD 파이프라인
├── src/main/
│   ├── java/kr/io/blankspace
│   │   ├── api/ # REST API
│   │   ├── controller/ # Thymeleaf View 렌더링용 MVC Controller
│   │   ├── service/ # 비즈니스 로직
│   │   ├── repository/ # JPA Interface
│   │   ├── entity/ # JPA Entity
│   │   ├── dto/ # 계층 간 데이터 전달용 DTO
│   │   ├── setting/ # JPA, Security, S3, Web, GeoIP, 로그인 기록 관련 설정
│   │   └── BlankspaceAppApplication.java # 실행 파일 
│   └── resources/
│       ├── templates/ # Thymeleaf 템플릿
│       └── static/ # 정적 리소스
└── build.gradle
