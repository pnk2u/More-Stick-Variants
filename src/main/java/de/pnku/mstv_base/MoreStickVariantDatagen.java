package de.pnku.mstv_base;

import de.pnku.mstv_base.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;


public class MoreStickVariantDatagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		//pack.addProvider(MoreStickVariantRecipeGenerator::new);
		//pack.addProvider(MoreStickVariantLangGenerator::new);
	}

}
