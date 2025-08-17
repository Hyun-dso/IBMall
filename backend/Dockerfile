FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY backend/gradlew backend/gradle/ ./
COPY backend/build.gradle backend/settings.gradle ./
RUN ./gradlew --version || true
COPY backend/ ./
RUN chmod +x gradlew && ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Seoul
COPY --from=build /app/build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75"
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
