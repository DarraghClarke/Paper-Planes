FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/gateway-0.1.0-jar-with-dependencies.jar /gateway-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /gateway-0.1.0.jar service.gateway.ChatEndpoint ${CONTAINER_NAME}