# E-Shop Microservice Architecture

This is a demonstration project of a microservice architecture using Spring Boot, 
Spring Cloud, Docker and Maven. Work in progress.

## How to run

<code>mvn verify</code>  
<code>docker compose build</code>  
<code>docker compose run --rm start-dependencies</code>  
<code>docker compose up</code>

Note that integration tests use reusable Testcontainers to 
speed things up. You will have to manually remove the 
containers after the integration tests are complete.