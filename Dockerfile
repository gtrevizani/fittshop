# ==============================================================================
# FittShop Backend - Root Multi-Stage Dockerfile (OpenJDK 21 LTS)
# Ensures successful builds even when Render context is the repository root
# ==============================================================================

# Stage 1: Build & Package
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven Wrapper and POM from backend
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw

# Copy source code and package application
COPY backend/src/ src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime Container
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# DevSecOps Hardening: Create non-root unprivileged service account
RUN addgroup -S fittgroup && adduser -S fittuser -G fittgroup
USER fittuser:fittgroup

# Copy built JAR from builder stage
COPY --from=builder /workspace/target/fittshop-backend-1.0.0-SNAPSHOT.jar app.jar

ENV PORT=8080
# Container-aware memory flags: adapts to Render 512MB RAM without choking Metaspace
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
