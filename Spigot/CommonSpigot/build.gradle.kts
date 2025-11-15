group = "net.rankedproject"
version = "1.0-SNAPSHOT"

plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
    configurations.all {
        resolutionStrategy {
            force("com.google.guava:guava:33.3.1-jre")
        }
    }
}

description = "CommonSpigot"