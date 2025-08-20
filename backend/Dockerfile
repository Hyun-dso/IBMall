# ===== Build stage =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# gradle wrapper 및 설정 복사
COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY gradlew ./
RUN chmod +x gradlew && ./gradlew --version || true

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean bootJar -x test

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Seoul
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]