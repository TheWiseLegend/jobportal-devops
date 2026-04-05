FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline --no-transfer-progress

COPY src/ ./src/

RUN ./mvnw package -DskipTests --no-transfer-progress

FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

COPY --from=builder /app/target/jobportal-0.0.1-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]