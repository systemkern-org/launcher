# Launcher
> Launcher is a template for Java/Kotlin applications 

This application template represents a strong opinionated view on the layout of Kotlin applications.
It is to be used and applied on an 'as is' basis with no guarantee of functionality.

The layout is aimed at building a a _modularised monolith_ , which can easily be separate into
micro services at any point in time.

### What this template provides:
* runtime for local execution including 
  * application configuration
  * logging configuration
  * assembly of distributable as zip 
* distribution via ftp (see ./pom.xml) 


#Application

### Local Execution
The assembled jar file is executable via `java -jar`

### Application Configuration
Application configuration is done via standard spring-boot config providers
* application.properties is used for technical configuration
* application.yml can be used for business configuration

### Logging Configuration
Logging is done via the [Logback](https://logback.qos.ch/documentation.html) framework.
Logging configuration currently is only availible at compile time.

#### Assembly and Distribution
For distribution of the assembled zip file via ftp call:
`mvn clean package wagon:upload-single`

###### Distribution configuration
Ftp login credentials need to be configured in you maven settings xml.
Usually be found at ~/.m2/settings.xml 

```
<settings>
  <servers>
    <server>
      <id>ftp-distribution-server</id>
      <username>myuseraccount</username>
      <password>s3cret</password>
    </server>
  </servers>
</settings>
```
