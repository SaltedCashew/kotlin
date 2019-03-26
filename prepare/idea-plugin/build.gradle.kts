description = "Kotlin IDEA plugin"

plugins {
    `java-base`
}

repositories {
    maven("https://jetbrains.bintray.com/markdown")
}

// Do not rename, used in pill importer
val projectsToShadow by extra(listOf(
        ":plugins:annotation-based-compiler-plugins-ide-support",
        ":core:type-system",
        ":compiler:backend",
        ":compiler:backend-common",
        ":compiler:backend.jvm",
        ":compiler:ir.backend.common",
        ":kotlin-build-common",
        ":compiler:cli-common",
        ":compiler:container",
        ":compiler:daemon-common",
        ":core:metadata",
        ":core:metadata.jvm",
        ":core:descriptors",
        ":core:descriptors.jvm",
        ":core:deserialization",
        ":idea:eval4j",
        ":idea:formatter",
        ":compiler:psi",
        *if (project.findProperty("fir.enabled") == "true") {
            arrayOf(
                ":compiler:fir:cones",
                ":compiler:fir:resolve",
                ":compiler:fir:tree",
                ":compiler:fir:java",
                ":compiler:fir:psi2fir",
                ":idea:fir-view"
            )
        } else {
            emptyArray()
        },
        ":compiler:frontend",
        ":compiler:frontend.common",
        ":compiler:frontend.java",
        ":compiler:frontend.script",
        ":idea:ide-common",
        ":idea",
        ":idea:idea-native",
        ":idea:idea-core",
        ":idea:idea-gradle",
        ":idea:idea-gradle-native",
        ":compiler:ir.psi2ir",
        ":compiler:ir.tree",
        ":js:js.ast",
        ":js:js.frontend",
        ":js:js.parser",
        ":js:js.serializer",
        ":js:js.translator",
        ":kotlin-native:kotlin-native-utils",
        ":kotlin-native:kotlin-native-library-reader",
        ":compiler:light-classes",
        ":compiler:plugin-api",
        ":kotlin-preloader",
        ":compiler:resolution",
        ":compiler:serialization",
        ":compiler:util",
        ":core:util.runtime",
        ":allopen-ide-plugin",
        ":plugins:lint",
        ":plugins:uast-kotlin",
        ":plugins:uast-kotlin-idea",
        ":j2k",
        ":kotlin-allopen-compiler-plugin",
        ":kotlin-noarg-compiler-plugin",
        ":kotlin-sam-with-receiver-compiler-plugin",
        ":kotlin-scripting-idea",
        ":kotlinx-serialization-compiler-plugin",
        ":kotlinx-serialization-ide-plugin",
        ":noarg-ide-plugin",
        ":sam-with-receiver-ide-plugin",
        ":idea:idea-android",
        ":idea:idea-android-output-parser",
        ":idea:idea-jvm",
        ":idea:idea-git",
        ":idea:idea-jps-common",
        ":idea:kotlin-gradle-tooling",
        ":plugins:android-extensions-compiler",
        ":plugins:android-extensions-ide",
        ":plugins:kapt3-idea",
        *if (Ide.IJ())
            arrayOf(":idea:idea-maven")
        else
            emptyArray<String>()
))

// Do not rename, used in pill importer
val packedJars by configurations.creating

val sideJars by configurations.creating

dependencies {
    projectsToShadow.forEach {
        packedJars(project(it)) { isTransitive = false }
    }
    packedJars(protobufFull())
    packedJars(project(":core:builtins"))
    sideJars(project(":kotlin-script-runtime"))
    sideJars(kotlinStdlib())
    sideJars(kotlinStdlib("jdk7"))
    sideJars(kotlinStdlib("jdk8"))
    sideJars(project(":kotlin-reflect"))
    sideJars(project(":kotlin-compiler-client-embeddable"))
    sideJars(commonDep("io.javaslang", "javaslang"))
    sideJars(commonDep("javax.inject"))
    sideJars(commonDep("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8"))
    sideJars(commonDep("org.jetbrains", "markdown")) { isTransitive = false }
}

val jar = runtimeJar {
    dependsOn(packedJars)
    from("$rootDir/resources/kotlinManifest.properties")
    from {
        packedJars.files.map(::zipTree)
    }

    archiveName = "kotlin-plugin.jar"
}

ideaPlugin {
    duplicatesStrategy = DuplicatesStrategy.FAIL // Investigation is required if we have multiple jars with same name
    dependsOn(":dist")
    from(jar)
    from(sideJars)
}
