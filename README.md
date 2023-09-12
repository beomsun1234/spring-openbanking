## spring boot을 이용한 금융결제원 오픈뱅킹 api사용해서 계좌조회 및 입금 출금 서비스 

<h3> 기능</h3>

-  ~~인증 및 토큰발급요청~~
-  ~~계좌조회 및 잔액조회~~
-  ~~간편송금(주거래은행)~~
-  ~~로그인~~

## 오픈뱅킹 properties 설정

오픈뱅킹 관련 properties 설정합니다.

        ......
        openbank:
          useCode : "{이용기관코드}"
          client-id: "{오픈뱅킹 client id}"
          client-secret: "{오픈뱅킹 client secret}"
          redirect-url: "frontend 주소"


## db 설정

      datasource:
        driver-class-name: 원하는 db
        username: 
        password:
        url: 

### postgres db

docker, docker-compose 가 설치되어있다고 가정합니다.

postgres db를 사용할 경우 프로젝트 루트에 있는 docker-compose 파일을 아래 명령어로 실행하면 됩니다.

    docker compose up -d

이후 datasource 설정해주면 됩니다.

## 기타

일반 사용자(센터인증 이용기관용)의 경우 사용자 인증시에만 계좌를 만들 수 있습니다.....

frontend에서 사용자 인증을 완료하고 redirect url로 전달받은 code를 backend에 요청하여 오픈뱅킹 사용자 토큰을 저장합니다. access token의 경우 사용기간이 90일이며, refresh token의 경우 95일 입니다. 중요한 정보이므로 서버에서 관리하고 요청시에만 db에서 조회하도록 구현했습니다.



<img width="1489" alt="스크린샷 2023-09-13 오전 12 36 51" src="https://github.com/beomsun1234/spring-openbanking/assets/68090443/eb905ddd-1f2c-41c3-b6b0-7a84fa85b04a">
-잔액조회-

