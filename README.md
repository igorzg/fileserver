# Requirements 

* [Gradle](https://gradle.org/)
* [JDK gte 1.8](https://openjdk.java.net/install/)

## Starting local server
```
gradle startServer
```

## Building

```
gradle build
```
## Usage
jar args:
<port> <threads> <public-folder> <defaults-to-index.html>

```
java -jar build/libs/icoder-1.0.jar 5000 10 public index.html
```

## Or simply run jar
```
java -jar icoder-1.0.jar 5000 10 public index.html
```