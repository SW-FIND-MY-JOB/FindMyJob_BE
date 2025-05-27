# JDK 17을 베이스 이미지로 사용
FROM openjdk:17-jdk-slim

# JAR 복사
COPY build/libs/coverLetterService-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]