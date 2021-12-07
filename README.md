# crash-reporter

Another crash reporter. Allows a user to send a crash report via the end point (HTTP).
Later the crash is added to a rabbitMQ queue to be sent to a slack channel.

## Requirements

* RabbitMQ (2x or 3x)
* JDK >8

## Configuration

The config should be placed in the resources directory.

The example of the config.

```json
{
    "slackHook": "https://slack/some-hook", // the web hook of Slack, it'll be triggered when the crash is received
    "port": 6789, // the port to launch the app
    "rmqConf": { // the config of RabbitMQ
        "host": "rmq-host.com",
        "port": 5672,
        "username": "user",
        "vhost": "vhost",
        "password": "password"
    }
}
```
