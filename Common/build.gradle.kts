group = "net.rankedproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo1.maven.org/maven2/")
}

dependencies {
    compileOnly(libs.lombok)
    api(libs.gson)
    api(libs.okhttp)
    api(libs.caffeine)
    api(libs.caffeine.guava)
    api(libs.caffeine.jcache)
    api(libs.fastutil)
    api(libs.guice)
    api(libs.annotations)
    api(libs.jnats)
    api(libs.protobuf.java)
    api(libs.redisson)
    api(libs.reflections)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.+")
    annotationProcessor(libs.lombok)
}

tasks.test {
    useJUnitPlatform()
}