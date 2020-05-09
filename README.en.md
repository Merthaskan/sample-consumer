# Sample Consumer
This project created for demonstrate delayed message structure. The structure is shown below.\
![QueueStructure](./docs/Project.png)

# Starting
The project package structure, preconditions and installation are explained below.

## Preconditions
* [Java 11 SDK](https://openjdk.java.net/projects/jdk/11/)
* [Jetbrains IntelliJ](https://www.jetbrains.com/idea/)
* [RabbitMQ](https://www.rabbitmq.com/download.html)

## Project Package Structure
* Properties
* Configuration
* Consumer
* Service
* Recover

### Properties
This package includes models for defined in the application.yaml

### Configuration
This package includes **Spring Configuration** classes.

### Consumer
This package includes **RabbitMQ Listener** classes.

### Service
This package includes classes which implements business logic.

### Recover
This package includes **Message Recoverer** classes.

## Used Libraries
* Spring Boot
* Spring AMQP
* Lombok

## Installation
1. Clone project
2. Start RabbitMQ
3. Open intelliJ idea with project
4. Run ```mvn install```
5. Run project using intelliJ
