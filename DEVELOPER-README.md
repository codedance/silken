Silken Developer Readme
======

##Coding conventions

* Standard "Sun Style" coding conventions
* Spaces only - no tabs
* 4 space indents
* 120 line width


##Using Eclipse

1. Clone the repo
2. Install maven
3. ```cd``` into project root
4. Type ```mvn eclispe:eclipse```
5. Open project in Eclipse


## Build Instructions

```mvn test``` - Run unit tests

```mvn package``` - Compile and build the JAR

```mvn deploy``` - Create maven repo structure at ${basedir}/../maven-repository

