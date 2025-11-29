group = "net.rankedproject"
version = "1.0-SNAPSHOT"

tasks.named("shadowJar") {
    dependsOn(":Spigot:CommonSpigot:shadowJar")
}