pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://tencent-tds-maven.pkg.coding.net/repository/shiply/repo" )

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://tencent-tds-maven.pkg.coding.net/repository/shiply/repo" )
    }
}

rootProject.name = "lolo"
include(":app")
 