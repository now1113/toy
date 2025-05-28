# toy 프로젝트

> 백엔드 개발 공부를 자유롭게 시도해보는 토이 프로젝트입니다.  
> 하고 싶은 기술이나 아키텍처를 실험하고, 기록하고, 리팩토링하며 성장하는 공간입니다.


## 관련 문서

- [docs/infra.md](./docs/infra.md): toy 프로젝트의 서버 인프라 및 배포 환경 구성
- [docs/ci-cd.md](./docs/ci-cd.md): toy 프로젝트의 CI/CD 환경 구성
- [docs/ssl-setup.md](./docs/ssl-setup.md): 도메인 기반 SSL 인증 및 nginx 설정


> 새로운 기술을 적용하거나 리팩토링이 잦습니다. 실험적인 코드가 많습니다.

## 기술 스택

| 분야        | 사용 기술 |
|-----------|----------|
| Language   | Java 17 |
| Backend    | Spring Boot 3.4.5 |
| Infra      | Docker, Docker Compose, Jenkins |
| Database   | MySQL 8.0 (docker), Redis |
| DevOps     | GitHub, DockerHub, Nginx |
| Docs       | Swagger/OpenAPI, Markdown 기반 문서화


## 주요 디렉토리 구조 (수시 업데이트)

```bash
src/main/java/site/kimnow/toy/
├── home/           # 홈 페이지 컨트롤러 (화면 이동)
├── auth/           # 인증 도메인 (로그인, 토큰 재발급 등)
├── user/           # 사용자 도메인 (회원가입, 조회 등)
├── common/         # 공통 유틸, 예외, 상수, 응답 객체 등
├── jwt/            # JWT 관련 설정 및 유틸
├── redis/          # Redis 연동 서비스
├── security/       # Spring Security 설정, 필터, 핸들러 등
