Using AMQP to communicate between services
------------------------------------------


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