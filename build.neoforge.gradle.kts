plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
	}
	replacements.string(current.parsed < "1.21.11") {
		replace("Identifier", "ResourceLocation")
	}
	replacements.string(current.parsed > "1.21.1") {
		replace("ShaderInstance", "ShaderManager")
	}
	replacements.string(current.parsed <= "1.21.1") {
		replace("ShaderManager", "ShaderInstance")
	}
	replacements.string(current.parsed > "1.21.1") {
		replace("RegisterClientReloadListenersEvent", "AddClientReloadListenersEvent")
	}
	replacements.string(current.parsed <= "1.21.1") {
		replace("AddClientReloadListenersEvent", "RegisterClientReloadListenersEvent")
	}
	replacements.string(current.parsed <= "1.21.1") {
		replace(
			"event.addListener(ModTemplate.id(\"reload_resources\"), new ResourceReloadListener());",
			"event.registerReloadListener(new ResourceReloadListener());"
		)
	}
	// En 1.21.1 NeoForge parchea ParticleEngine#render anadiendole dos parametros (frustum y
	// predicado de particulas), asi que el target de ese @Inject se completa desde el script del
	// loader. De este modo el fuente no necesita anidar condicionales de loader dentro de bloques de
	// version, combinacion que Stonecutter no sabe preprocesar.
	// Ver build.fabric.gradle.kts.
	replacements.string(current.parsed >= "26.1.2") {
		replace("GuiGraphics", "GuiGraphicsExtractor")
	}
	replacements.string(current.parsed <= "1.21.1") {
		replace(
			"__LOOTBEAMS_PARTICLE_RENDER_TARGET__",
			"Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V"
		)
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("neoforge") {
			forgeLikeVersionRange.set("[1,)")
		}
	}
}

neoForge {
	version = prop("deps.neoforge")
	accessTransformers.from(rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg"))
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		val devJvmArgs = propsList("mod", "dev_jvm_args") +
			propsList("mod", "dev_jvm_args_mixin_debug")
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll(devJvmArgs)
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
			jvmArguments.addAll(devJvmArgs)
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	strictMaven("https://maven.shedaniel.me/")
}

dependencies {
	api("me.shedaniel.cloth:cloth-config-neoforge:${prop("deps.cloth-config")}")
	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
