group = "net.rankedproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    api("com.google.code.gson:gson:2.13.2")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.github.ben-manes.caffeine:caffeine:3.2.3")
    api("it.unimi.dsi:fastutil:8.2.1")
    api("com.google.inject:guice:7.0.0")
    api("org.jetbrains:annotations:26.0.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.test {
    useJUnitPlatform()
}