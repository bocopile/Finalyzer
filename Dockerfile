# 1단계: Gradle로 빌드하는 스테이지
FROM gradle:8.5.0-jdk17-alpine AS builder
WORKDIR /app

COPY . .
RUN gradle clean build -x test

# 2단계: 실행용 이미지
FROM azul/zulu-openjdk:17
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
