import org.gradle.nativeplatform.platform.internal.ArchitectureInternal
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.lang.System.getenv

plugins {
    kotlin("multiplatform") version "1.9.20"
}

group = "me.ikvarxt"
version = "0.1.0"

private val hostOs: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
private val hostArchitecture: ArchitectureInternal = DefaultNativePlatform.getCurrentArchitecture()

private val exeExt: String get() = when {
    hostOs.isWindows -> ".exe"
    else -> ""
}

private val cargoAbsolutePath: String
    get() = when {
        hostOs.isWindows -> getenv("USERPROFILE")
        else -> getenv("HOME")
    }
        ?.let(::File)
        ?.resolve(".cargo/bin/cargo$exeExt")
        ?.takeIf { it.exists() }
        ?.absolutePath
        ?: throw GradleException("cargo binary is required to build project but it wasn't found")

fun projectFile(path: String): String = projectDir.resolve(path).absolutePath

private val rustLibAbsolutePath: String
    get() = projectFile(
        path = when {
            hostOs.isWindows -> "tyme4rs_clib/target/release/tyme4rs_clib.lib"
            else -> "tyme4rs_clib/target/release/libtyme4rs_clib.a"
        }
    )

kotlin {
    applyDefaultHierarchyTemplate()

    val host = when {
        hostOs.isMacOsX && hostArchitecture.isAmd64 -> Host(macosX64())
        hostOs.isMacOsX && hostArchitecture.isArm64 -> Host(macosArm64())
        hostOs.isLinux && hostArchitecture.isAmd64 -> Host(linuxX64())
        hostOs.isWindows && hostArchitecture.isAmd64 -> Host(mingwX64())
        else -> throw GradleException("OS: $hostOs and architecture: $hostArchitecture is not supported in script configuration.")
    }
    tasks.create("binaries") {
        dependsOn("${host.targetName}Binaries")
        doLast { host.renameBinaries() }
    }
    host.target {
        compilations.getByName("main").cinterops {
            create("tyme4rs_clib") {
                val buildRustLib by tasks.creating {
                    exec {
                        executable = cargoAbsolutePath
                        args(
                            "build",
                            "--manifest-path", projectFile("tyme4rs_clib/Cargo.toml"),
                            "--package", "tyme4rs_clib",
                            "--lib",
                            "--release"
                        )
                    }
                }
                tasks.getByName(interopProcessingTaskName) {
                    dependsOn(buildRustLib)
                }
                header(projectFile("tyme4rs_clib/target/tyme4rs_clib.h"))
            }
        }
        binaries.executable {
            entryPoint = "main"
            baseName = "kotlin-tool"
            linkerOpts += rustLibAbsolutePath
        }
    }
}

class Host(private val target: KotlinNativeTargetWithHostTests) {

    val targetName: String get() = target.targetName

    fun target(configure: KotlinNativeTargetWithHostTests.() -> Unit): Unit = target.run(configure)

    fun renameBinaries() {
        project.buildDir.resolve("bin/${target.name}").walkTopDown().forEach rename@{
            if (it.extension != "kexe") return@rename
            val renamed = it.parentFile.resolve(it.nameWithoutExtension + exeExt)
            it.renameTo(renamed)
        }
    }
}