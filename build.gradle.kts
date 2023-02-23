import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	id("org.springframework.boot") version "3.0.2"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.0"
	kotlin("plugin.spring") version "1.8.0"
}

group = "com.issahar"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven("https://www.dcm4che.org/maven2/")
	maven("https://raw.github.com/nroduit/mvn-repo/master/")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jersey:3.0.2")
	implementation("org.glassfish.jersey.media:jersey-media-multipart")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("javax.annotation:javax.annotation-api:1.3.2")


	// DB, JDBI and connection pooling
	implementation("com.zaxxer:HikariCP:5.0.1")
	implementation("com.mysql:mysql-connector-j:8.0.31")
	implementation("org.jdbi:jdbi3-core:3.35.0")
	implementation("org.jdbi:jdbi3-sqlobject:3.35.0")
	implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.35.0")
	implementation("org.jdbi:jdbi3-kotlin:3.34.0")
	// aws sdk
	implementation("software.amazon.awssdk:s3:2.19.26")
	implementation("software.amazon.awssdk:secretsmanager:2.19.26")

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
