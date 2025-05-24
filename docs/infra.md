# toy 프로젝트 인프라 구성 문서

## 1. 개요

본 문서는 `toy` 프로젝트의 서버 및 배포 환경에 대한 구성을 정리한 문서입니다.

- **운영 환경**: 개인 서버(VMWare + Ubuntu 20.04.06 LTS)
- **배포 구조**: GitHub -> Jenkins -> DockerHub -> toy 서버 배포
- **사용 기술 스택**:
  - Spring Boot 3.4.5
  - Java 17
  - MySQL 8.0 (docker)
  - Redis (docker)
  - Nginx (docker)
  - Docker compose
  - GitHub + DockerHub

## 2. 전체 아키텍쳐

이미지 자리

- `toy.kimnow.stie` -> Spring 서비스 
- `jenkins.kimnow.site` -> Jenkins UI
- Nginx가 Reverse Proxy 및 SSL 종료 처리


## 3. 디렉토리 구조

```bash
toy-infra/
├── docker-compose.yml
├── .env
├── nginx/
│   ├── conf.d/
│   └── ssl/
├── jenkins/
│   ├── Dockerfile
│   └── jenkins_home/
├── mysql/
│   ├── init.sql
│   └── data/
├── redis/
├── spring/
│   └── Dockerfile
│   └── app.jar
```


## 4. 도메인 및 SSL 구성

- DNS: 가비아에서 `toy.kimnow.site`, `jenkins.kimnow.site` 설정
- 포트 포워딩
  - toy: 8080
  - jenkins: 8088
- Nginx + Let's Encrypt로 HTTPS 적용
- 인증서 갱신은 `certbot` 사용 (cron 등록 예정)

## 5. 배포 흐름 요약

```text
[GitHub Push]
    ↓ (Webhook)
[Jenkins 빌드 & Docker 이미지 생성]
    ↓
[DockerHub Private 저장소 Push]
    ↓
[서버 Pull & docker-compose 재시작]
```

## 6. 보안 및 관리

- Jenkins 계정 패스워드 및 SSH 키는 .secrets/ 디렉토리 별도 관리
- DockerHub Access Token은 Jenkins Credential에 등록 
- `.env`, `.secret.properties` 파일은 `.gitignore`에 등록하여 Git에 포함되지 않도록 설정