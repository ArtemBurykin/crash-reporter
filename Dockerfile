FROM clojure:openjdk-11-lein-buster as build
COPY . /app
WORKDIR /app
RUN lein uberjar

FROM openjdk:11
EXPOSE 8080
WORKDIR /
COPY --from=build /app/target/crash-reporter.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
# IMHERE: build it
