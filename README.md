# site backend

App written in Java for management of md documents. By default, this project needs psql and keycloak instances for it to
function. Docker compose for both can be found in site repo.

For keycloak setup in the`.env` file, set `ISSUER_URI` and `JWK_SET_URI` as a links to your Keycloak instance.
In the site repo set as following (`site` is name of realm.):

     ISSUER_URI=http://localhost:8080/realms/site
     JWK_SET_URI=http://localhost:8080/realms/site/protocol/openid-connect/certs

The app needs postgres to run. For which you need to spin up instance and in `.env` set `PROFILE=test` if you want to
use test db for development. Set `POSTGRES_USER` for admin username and `POSTGRES_PASSWORD` as admin password.

In `application.yml` set `spring.datasource.url` to url to you psql instance.

Eg. `url: jdbc:postgresql://localhost:5435/site`

init:

    $ cp .env.dist .env     

    $ mvn install

run:

    $ mvn spring-boot:run
