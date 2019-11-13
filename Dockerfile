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

ADD https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem /app
RUN keytool -importcert -alias RDS_CA_Cert \
            -file /app/rds-combined-ca-bundle.pem \
            -keystore /app/rds-truststore \
            -storepass mypassword

COPY --from=build /app/target/cdss-supplier-stub.war /app
COPY --from=build /app/target/classes/application.properties /app

ENTRYPOINT [ "java", "-jar", "cdss-supplier-stub.war", "application.properties" ]
EXPOSE 8080