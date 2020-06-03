FROM maven:3-jdk-11 as build
WORKDIR /app

ARG GITHUB_USER
ARG GITHUB_TOKEN
ENV GITHUB_USER=$GITHUB_USER GITHUB_TOKEN=$GITHUB_TOKEN
COPY pom.xml settings.xml /app/
COPY src src
RUN mvn -B package -DskipTests --settings settings.xml

FROM openjdk:11-jre-slim
WORKDIR /app
VOLUME /tmp
COPY run.sh /app
RUN chmod +x run.sh
ENTRYPOINT [ "/app/run.sh" ]
EXPOSE 8080

COPY --from=build /app/target/cds-test-engine.war /app
