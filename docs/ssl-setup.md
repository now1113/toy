# Jenkins SSL 적용 및 도메인 접근 설정

## 개요

이 문서는 `jenkins.kimnow.site`에 HTTPS(SSL)를 적용하고 도메인으로 접근 가능하도록 설정하는 과정을 설명합니다.  
인증서는 Let's Encrypt의 `certbot`을 Docker 기반으로 자동 발급하며, `nginx`는 리버스 프록시 역할을 합니다.

> 본 문서는 실패했던 문제와 해결 과정을 함께 기록합니다.
> 
> SSL Termination 구조로 구성되어 있으며,
> 외부 사용자는 https://jenkins.kimnow.site로 접속하지만,
> 내부적으로는 Nginx가 SSL을 종료하고 Jenkins 컨테이너와는 HTTP로 통신합니다.
>
>이를 통해 SSL 인증서 관리와 암복호화 부하를 Nginx에 집중시키고,
>Jenkins는 별도의 SSL 설정 없이 운영이 가능합니다.

## 전제 조건

- Jenkins는 Docker로 실행 중 (내부 포트: 8088)
- `nginx`는 `toy-infra/nginx` 하위에서 구성됨
- 도메인: `jenkins.kimnow.site` (가비아에서 설정 완료)
- SSL: Let's Encrypt + Certbot 사용
- 인증서는 `toy.kimnow.site`로 통합 발급하여 여러 서브도메인에 공유 사용

---

## 전체 흐름 요약

```text
[사용자 브라우저]
⇅ HTTPS
[ Nginx (SSL 종료) ]
⇅ HTTP
[ Jenkins 컨테이너 (8088) ]
```

## Nginx 설정 단계별 정리

### 1단계: 인증서 발급 전 Nginx 설정 (80포트만)

```text
server {
    listen 80;
    server_name toy.kimnow.store jenkins.kimnow.site;

    location /.well-known/acme-challenge/ {
        alias /var/www/certbot/.well-known/acme-challenge/;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

```
> 인증서 발급 전에는 절대로 443 포트 관련 설정을 추가하면 안 됨. 인증서가 없으므로 nginx가 기동 실패함.

### 2단계: 인증서 발급 후 전체 Nginx 설정 (443 포함)

```text
# HTTP 포트 (80)
server {
    listen 80;
    server_name toy.kimnow.store jenkins.kimnow.site;

    location /.well-known/acme-challenge/ {
        alias /var/www/certbot/.well-known/acme-challenge/;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl;
    server_name jenkins.kimnow.site;

    ssl_certificate /etc/letsencrypt/live/jenkins.kimnow.site/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/jenkins.kimnow.site/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;

    location / {
        proxy_pass http://jenkins:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Port $server_port;
        proxy_set_header X-Forwarded-Host $host;

        proxy_redirect     off;
        proxy_http_version 1.1;
        proxy_request_buffering off;

        # Jenkins WebSocket 지원
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $http_connection;
    }
}

```

## 인증서 발급 (certbot-run.sh)

```bash
#!/bin/bash

EMAIL="now@example.com"
DOMAINS=(-d toy.kimnow.site)

CERT_DIR="./nginx/ssl"
WEBROOT_DIR="./nginx/www"

echo "인증용 디렉토리 생성 중..."
mkdir -p "$WEBROOT_DIR/.well-known/acme-challenge"

echo "인증서 발급 요청 중..."
docker run --rm \
  -v "$PWD/$CERT_DIR:/etc/letsencrypt" \
  -v "$PWD/$WEBROOT_DIR:/var/www/certbot" \
  certbot/certbot certonly \
  --webroot -w /var/www/certbot \
  --email "$EMAIL" \
  --agree-tos \
  --no-eff-email \
  --non-interactive \
  "${DOMAINS[@]}"

RESULT=$?

if [ $RESULT -eq 0 ]; then
  echo "인증서 발급 성공."
else
  echo "인증서 발급 실패."
fi
```

- 나중에 서브도메인 추가하고 싶을 땐 DOMAINS=(-d ... -d sub.kimnow.site) 추가만 하면 됨.

## 문제 회고

### 문제 1: 인증서 발급전에 nginx에 443 포트 설정이 있었음
- 인증서가 없는 상태에서 `ssl_certificate`를 참조하자 nginx가 부팅 실패함
- 해결: 인증 전에는 80포트만 설정

### 문제 2: `.well-known/acme-challenge` 경로에 접근 불가
- certbot은 해당 경로에 접근하여 인증하지만, 파일이 없거나 nginx root 설정이 잘못돼서 404 발생
- 해결: `mkdir -p ./nginx/www/.well-known/acme-challenge` 명시적 생성
- 테스트 스크립트 (`certbot-test.sh`)로 미리 서빙 가능 여부 확인

## 인증서 발급 실행 절차

### 스크립트 경로: toy-infra/certbot-run.sh

```bash
cd toy-infra
chmod +x certbot-run.sh
./certbot-run.sh
```

- `/nginx/ssl/live/toy.kimnow.site/` 하위에 인증서가 생성되면 성공
- `https://jenkins.kimnow.site` 접속 시 브라우저 자물쇠 아이콘 확인

## 인증서 자동 갱신

```bash
# 매주 월요일 새벽 3시 자동 갱신
0 3 * * 1 /home/ubuntu/toy-infra/certbot-run.sh >> /home/ubuntu/toy-infra/certbot.log 2>&1
```