FROM eclipse-temurin:20-jdk-alpine
WORKDIR /
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]