FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

WORKDIR "/app"

COPY ./target/product-service.jar .

CMD ["java", "-jar", "/app/product-service.jar"]

EXPOSE 8081