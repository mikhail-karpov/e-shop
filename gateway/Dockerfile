FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

WORKDIR "/app"

COPY ./target/gateway.jar .

CMD ["java", "-jar", "/app/gateway.jar"]

EXPOSE 8080