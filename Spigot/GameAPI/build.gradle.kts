group = "net.rankedproject"
version = "1.0-SNAPSHOT"
description = "GameAPI"

tasks.named("shadowJar") {
    dependsOn(":Spigot:CommonSpigot:shadowJar")
}