# RetroCraft
Use Java 11 on Minecraft 1.7.10 with FORGE!

# Usage

This patch is NOT a forge mod! DO NOT add it to your mods folder!!!

Instead, this patch is a Java Agent, which used to modify class before they're loaded. LaunchWrapper fails before the mods folder are read, so we can only use this form of patch.

Download the patch .jar file, place it into your .minecraft folder and add a Java(JVM) argument (not "Minecraft/Game argument!")to your Minecraft Launcher, like this:

```
-javaagent:jar file name
```

for example if the name of the patch file is "RetroCraft-target-1.0-SNAPSHOT.jar", your argument added should be:

```
-javaagent:RetroCraft-target-1.0-SNAPSHOT.jar
```

Have fun!
