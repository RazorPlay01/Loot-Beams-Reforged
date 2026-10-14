package com.github.razorplay.lootbeams;

import com.github.razorplay.lootbeams.compat.iceberg.IcebergCompat;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.config.JsonConfigAdapter;
import com.github.razorplay.lootbeams.dconfig.DynamicConfig;
import com.github.razorplay.lootbeams.platform.Platform;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import com.github.razorplay.lootbeams.platform.fabric.FabricPlatform;
//?} neoforge {
/*import com.github.razorplay.lootbeams.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import com.github.razorplay.lootbeams.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ModTemplate {

	public static final String MOD_ID = /*$ mod_id*/ "lootbeams";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "LootBeamsContinued";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static DynamicConfig.ConfigManager<Configuration> configManager;
	public static Configuration config;

	public static void onInitialize() {
		configManager = DynamicConfig.load(MOD_ID, Configuration.class, new JsonConfigAdapter());
		config = configManager.getConfig();
		if (IcebergCompat.isIcebergLoaded()) {
			LOGGER.info(IcebergCompat.getPlatformName());
		}
		LOGGER.info("Initializing {} on {}", MOD_ID, xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
		ClientSetup.registerCommonEvents();
	}

	public static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		 *///?}
	}

	public static Identifier id(String path) {
		//? >1.19.2 {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
		//?} <=1.19.2 {
		/*return new Identifier(MOD_ID, path);
		 *///?}
	}

	public static Identifier id(String namespace, String path) {
		//? >1.19.2 {
		return Identifier.fromNamespaceAndPath(namespace, path);
		//?} <=1.19.2 {
		/*return new Identifier(namespace, path);
		 *///?}
	}
}
