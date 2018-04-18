App Engine Java SpringBoot Kotlin application
===

## App Engine Java8/ Standard Runtime for Spring Boot Application.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/download.cgi) (at least 3.5)
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud command line tool)  
  * [quickstarts](https://cloud.google.com/sdk/docs/quickstarts)

## Setup

* Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    `gcloud init`

* Create an App Engine app within the current Google Cloud Project

    `gcloud app create`

## Maven
### Running locally

`mvn appengine:run`

To use visit: http://localhost:8080/

### Deploying

`mvn appengine:deploy`

To use vist:  https://YOUR-PROJECT-ID.appspot.com

## Testing

`mvn verify`

As you add / modify the source code (`src/main/kotlin/...`) it's very useful to add [unit testing](https://cloud.google.com/appengine/docs/java/tools/localunittesting)
to (`src/main/test/...`).  The following resources are quite useful:

* [Junit4](http://junit.org/junit4/)
* [Mockito](http://mockito.org/)
* [Truth](http://google.github.io/truth/)


For further information, consult the
[Java App Engine](https://developers.google.com/appengine/docs/java/overview) documentation.

