plugins {
    id("java")
    `maven-publish`
    signing
}

group = "com.chriskiehl"
version = "0.0.1"

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "deoplice"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Deoplice")
                description.set("Very spicy additions to Lombok's annotations")
                url.set("https://github.com/chriskiehl/Deoplice")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer {
                        id.set("chriskiehl")
                        name.set("Chris Kiehl")
                        email.set("me@chriskiehl.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/chriskiehl/Deoplice.git")
                    developerConnection.set("scm:git:ssh://github.com/chriskiehl/Deoplice.git")
                    url.set("https://github.com/chriskiehl/Deoplice")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = project.property("ossrhUsername").toString()
                password = project.property("ossrhPassword").toString()
            }
        }
    }
}

/**
 * This seems to format the keyring location incorrectly when read
 * from gradle.properties. I gave up and now just read them by hand.
 *
 * Note to future you: see publishing.md
 */
signing {
    val signingKey = File("signingkey").readText()
    val signingPassword = project.property("signing.password").toString()
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}



repositories {
    mavenCentral()
}

dependencies {

    implementation("io.vavr:vavr:0.10.4")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.projectlombok:lombok:1.18.24")
    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}



tasks.getByName<Test>("test") {
    useJUnitPlatform()
}