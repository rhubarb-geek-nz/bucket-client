# rhubarb-geek-nz/bucket-client

## Introduction

This is a very simple tool for bootstrapping legacy systems where you might not have either [curl](https://curl.se/) or a [browser](https://en.wikipedia.org/wiki/Netscape) but you do have an early edition of [Java](https://www.java.com/).

## Standard maven build

Build with [maven](https://maven.apache.org/) with

```
mvn package -Dmanifest.Built-By=rhubarb-geek-nz@users.sourceforge.net
```

## Legacy command line build

Build with JDK 1.3 with a manifest file.

MANIFEST.MF

```
Main-Class: nz.geek.rhubarb.bucket.client.Main
Built-By: rhubarb-geek-nz@users.sourceforge.net
```

Command sequence with java source in current directory.

```
javac -target 1.2 *.java -d .
jar cfm nz-geek-rhubarb-bucket-client.jar MANIFEST.MF nz
```

## Usage

The command line arguments are

| switch | arguments | comment |
| -------- | ------ | ------- |
| -method | method | http method |
| -url| url | relevant URL |
| -header | name value | set header with name to value |
| -file | path | file to read or write |

## Examples

To `GET` a file

```
java -jar nz-geek-rhubarb-bucket-client.jar -url http://127.0.0.1:8080/favicon.ico -header Authorization "Basic Z3Vlc3Q6Y2hhbmdlaXQ=" -file favicon.ico
```

To `PUT` a file

```
java -jar nz-geek-rhubarb-bucket-client.jar -url http://127.0.0.1:8080/example.txt -header Authorization "Basic Z3Vlc3Q6Y2hhbmdlaXQ=" -method PUT -file example.txt
```
