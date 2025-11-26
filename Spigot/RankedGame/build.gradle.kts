group = "net.rankedproject"
version = "1.0-SNAPSHOT"

dependencies {
    api(project(":Spigot:GameAPI"))
}

tasks.named("shadowJar") {
    dependsOn(":Spigot:GameAPI:shadowJar")
}