### BUILDER IMAGE ###
FROM maven:3 as builder

COPY pom.xml /build/

RUN mvn --file build/pom.xml --batch-mode package

COPY src /build/src/

RUN mvn --file build/pom.xml --batch-mode --offline package -DskipTests \
	&& mkdir app \
	&& mv build/target/frontend-service-full.jar app/app.jar \
	&& rm -rf build


### PRODUCTION IMAGE ###
FROM openjdk:8-jre-alpine

COPY --from=builder app/app.jar app/app.jar

WORKDIR /app

CMD ["java", "-jar", "app.jar"]
