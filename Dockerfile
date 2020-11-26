FROM openjdk:11-slim

ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8

WORKDIR  /usr/local/

COPY target/orgchart-0.0.1-SNAPSHOT.jar /usr/local/server.jar

EXPOSE 8080

# Run the web socket server on container startup.
ENTRYPOINT ["java","-jar","/usr/local/server.jar"]