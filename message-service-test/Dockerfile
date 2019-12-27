FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/messageService-0.1.0-jar-with-dependencies.jar /messageService-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /gateway-0.1.0.jar service.messageService  ${CONTAINER_NAME}