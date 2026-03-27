FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml pom.xml
COPY bussinesdomain/pom.xml bussinesdomain/pom.xml
COPY infraestructure/pom.xml infraestructure/pom.xml
COPY bussinesdomain/suppliers/pom.xml bussinesdomain/suppliers/pom.xml
COPY bussinesdomain/suppliers/src bussinesdomain/suppliers/src

RUN mvn -pl bussinesdomain/suppliers -am clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/bussinesdomain/suppliers/target/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
