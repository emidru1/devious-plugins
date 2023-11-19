import ProjectVersions.unethicaliteVersion

version = "1.3.7"

project.extra["PluginName"] = "Chaos Bird House"
project.extra["PluginDescription"] = "Mass bird slaughter"

dependencies {
    annotationProcessor(Libraries.lombok)
    annotationProcessor(Libraries.pf4j)

    compileOnly("net.unethicalite:runelite-api:${unethicaliteVersion}")
    compileOnly("net.unethicalite:runelite-client:${unethicaliteVersion}")

    compileOnly(Libraries.guice)
    compileOnly(Libraries.lombok)
    compileOnly(Libraries.pf4j)
    compileOnly(Libraries.rxjava)
    compileOnly(project(":utils"))
}

tasks {
    jar {
        manifest {
            attributes(
                    mapOf(
                            "Plugin-Version" to project.version,
                            "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                            "Plugin-Provider" to project.extra["PluginProvider"],
                            "Plugin-Description" to project.extra["PluginDescription"],
                            "Plugin-Dependencies" to
                                    arrayOf(
                                            nameToId("Chaos Utils")
                                    ).joinToString(),
                            "Plugin-License" to project.extra["PluginLicense"]
                    )
            )
        }
    }
}