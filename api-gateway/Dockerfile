# === STAGE 1: Build ===
FROM eclipse-temurin:26-jdk AS builder
WORKDIR /app

# Kopiujemy parent POM i wrapper
COPY pom.xml ./
COPY mvnw mvnw.cmd ./
COPY .mvn ./.mvn

# Kopiujemy POM modułu i pobieramy zależności (cache)
COPY api-gateway/pom.xml ./api-gateway/
COPY api-order/pom.xml ./api-order/
COPY api-warehouse/pom.xml ./api-warehouse/
COPY api-forklift/pom.xml ./api-forklift/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -pl api-gateway -am

# Kopiujemy źródła i budujemy
COPY api-gateway/src ./api-gateway/src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -pl api-gateway -am

# === STAGE 2: Run ===
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]