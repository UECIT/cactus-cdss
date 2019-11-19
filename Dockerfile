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
COPY run.sh /app
RUN chmod +x run.sh
ENTRYPOINT [ "/app/run.sh" ]
EXPOSE 8080

COPY --from=build /app/target/cds-test-engine.war /app
