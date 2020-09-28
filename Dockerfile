FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

VOLUME /opt/logs
EXPOSE 8080
RUN mkdir -p /opt/logs

ENTRYPOINT ["java","-jar","/app.jar","--spring-boot.run.profiles=docker","--spring.profiles.active=docker"]