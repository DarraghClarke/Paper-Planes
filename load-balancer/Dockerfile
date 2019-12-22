FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/balancer-0.1.0-jar-with-dependencies.jar /balancer-0.1.0.jar
CMD sleep 5 ; /usr/bin/java -cp /balancer-0.1.0.jar service.balancer.Application ${CONTAINER_NAME}