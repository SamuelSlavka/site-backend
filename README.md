# site backend

App written in Java for management of md documents. By default, this project needs psql and keycloak instances. Docker
compose for both can be found in the [site](https://github.com/SamuelSlavka/site) repo. After setting both env
files to common values (if applicable) and running the docker compose, this application will be fully
functional.

### Setup

For keycloak setup in the`.env` file, set `ISSUER_URI` and `JWK_SET_URI` as a links to your Keycloak instance.
In the site repo set as following (`site` is name of realm.):

    ISSUER_URI=http://localhost:8080/realms/site
    JWK_SET_URI=http://localhost:8080/realms/site/protocol/openid-connect/certs

The app needs postgres to run. If you want to
use test db for development in `.env` set `PROFILE=test`. Further also set `POSTGRES_USER` for admin username
and `POSTGRES_PASSWORD` as admin password in your db instance.

If you are not using local db instance change in `application.yml` property `spring.datasource.url` to url leading to
you psql instance.
Eg. `url: jdbc:postgresql://localhost:5435/site`

#### Before running you need to init and install:

    $ cp .env.dist .env     

    $ mvn install

#### To run the app use following:

    $ mvn spring-boot:run
