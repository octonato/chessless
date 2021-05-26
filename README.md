# samples-java-chessless

Very minimal (and rough) chess application.

Lichess is the main inspiration for building this application. Lichess is an OSS Chess server build on Play and Akka. The project is huge and full of features. The goal is not to replicate it, but build a very minimal chess app.

This project is currently using https://github.com/bhlangonijr/chesslib as the underlying chess rules library. I tried to use Lichess' https://github.com/ornicar/scalachess, but it uses Cats and other FP Scala constructs that didn't play well with Java. It would be cool to move to scalachess once we have the Scala SDK.
## Major TODOs

- [ ] add a GUI - possibly something running on Play or Akka Http and working as a gateway. It's an opporutniy to show an integration point between Akka Platform and Akka Serverless. For the GUI part, we can use https://github.com/ornicar/lila/
- [ ] adds user services
- [ ] Views
  - [ ] find puzzles based on level (easy, moderate, hard)
  - [ ] winners statistics

## Designing

While designing your service it is useful to read [designing services](https://developer.lightbend.com/docs/akka-serverless/designing/index.html)


## Developing

This project has a bare-bones skeleton service ready to go, but in order to adapt and
extend it it may be useful to read up on [developing services](https://developer.lightbend.com/docs/akka-serverless/developing/index.html)
and in particular the [Java section](https://developer.lightbend.com/docs/akka-serverless/java-services/index.html)


## Building

To build, at a minimum you need to generate and process sources, particularly when using an IDE.
A convenience is compile your project:

```
mvn compile
```


## Running Locally

In order to run your application locally, you must run the Akka Serverless proxy. The included `docker-compose` file contains the configuration required to run the proxy for a locally running application. To start the proxy, run the following command from this directory:


```
docker-compose up
```


> On Linux this requires Docker 20.10 or later (https://github.com/moby/moby/pull/40007),
> or for a `USER_FUNCTION_HOST` environment variable to be set manually.

```
docker-compose -f docker-compose.yml -f docker-compose.linux.yml up
```

To start the application locally, the `exec-maven-plugin` is used. Use the following command:

```
mvn compile exec:java
```

With both the proxy and your application running, any defined endpoints should be available at `http://localhost:9000`. In addition to the defined gRPC interface, each method has a corresponding HTTP endpoint. Unless configured otherwise (see [Transcoding HTTP](https://docs.lbcs.dev/js-services/proto.html#_transcoding_http)), this endpoint accepts POST requests at the path `/[package].[entity name]/[method]`. For example, using `curl`:

For running, check out chess-api.sh. Source it in your shell and call the functions. Or simply look at the implementation and call grpcurl directly.

## Deploying

To deploy your service, install the `akkasls` CLI as documented in
[Setting up a local development environment](https://developer.lightbend.com/docs/akka-serverless/getting-started/set-up-development-env.html)
and configure a Docker Registry to upload your docker image to.

You will need to update the `akkasls.dockerImage` property in the `pom.xml` and refer to
[Configuring registries](https://developer.lightbend.com/docs/akka-serverless/deploying/registries.html)
for more information on how to make your docker image available to Akka Serverless.

Finally you can or use the [Akka Serverless Console](https://console.akkaserverless.com)
to create a project and then deploy your service into the project either by using `mvn deploy`,
through the `akkasls` CLI or via the web interface. When using `mvn deploy`, Maven will also
conveniently package and publish your docker image prior to deployment.
