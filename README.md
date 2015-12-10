# Akka HTTP invitation microservice example

This project demonstrates the [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala.html) 
library and Scala to write a simple REST invitation (micro)service. 
This project is based on Typesage Activator [AKKA HTTP MICROSERVICE EXAMPLE](http://www.typesafe.com/activator/template/akka-http-microservice)

The service provides endpoint for adding and fetching added invitations.
The invitations are stored in memory.

## Configuration

To change default configuration check application.conf

## Usage

Start services with script:

```
$ ./run.sh
> ~re-start
```


Start services with activator:

```
$ activator
> ~re-start
```

With the service up, you can start sending HTTP requests:

```
$ curl http://localhost:9000/invitation
 
Response:
 
[{
  "invitee": "John Smith",
  "email": "john@smith.mx"
}]
```

```
$ curl -X POST -H 'Content-Type: application/json' http://localhost:9000/invitation -d '{  "invitee": "John Smith", "email": "john@smith.mx"}'

Response:

201 Created

```

### Testing

Execute tests using `test` command:

```
$ sbt
> test
```

## Author & license
Michal Kunikowski

