import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.channels.Channels


plugins {
//    java
    `java-library`
//    application
    id("com.github.ben-manes.versions").version("0.33.0")
    id("com.github.johnrengelman.shadow").version("6.0.0")
    id("io.freefair.lombok").version("5.2.1")
    kotlin("jvm").version("1.4.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.solostudios.omnivoxscraper"
//version = "0.0.1-alpha.3"
val versionObj = Version("0", "0", "1", "alpha.3")
version = versionObj

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.sourceforge.htmlunit:htmlunit:2.43.0")
    implementation("org.mnode.ical4j:ical4j:3.0.19")
    implementation("org.jetbrains:annotations:20.1.0")
    implementation("org.slf4j:slf4j-api:1.7.+")
    implementation("org.slf4j:jcl-over-slf4j:1.7.30")

//    testImplementation("junit:junit:4.13")
    //runtime implementation of slf4j
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
    //testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    
    //implementation("org.jsoup:jsoup:1.13.1")
}

val compileJava: JavaCompile by tasks
val mainDelombokPath = file("build/delombok")
val mainSourceSet: SourceSet = sourceSets["main"]

tasks.test {
    useJUnitPlatform()
    
    systemProperty("OMNIVOX_PASSWORD", getSecret("omnivox_password"))
    systemProperty("OMNIVOX_USERNAME", getSecret("omnivox_username"))
    
    maxHeapSize = "1G"
    ignoreFailures = false
    failFast = true
    maxParallelForks = 12
}

val tokenizeJavaSources = task<Copy>(name = "tokenizeJavaSources") {
    from(mainSourceSet.allSource) {
//        exclude("**")
        include("**/OmniInfo.java")
        val tokens = mutableMapOf("versionMajor" to versionObj.major,
                                  "versionMinor" to versionObj.minor,
                                  "versionRevision" to versionObj.revision,
                                  "gitHash" to getGitHash())
        if (!versionObj.preReleaseData.isNullOrBlank())
            tokens["preReleaseData"] = versionObj.preReleaseData
        else
            tokens["preReleaseData"] = ""
        
        filter(ReplaceTokens::class, "tokens" to tokens)
    }
    into("build/tokenizedSources")
    includeEmptyDirs = false
}

val generateJavaSources = task<SourceTask>(name = "generateJavaSources") {
    dependsOn(tokenizeJavaSources)
    exclude("**/old/**")
    source =
            mainSourceSet.allJava.asFileTree.matching { name != "OmniInfo.java" } + fileTree(tokenizeJavaSources
                                                                                                     .destinationDir)
}

val delombokCopy = task<Copy>(name = "delombokCopy") {
    dependsOn(generateJavaSources)
    from(generateJavaSources.source)
    into("build/delombokCopy")
}

val delombok = tasks.delombok.get().apply {
    dependsOn(delombokCopy)
    doFirst {
        input.setFrom(delombokCopy.destinationDir)
    }
    target.set(mainDelombokPath.resolve("main"))
}

tasks.generateLombokConfig {
    enabled = false
}

task<ShadowJar>(name = "shadedJar") {
    archiveClassifier.set("shaded")
    configurations = tasks.shadowJar.get().configurations//{ project.configurations.runtime }
}

task<Jar>(name = "sourcesJar") {
    archiveClassifier.set("sources")
    dependsOn(delombok)
    from(mainDelombokPath)
}

task<Jar>(name = "javadocJar") {
    dependsOn(tasks.javadoc)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

compileJava.apply {
    source = fileTree(mainDelombokPath)
    dependsOn(delombok)
    
    options.encoding = "UTF-8"
    options.isIncremental = false
    doFirst {
        options.compilerArgs = mutableListOf("-Xlint:all", "-Xlint:-processing", "--release", "11")
    }
    source = fileTree(mainDelombokPath)
    dependsOn(delombok)
}

tasks.withType<ShadowJar>().configureEach {
    if (name != ShadowJavaPlugin.getSHADOW_JAR_TASK_NAME()) {
        configurations = tasks.shadowJar.get().configurations
        description = "Create a combined JAR of project and runtime dependencies"
        manifest.inheritFrom(tasks.jar.get().manifest)
        dependsOn(compileJava)
        from(compileJava.outputs)
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
        doFirst {
            setVersion(project.version)
            archiveVersion.set(project.version.toString())
        }
    } else {
        enabled = false
    }
}

val downloadCss = tasks.create("downloadCss") {
    val url = URL("https://gist.githubusercontent.com/solonovamax/59a5b5bf1acfcb40f93a559d405a87c3/" +
                  "raw/649d9856bc4e411f76c76f582f31b792b77b635f/javadoc-dark-theme.css")
    val readableByteChannel = Channels.newChannel(url.openStream())
    val file = file("${buildDir}/stylesheet/dark-theme.css")
    file("${buildDir}/stylesheet/").mkdirs()
    val fileOutputStream = file.outputStream()
    val fileChannel = fileOutputStream.channel
    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
}

tasks.javadoc.get().apply {
    source = fileTree(mainDelombokPath)
    dependsOn(delombok)
    
    classpath = mainSourceSet.compileClasspath
    isFailOnError = true
    
    setMaxMemory("1G")
    
    (options as StandardJavadocDocletOptions).apply {
        author()
        tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:", "implNote:a:Implementation Note:")
        memberLevel = JavadocMemberLevel.PROTECTED
        encoding = "UTF-8"
//        val inputStream: InputStream =
//                URL("https://raw.githubusercontent.com/dracula/javadoc/master/styles/dracula-javadoc8.css")
//                .openStream()

//        Files.copy(inputStream, Paths.get("${buildDir}/stylesheet/dracula.css"), StandardCopyOption.REPLACE_EXISTING)
        dependsOn(downloadCss)
        stylesheetFile = file("${buildDir}/stylesheet/dark-theme.css")
    }
}

tasks.build {
    dependsOn(tasks.clean)
    dependsOn(tasks.withType<Jar>().toTypedArray())
    dependsOn(tasks.withType<ShadowJar>().toTypedArray())
    
    tasks.jar.get().mustRunAfter(tasks.clean)
    tasks.withType<Jar>().filter { it != tasks.jar.get() }.forEach { it.mustRunAfter(tasks.jar) }
    tasks.withType<ShadowJar>().forEach { it.mustRunAfter(tasks.jar) }
}

tasks.withType<Jar> {
    group = "Jar"
    manifest {
        attributes("Name" to "Omnivox Scrapper",
                   "Specification-Title" to "Omnivox Scrapper",
                   "Specification-Version" to project.version,
                   "Specification-Vendor" to "Solo Studios",
                   "Built-By" to System.getProperty("user.name"),
                   "Build-Jdk" to System.getProperty("java.version"),
                //                   "Created-By" to "Shadow Jar",
                   "Automatic-Module-Name" to "com.solostudios.omnivoxscrapper")
        
    }
}

fun getSecret(key: String): Any {
    val props = Properties()
    props.load(file("secrets.properties").inputStream())
    return props[key]!!
}

class Version(val major: String, val minor: String, val revision: String, val preReleaseData: String? = null) {
    
    override fun toString(): String {
        return if (preReleaseData.isNullOrBlank())
            "$major.$minor.$revision+${getGitHash()}"
        else
            "$major.$minor.$revision-$preReleaseData+${getGitHash()}"
    }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = mutableListOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
