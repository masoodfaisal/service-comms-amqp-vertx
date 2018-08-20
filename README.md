Using AMQP to communicate between services
------------------------------------------


## Setup
* Download and install docker
* Download and install vegeta


## Run Interconnect server
Run AMQP Interconnect locally
```bash
docker run -it -p 5672:5672 ceposta/qdr
```

Once the interconnect sever is running, you can run following to get some stats
```bash
docker ps
docker exec -it CONTAINER_ID /bin/bash
#inside container
qdstat -a
qdstat -c
qdstat -l

```

## Run applications
```bash
cd frontend 
mvn clean install
java -jar target/frontend-service-full.jar

```

In another terminal
```bash
cd frontend 
mvn clean install
java -jar target/backend-service-full.jar
```

In third terminal
```bash
 echo "GET http://localhost:8080/" | ./vegeta attack -duration=60s -rate=50 | tee results.bin | ./vegeta report
```