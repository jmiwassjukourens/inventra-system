FROM eclipse-temurin:21-jdk-jammy

ARG JAR_FILE=target/catalog-1.0.0.jar

WORKDIR /catalog

COPY ${JAR_FILE} app.jar


ENV JAVA_OPTS=""
ENV CONFIG_PROFILE="local"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar ${CONFIG_PROFILE:+--spring.cloud.config.profile=$CONFIG_PROFILE}"]

# Build and run the Docker container
 #docker build -t catalog_image --no-cache --build-arg JAR_target/*.jar .

 #docker run -d -p 8080:8080 --name catalog_container catalog_image
