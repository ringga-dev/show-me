# ===== Build stage: JDK 21 =====
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Salin wrapper & config dulu (cache)
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts
COPY gradle.properties* ./
RUN chmod +x gradlew

# Non-daemon + izinkan auto-download toolchain (aman kalau plugin lain butuh)
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.java.installations.auto-download=true"

# Salin source
COPY src src

# Build
RUN ./gradlew clean bootJar -x test --no-daemon --stacktrace

# ===== Runtime stage: JRE 21 =====
FROM eclipse-temurin:21-jre-jammy
ENV TZ=Asia/Jakarta
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

RUN useradd -m spring && chown -R spring:spring /app
USER spring

EXPOSE 9091
ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","/app/app.jar"]
