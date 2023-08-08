# site backend
App written in Java for management of md documents. By default, the project uses in memory database. This can be changed in `application.yml` in `spring.profiles.active` from `mem` to `psql`.

The app is configured to use keycloak for authentication. It can be found in site repo. 
For keycloak set `ISSUER_URI` and `JWK_SET_URI` to your preferred values.

init: 

    $ cp .env.dist .env     

    $ mvn install
to run:

    $ mvn spring-boot:run
