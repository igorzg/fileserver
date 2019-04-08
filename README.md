# Requirements 

gradle

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