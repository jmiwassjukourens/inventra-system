FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY infraestructure ./infraestructure
COPY bussinesdomain ./bussinesdomain

RUN mvn -pl infraestructure/keycloak-adapter -am clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/infraestructure/keycloak-adapter/target/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
