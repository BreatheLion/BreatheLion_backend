FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Gradle wrapper와 build 파일들 먼저 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 빌드 실행
RUN ./gradlew bootJar --no-daemon

# 런타임 이미지
FROM openjdk:17-jre-slim

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]