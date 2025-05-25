# toy 프로젝트 CI/CD 설정 문서

## 1. 아키텍쳐 흐름

```text
GitHub (Push / PR)
↓ (Webhook)
Jenkins (CI 빌드)
↓ (Docker Build & Push)
DockerHub (이미지 저장소)
↓ (서버에서 Pull → 커테이너 재기동)
```

## Jenkins 구성

### Docker compose

```text
services:
  jenkins:
    image: jenkins/jenkins:lts
    ports:
      - "8080:8080"
    volumes:
      - ./jenkins:/var/jenkins_home
```

## Jenkins Credentials 등록

| ID                         | Kind                   | 내용                      |
| -------------------------- | ---------------------- | ----------------------- |
| `github-username-password` | Username with password | GitHub 계정 ID + PAT      |

- 등록 위치: `Mange Jenkins -> Credentials -> (Global)`