FROM maven:3-jdk-8-alpine as build
WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn install -DskipTests

FROM openjdk:8-jdk-alpine
WORKDIR /app
VOLUME /tmp
COPY --from=build /app/target/cdss-supplier-stub.war /app
COPY --from=build /app/target/classes/application.properties /app

ENTRYPOINT [ "java", "-jar", "cdss-supplier-stub.war", "application.properties" ]
EXPOSE 8080