# Server
REST API server in `Flask` with `PostgreSQL` db using `sqlalchemy`.

### Project setup 
Project has its own `.env` which is structured according to `.env.dist` file. Project has two `docker-compose.yml` files. `docker-compose-deploy.yml` is used only during deployment and `docker-compose.yml` is used during development. Development compose file has hotreload and is bound to local files.
### CI/CD setup 
CI/CD is configured to build `arm64` immages. To change this in `.gitlab-ci.yml` change all `--platform`  flags.

CI/CD has also set of following variables:
```
CI_REGISTRY: [registry.gitlab.com]
CI_REGISTRY_PASSWORD: [passwd]
CI_REGISTRY_USER: [user]
ENV_FILE: [.env.dist]
PROD_SERVER_IP: [domain]
SSH_PRIVATE_KEY: [pk]
SSH_ROOT_PRIVATE_KEY: [pk]
SSH_CONFIG: [
  Host [domain]
  ProxyCommand cloudflared access ssh --hostname %h
]
```

### Project dev execution

#### `pip3 install -r requirements.txt`

#### `python3 app.py`


### Project prod execution
Building the project:
#### `docker-compose build`
Running the built project with db and adminer:
#### `docker-compose up`

