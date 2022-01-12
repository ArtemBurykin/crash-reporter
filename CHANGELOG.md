# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 0.2.0 - 2022-01-12
### Added
* Now the system writes crashes to the DB as well.

## 0.1.5 - 2022-01-06
### Added
* Now the system should be configured by the env variables.

## 0.1.4 - 2021-12-29
### Added
* Added docker-compose.yml to be able to use it as a docker container.

## 0.1.3 - 2021-12-07
### Added
* Added the ability to send extra information about crashes.

## 0.1.2 - 2021-02-04
### Added
* Added the ability to compile it like a jar file.

## 0.1.1 - 2021-01-27
### Added
* Added the ability to change the port of the server.

## 0.1.0 - 2021-01-27
### Added
* Added the ability to send crashes via the http end point.
* It uses RabbitMQ as a message broker.
* The system can be configured using the resources/config.json file.
