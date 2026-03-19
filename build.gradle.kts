plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    doFirst {
        // Kill any process using port 8000 to avoid "port already in use" errors
        try {
            ProcessBuilder("bash", "-c", "kill -9 \$(lsof -ti:8000 2>/dev/null) 2>/dev/null; sleep 2; exit 0")
                .redirectErrorStream(true)
                .start()
                .waitFor(10, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: Exception) {
            println("Note: Could not free port 8000: \${e.message}")
        }
    }
}
