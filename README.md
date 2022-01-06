# crash-reporter

Another crash reporter. Allows a user to send a crash report via the end point (HTTP).
Later the crash is added to a rabbitMQ queue to be sent to a slack channel.

## Requirements

* RabbitMQ (2x or 3x)
* JDK >8

## Usage with Docker

### Production
To use in the production environment you first need to specify all the environment variables from `.env.dist` in `.env`,
Then it is possible to use docker-compose:
```bash
docker-compose -f docker-compose.yml up -d --build
```
It will build the app first, and then run it.

### Dev
To use it during development, you need to specify the APP_PORT variable, and then run docker-compose:
```bash
docker-compose -f docker-compose.dev.yml up -d --build
```

### Configuration
You can configure the system by using ```.env```.

## Usage without Docker
To use it you need to set few environment variables.

```
# The port for the server to accept requests
INTERNAL_PORT=...
RABBITMQ_USER=guest
RABBITMQ_PWD=guest
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
SLACK_WEBHOOK=...
```

Then it can be launched through lein in the dev environment, or built and run as a production application.
