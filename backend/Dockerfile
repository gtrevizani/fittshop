# ==============================================================================
# FittShop Backend - Multi-Stage Dockerfile (OpenJDK 21 LTS)
# DevSecOps Hardened: Non-root user, minimal footprint, healthcheck
# ==============================================================================

# Stage 1: Build & Package
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven Wrapper and POM first for efficient layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy sources and package JAR
COPY src/ src/
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
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/products || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
