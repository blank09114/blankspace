# 개발 메모

---

## 1. CI (Continuous Integration)

### 1.1 사용 기술

* GitHub Actions
* Java 17(Temurin)
* Gradle

---

### 1.2 트리거 조건

* `dev` 브랜치 push
* Pull Request 생성

---

### 1.3 CI 파이프라인 구성

1. Repository checkout
2. JDK 17 설정
3. Gradle 빌드 실행

---

## 2. CD (Continuous Deployment)

### 2.1 배포 환경

* AWS EC2 단일 인스턴스
* Docker 기반 애플리케이션 실행

---

### 2.2 배포 전략

* `dev` 브랜치 기준 배포
* GitHub Actions를 통한 배포 자동화

---

### 2.3 CD 흐름

1. `dev` 브랜치 push
2. CI 성공 확인
3. Docker 이미지 빌드
4. EC2로 이미지 배포
5. 기존 컨테이너 종료 후 신규 컨테이너 실행

---

## 3. AWS 비용 폭탄 방지 설계

### 3.1 AWS 안전장치

#### 3.1.1. 비용 감지

* AWS Budgets
  * 월 예산 알림: 60%/80%/100%
  * 일 예산 알림 추가
* Cost Anomaly Detection 활성화

#### 3.1.2. 보안

* 루트 계정 MFA 활성화
* IAM 최소 권한 원칙 적용
* EC2 인스턴스에 Access Key 직접 저장 금지

---

### 3.2 캐시 전략

#### 3.2.1 정적 리소스 캐시

* 대상: JavaScript/CSS/Image
* 저장소: S3
* Cache-Control: 1년
* 해시 기반 파일명 관리

---

#### 3.2.2 HTML 캐시

* 쿠키(세션) 없는 요청만 캐시
* TTL: 30초

---

#### 3.2.3 애플리케이션 내부 캐시

* 캐시 라이브러리: Caffeine
* 대상: 게시판, 소설, 방명록, 댓글
* TTL 정책
  * 목록: 30초
  * 상세 페이지: 60초

---

### 3.3 API 폭탄 방지

* 요청 제한
  * 일부 API에 대해 IP 기반 레이트 리밋
  * 계정 기반 요청 횟수 제한
* 입력 제한
  * 댓글/방명록 길이 제한
  * Request Body 크기 제한
  * 동일 내용 반복 제출 차단

---

### 3.4 RDS 비용 방지

* 개발/테스트 DB는 로컬 Docker 사용
* 운영 DB
  * 자동 백업 7일 보관
  * 스토리지 최대 용량 상한 설정
* HikariCP 커넥션 풀 - 인스턴스 스펙에 맞게 최대 커넥션 제한
* 조회 컬럼 인덱스 적용

---

### 3.5 EC2 비용 방지

* 로그 관리
  * CloudWatch Logs 최대 7일 보관
  * 예외 로그 무한 반복 출력 방지
* 인스턴스 정책
  * 단일 인스턴스 유지
  * 오토 스케일링 미사용
  
---

### 3.6 S3 비용 방지

* 퍼블릭 쓰기 권한 차단
* 업로드 파일 크기/확장자 제한
