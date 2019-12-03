FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/auldfellas-1.0-jar-with-dependencies.jar /auldfellas-1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /auldfellas-1.0.jar service.auldfellas.Receiver ${CONTAINER_NAME}