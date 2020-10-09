import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream

plugins {
//    java
    `java-library`
//    application
    id("com.github.ben-manes.versions").version("0.33.0")
    id("com.github.johnrengelman.shadow").version("6.0.0")
    kotlin("jvm").version("1.4.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.solostudios.omnivoxscraper"
version = "0.0.1-alpha.2"
//application.mainClassName = "com.solostudios.omnivoxscraper.old.OmniScraper"
//archivesBaseName = "Omnivox Scrapper"

val versionObj = Version("0", "0", "1", "alpha.3")
version = versionObj

repositories {
    mavenCentral()
}

dependencies {
    //htmlunit
    implementation("net.sourceforge.htmlunit:htmlunit:2.43.0")
    //output to ical
    implementation("org.mnode.ical4j:ical4j:3.0.19")
    //annotations
    implementation("org.jetbrains:annotations:20.1.0")
    //sl4j
    implementation("org.slf4j:slf4j-api:1.7.+")
    implementation("org.slf4j:jcl-over-slf4j:1.7.30")

//    testImplementation("junit:junit:4.13")
    //runtime implementation of slf4j
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
    //testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

//    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.0")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    //implementation("org.jsoup:jsoup:1.13.1")
}


tasks {
    test {
        useJUnitPlatform()

        systemProperty("OMNIVOX_PASSWORD", omnivox_password)
        systemProperty("OMNIVOX_USERNAME", omnivox_username)

        maxHeapSize = "1G"
        ignoreFailures = false
        failFast = true
        maxParallelForks = 12
    }
}

val omnivox_password: String by project
val omnivox_username: String by project
val BUILD_NUMBER: String by project

val compileJava: JavaCompile by tasks
val shadowJar: ShadowJar by tasks
val javadoc: Javadoc by tasks
val jar: Jar by tasks
val build: Task by tasks
val clean: Task by tasks
val test: Test by tasks
val check: Task by tasks

//println(sourceSets["main"].allJava.filter {
////        it.name != "JavaFXTestInfo.java"
//    !it.path.contains("/old/")
//}.asFileTree)

val tokenizeJavaSources = task<Copy>("tokenizeJavaSources") {
    from(sourceSets["main"].allSource) {
//        include("**/JavaFXTestInfo.java")
        exclude("**")
        //TODO: do version shit
//        def tokens = [
//                "versionMajor": versionObj.major,
//                "versionMinor": versionObj.minor,
//                "versionPatch": versionObj.revision,
//                "versionBuild": versionObj.build
//        ]
//        if (versionObj.preReleaseData)
//            tokens.put("versionPreRelease", versionObj.preReleaseData)
//        filter(ReplaceTokens, "tokens": tokens)
    }
    into("build/filteredSrc")
    includeEmptyDirs = false
}

val generateJavaSources = task<SourceTask>("generateJavaSources") {
    val javaSources = sourceSets["main"].allJava.filter {
//        it.name != "JavaFXTestInfo.java"
        !it.path.contains("/old/")
    }.asFileTree
    source = javaSources + fileTree(tokenizeJavaSources.destinationDir)
    dependsOn(tokenizeJavaSources)
}

task<ShadowJar>(name = "shadedJar") {
    archiveClassifier.set("shaded")
    configurations = shadowJar.configurations//{ project.configurations.runtime }
}

task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from("src/main/java") {
        exclude("**/JavaFXTestInfo.java")
    }
    from(tokenizeJavaSources.destinationDir)
    dependsOn(tokenizeJavaSources)
}

task<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
//    options.isIncremental = true
    doFirst {
        options.compilerArgs = mutableListOf("-Xlint:all", "--release", "11")
    }
}

compileJava.apply {
    source = generateJavaSources.source
    dependsOn(generateJavaSources)
}

