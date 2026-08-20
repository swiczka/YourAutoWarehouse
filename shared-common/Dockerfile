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
COPY shared-common/pom.xml ./shared-common/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -pl shared-common -am

# Kopiujemy źródła i budujemy
COPY shared-common/src ./shared-common/src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -pl shared-common -am

# === STAGE 2: Run ===
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=builder /app/api-warehouse/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]