FROM eclipse-temurin:20-jdk-alpine

RUN mkdir /app
COPY target/*.jar app.jar

WORKDIR /app

EXPOSE 8090
ENTRYPOINT ["java","-jar","/app.jar"]