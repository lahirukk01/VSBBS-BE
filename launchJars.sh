nohup java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar > eureka-server/output.log &
nohup java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar > config-server/output.log &

nohup java -jar registration-service/target/registration-service-0.0.1-SNAPSHOT.jar > registration-service/output.log &
nohup java -jar account-service/target/account-service-0.0.1-SNAPSHOT.jar > account-service/output.log &
nohup java -jar beneficiary-service/target/beneficiary-service-0.0.1-SNAPSHOT.jar > beneficiary-service/output.log &
nohup java -jar loan-service/target/loan-service-0.0.1-SNAPSHOT.jar > loan-service/output.log &
nohup java -jar external-service/target/external-service-0.0.1-SNAPSHOT.jar > external-service/output.log &
nohup java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar > api-gateway/output.log &
