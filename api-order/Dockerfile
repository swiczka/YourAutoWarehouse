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
    ./mvnw dependency:go-offline -pl api-order -am

# Kopiujemy źródła i budujemy
COPY shared-common/src ./shared-common/src
COPY api-order/src ./api-order/src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -pl api-order -am

# === STAGE 2: Run ===
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=builder /app/api-order/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]