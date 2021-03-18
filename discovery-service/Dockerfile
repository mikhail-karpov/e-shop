FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

WORKDIR "/app"

COPY ./target/discovery-service.jar .

CMD ["java", "-jar", "/app/discovery-service.jar"]

EXPOSE 8888