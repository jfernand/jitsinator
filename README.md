# Jitsi dockerizer

Builds a docker compose pod of jitsi meet.

## How

On the command line, at the root folder of the project, run:
```shell
./gradlew dockerize
```

Then, to run the system, go to the jitsi meet folder, and run:
```shell
docker-compose up
```

## Customizing

The file ```env.master``` at the root folder of the project, 
is the only configuration point we are aware of right now.

It corresponds to the [config documentation here](https://jitsi.github.io/handbook/docs/devops-guide/devops-guide-docker#configuration)

## General layout

This is a gradle build. The file ``build.gradle.kts`` defines a task called `dockerify` that downloads and unzips the 
latest jitsi-meet distribution (into the ```jitsi-meet``` folder. It then copies the ```env.master``` file into that folder,
as ```.env```.

The distribution ships with a ```docker-compose``` file that wires all the different services together.

The stuff in the ```buildSrc``` folder is just supporting code for the ```gradle.build.kts``` script.

## Why gradle?

No better way...
