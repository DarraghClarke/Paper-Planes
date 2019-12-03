FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/client-0.1.0-jar-with-dependencies.jar /client-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /client-0.1.0.jar service.client.Client ${CONTAINER_NAME}