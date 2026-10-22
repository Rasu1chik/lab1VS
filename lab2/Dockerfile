FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY discovery-server discovery-server
COPY config-server config-server
COPY api-gateway api-gateway
COPY manager-service manager-service
COPY fantasy-service fantasy-service
RUN mvn -B package -DskipTests
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
ARG MODULE
COPY config-repo /config-repo
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-2.0.0.jar app.jar
ENTRYPOINT ["java","-XX:MaxRAMPercentage=70","-jar","/app/app.jar"]
