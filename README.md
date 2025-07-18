### 서비스 (USER)페이지
- [GO USER PAGE](http://lietzsche.iptime.org:8080)

---

### 주식 및 Upbit(coin) 시세분석 및 Upbit 자동매매 소프트웨어
- spring-eureka: 통합 도구(MSA)
- spring-admin: 로그 확인 및 운영 도구
- openFeign: 통합 내부 및 외부 통신 도구
- spring-security 6

#### 특징
- 계산로직: 주식, upbit 메서드 팩토리 패턴으로 같은 로직을 통해 계산
- spring-admin(admin-server): 모니터링 프로젝트 로그인 시 core 프로젝트(trade-service)의 유저 중 ADMIN or MASTER 권한의 유저로만 인증하도록 설정

### docker 사용시 빌드 명령어(root폴더에서 실행)
- docker-compose up -d
