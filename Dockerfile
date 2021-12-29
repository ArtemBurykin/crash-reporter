FROM openjdk:11
ARG JAR_FILE=target/crash-reporter.jar
EXPOSE 8080
WORKDIR /
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
