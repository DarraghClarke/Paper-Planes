FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/baby-client-0.1.0-jar-with-dependencies.jar /baby-client-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /baby-client-0.1.0.jar client.Application ${CONTAINER_NAME}