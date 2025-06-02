# JDK 17을 베이스 이미지로 사용
FROM openjdk:17-jdk-slim

# JAR 복사
COPY build/libs/correctionService-0.0.1-SNAPSHOT.jar /app.jar

# curl 설치
RUN apt-get update && apt-get install -y curl

ENTRYPOINT ["java", "-jar", "/app.jar"]