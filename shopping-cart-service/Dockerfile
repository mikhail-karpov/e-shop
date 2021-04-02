FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

WORKDIR "/app"

COPY ./target/shopping-cart-service.jar .

CMD ["java", "-jar", "/app/shopping-cart-service.jar"]

EXPOSE 8082