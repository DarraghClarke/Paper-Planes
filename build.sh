cd core
mvn install
cd ../gateway
mvn clean package
cd ../load-balancer
mvn clean package
cd ../session
mvn clean package