# ---------- Build stage ----------
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 캐시 최적화: 의존성 먼저
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle --no-daemon dependencies || true

# 소스 복사 후 빌드
COPY src src
RUN gradle --no-daemon bootJar

# ---------- Runtime stage ----------
FROM openjdk:17-jdk-slim
WORKDIR /app

# 비루트 유저
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

# JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]