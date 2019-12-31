cd core
mvn clean install
cd ../gateway
mvn clean package
cd ../load-balancer
mvn clean package
cd ../session
mvn clean package
cd ../message-service
mvn clean package
