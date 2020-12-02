###############################################################################
# Step 1 : Builder image
FROM maven:3.5.4-jdk-11-slim AS build
RUN mkdir -p /usr/src/app/
WORKDIR /usr/src/app/

COPY .  /usr/src/app/
# Build a release artifact.
RUN mvn clean package -DskipTests

###############################################################################
# Step 2 : Runner image
FROM openjdk:11-slim

ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8

WORKDIR  /usr/local/

COPY --from=build /usr/src/app/target/app-0.0.1-SNAPSHOT.jar /usr/local/server.jar

# Run the web socket server on container startup.
ENTRYPOINT ["java","-jar","-Dserver.port=${PORT}","/usr/local/server.jar"]