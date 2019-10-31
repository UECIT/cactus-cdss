FROM maven:3-jdk-11 as deps
WORKDIR /app

COPY pom.xml .
RUN mvn -B -Dmaven.repo.local=/app/.m2 dependency:go-offline

FROM deps as build

COPY src src
RUN mvn -B -Dmaven.repo.local=/app/.m2 package

FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY --from=build /app/target/cdss-supplier-stub.war /app
COPY --from=build /app/target/classes/application.properties /app

ENTRYPOINT [ "java", "-jar", "cdss-supplier-stub.war", "application.properties" ]
EXPOSE 8080