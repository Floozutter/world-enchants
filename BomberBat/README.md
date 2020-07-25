# BomberBat
A Spigot plugin to turn bats into bombers.

## Usage
First, use [BuildTools](https://www.spigotmc.org/wiki/buildtools/) to build
`spigot-1.16.1.jar`.

Then to create `BomberBat.jar`:
```
$ javac -classpath spigot-1.16.1.jar EntityBomberBat.java CommandSpawn.java BomberBatPlugin.java
$ jar cfv BomberBat.jar EntityBomberBat.class CommandSpawn.class BomberBatPlugin.class plugin.yml
```
