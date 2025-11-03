rootProject.name = "RankedProject"
include("Common")
include("Spigot")
include("PrivateAPI")
include("Spigot:GameAPI")
findProject(":Spigot:GameAPI")?.name = "GameAPI"
include("Spigot:RankedGame")
findProject(":Spigot:RankedGame")?.name = "RankedGame"
include("Spigot:RankedLobby")
findProject(":Spigot:RankedLobby")?.name = "RankedLobby"
include("Spigot:CommonSpigot")
findProject(":Spigot:CommonSpigot")?.name = "CommonSpigot"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}