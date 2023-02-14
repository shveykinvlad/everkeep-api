
# Everkeep
[![Build](https://github.com/shveykinvlad/everkeep-api/actions/workflows/build.yml/badge.svg)](https://github.com/shveykinvlad/everkeep-api/actions/workflows/build.yml)

### Notes service  
#### Technologies
* Java 17
* SpringBoot
* Spring Security
* Spring Data
* JWT
* PostgreSQL
* Liquibase
* TestContainers
* Docker
* Swagger
* Lombok
#### Functionality
* REST
* Validation
* Object mapping
* Registration
* Authorization
* Authentication
* Reset/update password
* Auth token
* Refresh token
* Verification token
* Mail sender
* Integration tests
* CRUD

## How to run locally
#### Prerequisites
* Docker
#### Steps
1. open project folder: `cd <path-to-project>`;
2. build image: `./gradlew bootBuildImage --imageName=everkeep-api`
3. set up environment variables in `.env` file;
    * INTEGRATION_UI_URL: URL to the root of a local instance of the [Everkeep fronted application](https://github.com/shveykinvlad/everkeep);
    * AUTHENTICATION_SECRET: secret key instance for use with [HMAC-SHA algorithms](https://www.rfc-editor.org/rfc/rfc7518#section-3.2)
4. run database and app in docker containers: `docker-compose up`;
5. stop database and app docker containers: `docker-compose down`;


