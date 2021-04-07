FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

WORKDIR "/app"

COPY ./target/order-service.jar .

CMD ["java", "-jar", "/app/order-service.jar"]

EXPOSE 8083