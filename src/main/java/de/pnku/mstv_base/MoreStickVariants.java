package de.pnku.mstv_base;

import de.pnku.mstv_base.item.MoreStickVariantItems;
import de.pnku.mstv_base.trade.MstvVillagerTrades;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MoreStickVariants implements ModInitializer {
	public static final String MOD_ID = "mstv-base";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	
	@Override
	public void onInitialize() {
		ResourceManagerHelper.registerBuiltinResourcePack(
					withModId("mstv-leaves-loot-fix"),
					FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
					ResourcePackActivationType.ALWAYS_ENABLED);
		MoreStickVariantItems.registerStickItems();
		ServerLifecycleEvents.SERVER_STARTED.register(MoreStickVariants::initMstvTradeRegistration);
	}

	private static void initMstvTradeRegistration(MinecraftServer minecraftServer) {
		MstvVillagerTrades.initMstvTradeRegistration();
	}


	public static ResourceLocation withModId(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
