FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml pom.xml
COPY infraestructure/pom.xml infraestructure/pom.xml
COPY infraestructure/config-server/pom.xml infraestructure/config-server/pom.xml
COPY infraestructure/config-server/src infraestructure/config-server/src

RUN mvn -pl infraestructure/config-server -am clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/infraestructure/config-server/target/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8888

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