tasks.withType<ShadowJar>().configureEach {
    if (name != ShadowJavaPlugin.getSHADOW_JAR_TASK_NAME()) {
        val convention = project.convention.getPlugin(JavaPluginConvention::class)
        group = "Jar"
        description = "Create a combined JAR of project and runtime dependencies"
        manifest.inheritFrom(jar.manifest)
        from(convention.sourceSets["main"].output)
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
        doFirst {
            setVersion(project.version)
//        archiveVersion.set(project.version.toString())
        }
    }
}

javadoc.apply {
    source = sourceSets["main"].java.asFileTree
    classpath = sourceSets["main"].runtimeClasspath
    isFailOnError = false
    options.memberLevel = JavadocMemberLevel.PUBLIC
    options.encoding = "UTF-8"

    if (options is StandardJavadocDocletOptions) {
        val opts = options as StandardJavadocDocletOptions
        opts.author()
        opts.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
        opts.links("https://docs.oracle.com/en/java/javase/11/docs/api/index.html", "https://htmlunit.sourceforge.io/")
    }

    (options as StandardJavadocDocletOptions).author()
    (options as StandardJavadocDocletOptions).tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
    (options as StandardJavadocDocletOptions).links("https://docs.oracle.com/en/java/javase/11/docs/api/index.html", "https://htmlunit.sourceforge.io/")
}

build.apply {
    dependsOn(clean)
    dependsOn(tasks.withType<Jar>().toTypedArray())
    dependsOn(tasks.withType<ShadowJar>().toTypedArray())

    jar.mustRunAfter(clean)
    tasks.withType<Jar>().filter { it != jar }.forEach {
        if (name != "jar")
            it.mustRunAfter(jar)
    }
    tasks.withType<ShadowJar>().forEach {
        it.mustRunAfter(jar)
    }
}

tasks.jar {
    dependsOn(generateJavaSources)
    manifest {
        attributes("Automatic-Module-Name" to "com.solostudios.omnivoxscrapper")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes("Built-By" to System.getProperty("user.name"),
                "Build-Jdk" to System.getProperty("java.version"),
                "Implementation-Title" to rootProject.name,
                "Implementation-Version" to project.version,
                "Created-By" to "Shadow Jar")
    }
}

val increaseBuildNumber = task("increaseBuildNumber") {
    doLast {
//        BUILD_NUMBER = ((Integer.parseInt(BUILD_NUMBER)) + 1).toString()
        project.extra.set("BUILD_NUMBER", ((Integer.parseInt(BUILD_NUMBER)) + 1).toString())

        ant.withGroovyBuilder {
            "propertyfile"("file" to "gradle.properties") {
                "entry"("key" to "BUILD_NUMBER", "value" to BUILD_NUMBER)
            }
        }
    }
}
compileJava.dependsOn(increaseBuildNumber)

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = mutableListOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

class Version(val major: String, val minor: String, val revision: String, val preReleaseData: String? = null) {
    override fun toString(): String {
        return if (preReleaseData.isNullOrBlank())
            "$major.$minor.$revision+${BUILD_NUMBER}"
        else
            "$major.$minor.$revision-$preReleaseData+${getGitHash()}"
    }

//    override fun toString() = "$major.$minor.${revision}_${getBuild()}"
}

//class Version {
//    public final major
//    public final minor
//    public final revision
//    private final project
//    final preReleaseData
//    public final build = project.BUILD_NUMBER
//
//    Version(String major, String minor, String revision, Project project)
//    {
//        this.major = major
//        this.minor = minor
//        this.revision = revision
//        this.project = project
//    }
//
//    Version(String major, String minor, String revision, String preReleaseData, Project project)
//    {
//        this.major = major
//        this.minor = minor
//        this.revision = revision
//        this.project = project
//        this.preReleaseData = preReleaseData
//    }
//
//    @Override
//    String toString()
//    {
//        return (preReleaseData ?
//        major + "." + minor + "." + revision + "-" + preReleaseData + "+build." + project.BUILD_NUMBER :
//        major + "." + minor + "." + revision + "+build." + project.BUILD_NUMBER)
//    }
//}