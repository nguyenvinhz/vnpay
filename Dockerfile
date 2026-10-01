# syntax=docker/dockerfile:1

FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

# Resolve dependencies separately so Docker can reuse this layer when only source changes.
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode verify

# Tomcat 9 is required because this application uses javax.servlet.*.
FROM tomcat:9.0-jdk17-temurin

RUN rm -rf "${CATALINA_HOME}/webapps/"*

# Deploy as the root context: https://<service>.onrender.com/
COPY --from=build /app/target/vnpay.war ${CATALINA_HOME}/webapps/ROOT.war
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN sed -i 's/\r$//' /usr/local/bin/docker-entrypoint.sh && chmod +x /usr/local/bin/docker-entrypoint.sh

ENV PORT=10000
EXPOSE 10000

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
