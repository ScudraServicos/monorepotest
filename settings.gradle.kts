rootProject.name = "br.com.ume"

fun findProjectsWithBuildGradle(directories: List<File>, prefix: String = ""): List<String> {
    val projects = mutableListOf<String>()

    directories.forEach { directory ->
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                val subprojects = findProjectsWithBuildGradle(listOf(file), "${prefix}${directory.name}:")
                projects.addAll(subprojects)
            } else if (file.name == "build.gradle.kts") {
                projects.add("${prefix}${directory.name}")
            }
        }
    }

    return projects
}

val projects = findProjectsWithBuildGradle(
    listOf(
        File("libs"),
        File("services")
    )
)

projects.forEach { project ->
    include(project)
}