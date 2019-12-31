FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/message-service-test-0.1.0-jar-with-dependencies.jar /message-service-test-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /message-service-test-0.1.0.jar client.Application  ${CONTAINER_NAME}