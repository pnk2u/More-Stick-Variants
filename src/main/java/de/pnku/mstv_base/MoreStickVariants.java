package de.pnku.mstv_base;

import de.pnku.mstv_base.item.MoreStickVariantItems;
import de.pnku.mstv_base.trade.MoreStickVariantsLootConditions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MoreStickVariants implements ModInitializer {
	public static final String MOD_ID = "mstv-base";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	
	@Override
	public void onInitialize() {
		ResourceLoader.registerBuiltinPack(
					withModId("mstv-leaves-loot-fix"),
					FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
					PackActivationType.ALWAYS_ENABLED);
		MoreStickVariantItems.registerStickItems();
		MoreStickVariantsLootConditions.register();
	}


	public static Identifier withModId(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
