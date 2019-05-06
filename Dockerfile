FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

FROM openjdk:8-jdk-alpine
WORKDIR /app
VOLUME /tmp
COPY --from=build /workspace/app/target/cdss-supplier-stub-0.0.1-SNAPSHOT.war /app
COPY --from=build /workspace/app/target/classes/application.properties /app

ENTRYPOINT [ "java", "-jar", "cdss-supplier-stub-0.0.1-SNAPSHOT.war", "application.properties" ]