# Jenkins CI/CD 설정 문서 (toy 프로젝트)

## 목적

본 프로젝트는 단순한 구조의 toy 프로젝트로, 다음과 같은 CI/CD 목표를 가진다.

- feature/* 브랜치: JAR 빌드만 수행
- main 브랜치: JAR 빌드 + Docker 이미지 생성 + DockerHub 푸시 + 컨테이너 배포
- Jenkins는 일반 Pipeline Job 형태로 구성하며, 조건 분기를 위해 브랜치명을 직접 추출



## 주요 동작 흐름

```text
GitHub Push → Jenkins Webhook Trigger →
  → Checkout
  → Branch 이름 추출
  → gradlew로 Build
  → Docker Build & Push (main일 경우만)
  → 컨테이너 재배포 (main일 경우만)
```
