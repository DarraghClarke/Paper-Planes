FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/session-0.1.0-jar-with-dependencies.jar /session-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /session-0.1.0.jar service.session.Server "mongo"