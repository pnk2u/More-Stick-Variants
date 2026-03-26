package de.pnku.mstv_base.trade;

import de.pnku.mstv_base.MoreStickVariants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class MoreStickVariantsLootConditions {
    private MoreStickVariantsLootConditions() {}

    public static void register() {
        Registry.register(
                BuiltInRegistries.LOOT_CONDITION_TYPE,
                MoreStickVariants.withModId("villager_job_site_block"),
                VillagerJobSiteBlockCondition.MAP_CODEC
        );
        Registry.register(
                BuiltInRegistries.LOOT_CONDITION_TYPE,
                MoreStickVariants.withModId("villager_missing_trades"),
                VillagerMissingTradesCondition.MAP_CODEC
        );
    }
}
