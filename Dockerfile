# 베이스 이미지 선택
FROM openjdk:11-jre-slim
# 애플리케이션 JAR 파일을 이미지에 복사
COPY build/libs/*.jar /app/application.jar
# 운영 환경에서는 'prod' 프로파일을 사용
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/application.jar"]