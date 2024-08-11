plugins {
    id("java")
}

group = "de.LuxuryBot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("net.dv8tion:JDA:5.0.0-beta.24")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("net.oneandone.reflections8:reflections8:0.11.7")

    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("org.slf4j:slf4j-api:2.0.5")                             // SLF4J API
    implementation("ch.qos.logback:logback-classic:1.5.6")                  // Logback Classic (with SLF4J binding)
    implementation("ch.qos.logback:logback-core:1.4.14")                    // Logback Core (required for configuration)

    implementation("org.postgresql:postgresql:42.7.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}