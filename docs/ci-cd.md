# Jenkins CI/CD 설정 문서 (toy 프로젝트)

## 목적

이 toy 프로젝트는 간단한 구조의 백엔드 서비스로, 다음과 같은 지속적 통합(CI) 및 배포(CD) 목표를 가지고 Jenkins를 통해 자동화한다:

| 브랜치 종류      | 동작 내용                                                 |
| ----------- | ----------------------------------------------------- |
| `feature/*` | JAR 빌드만 수행 (Docker 이미지 생성 및 배포 없음)                    |
| `main`      | JAR 빌드 + Docker 이미지 생성 + DockerHub Push + 컨테이너 자동 재배포 |

> Jenkins는 **Pipeline Script 방식의 단일 Job**으로 구성하며, `BRANCH_NAME`을 통해 분기 처리한다.


## 전체 흐름

```text
GitHub push
   ↓
Jenkins Webhook Trigger
   ↓
Pipeline 실행
   ↓
Git 체크아웃 및 브랜치 이름 추출
   ↓
./gradlew clean build -x test
   ↓
[main 브랜치일 경우]
   ↓
Docker Build → DockerHub Push → toy 컨테이너 재시작
```

## Jenkins 환경 구성

### Jenkins 실행 (Docker Compose 기반)
Jenkins는 Docker Compose를 사용해 다음과 같이 구성한다.

```yaml
services:
  jenkins:
    build:
      context: ./jenkins
      dockerfile: Dockerfile
    container_name: jenkins
    user: root
    ports:
      - "8088:8080"       # Jenkins Web UI
      - "50000:50000"     # 에이전트 포트
    volumes:
      - ./jenkins_home:/var/jenkins_home       # Jenkins 데이터 볼륨
      - /var/run/docker.sock:/var/run/docker.sock  # 호스트 도커 소켓 공유
    restart: unless-stopped
    networks:
      - toy-net
```

- `8088:8080`포트로 Jenkins UI 접근 가능 (`http://localhost:8088`)
- `docker.sock` 공유로 Jenkins 컨테이너가 **도커 빌드 및 컨테이너 재시작 직접 수행 가능**
- `toy-net` 네트워크는 다른 서비스 (**toy-app, nginx 등**)와 통신을 위한 내부 브릿지

> 도커 이미지는 `./jenkins/Dockerfile`을 통해 커스터마이징 가능 ex) 플러그인 미리 설치 등


## GitHub 연동 설정

### GitHub -> Jenkins Webhook 추가

- URL: `http://{your-server}/github-webhook/`
- Content-Type: `application/json`
- 이벤트: `push` 이벤트만 선택

### Jenkins -> Credentials에 다음 등록

| 항목           | 유형                            | ID                    |
| ------------ | ----------------------------- | --------------------- |
| GitHub PAT   | Username with password        | `github-pat`          |
| DockerHub ID | Secret text                   | `docker-hub-username` |
| DockerHub PW | Secret text                   | `docker-hub-password` |


### Jenkins 파일

```groovy
pipeline {
    agent any

    environment {
        APP_NAME = 'toy'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Extract Branch Name') {
            steps {
                script {
                    env.BRANCH_NAME = env.GIT_BRANCH?.split('/')[-1]
                    echo "Current Branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Login & Build') {
            steps {
                withCredentials([
                        string(credentialsId: 'docker-hub-username', variable: 'DOCKER_USERNAME'),
                        string(credentialsId: 'docker-hub-password', variable: 'DOCKER_PASSWORD')
                ]) {
                    script {
                        def imageTag = "${DOCKER_USERNAME}/${APP_NAME}:latest"
                        sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                            docker build -t ${imageTag} -f infra/Dockerfile .
                        """
                    }
                }
            }
        }

        stage('Docker Push') {
            when {
                expression {
                    return env.BRANCH_NAME == 'main'
                }
            }
            steps {
                withCredentials([
                        string(credentialsId: 'docker-hub-username', variable: 'DOCKER_USERNAME')
                ]) {
                    script {
                        def imageTag = "${DOCKER_USERNAME}/${APP_NAME}:latest"
                        sh "docker push ${imageTag}"
                    }
                }
            }
        }

        stage('Deploy on Local Jenkins Server') {
            when {
                expression {
                    return env.BRANCH_NAME == 'main'
                }
            }
            steps {
                withCredentials([
                        string(credentialsId: 'docker-hub-username', variable: 'DOCKER_USERNAME')
                ]) {
                    script {
                        def imageTag = "${DOCKER_USERNAME}/${APP_NAME}:latest"
                        sh """
                            echo "[1] Pulling latest image"
                            docker pull ${imageTag}

                            echo "[2] Stopping old container"
                            docker stop toy-app 2>/dev/null || true
                            docker rm toy-app 2>/dev/null || true

                            echo "[3] Running new container"
                            docker run -d --name toy-app \\
                              --network=toy-infra_toy-net \\
                              -p 8080:8080 \\
                              --restart=always \\
                              -e SPRING_PROFILES_ACTIVE=prod \\
                              -v /home/now/toy-secret:/config \\
                              ${imageTag}

                            echo "[4] Deployment complete"
                        """
                    }
                }
            }
        }
    }
}
```

## 프로젝트 인프라 디렉토리 구조

```text
toy/
├── build.gradle
├── infra/
│   └── Dockerfile
│   └── Jenkinsfile
├── src/
│   └── ...
```
