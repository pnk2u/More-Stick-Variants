package de.pnku.mstv_base.trade;

import de.pnku.shields_mxsv.item.MoreExtraShieldVariantItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nemonotfound.nemoscampfires.item.ModItems.*;
import static de.pnku.lolmsv.item.MoreShieldVariantItems.*;
import static de.pnku.mbdv.init.MbdvItemInit.*;
import static de.pnku.mft.init.MftBlockInit.*;
import static de.pnku.mstv_base.MoreStickVariants.*;
import static de.pnku.mstv_base.item.MoreStickVariantItems.*;
import static de.pnku.mstv_mframev.item.MoreFrameVariantItems.*;
import static de.pnku.mstv_mfrv.item.MoreFishingRodVariantItems.*;
import static de.pnku.mstv_mweaponv.item.MoreWeaponVariantItems.*;
import static de.pnku.mstv_mtoolv.item.MoreToolVariantItems.*;
import static io.github.lieonlion.lolmbv.init.MbvItemInit.*;
import static net.minecraft.world.entity.npc.VillagerType.*;
import static net.minecraft.world.entity.npc.VillagerProfession.*;
import static net.minecraft.world.item.Items.*;


public class MstvVillagerTrades {

    public static Map<VillagerType, Item> fletcherLocalSticksBuyOffers = new HashMap<>();
    public static List<Map<VillagerType, Item>> fletcherForeignSticksBuyOffers = new ArrayList<>();
    public static Map<VillagerType, Block> fletcherLocalFletchingTable = new HashMap<>();
    public static Map<Block, Item> fletchingTableToStick = new HashMap<>();

    public static boolean isMFletchingTableVLoaded = false;
    public static boolean isMWeaponVLoaded = false;
    public static boolean isMToolVLoaded = false;
    public static boolean isMFrameVLoaded = false;
    public static boolean isMFishingRodVLoaded = false;
    public static boolean isNemosCampfireVLoaded = false;
    public static boolean isMShieldVLoaded = false;
    public static boolean isMXShieldVLoaded = false;
    public static boolean isMBedVLoaded = false;
    public static boolean isMBookshelfVLoaded = false;

    public static List<Item> replacedTrades = new ArrayList<>();
    public static List<VillagerProfession> affectedVillagers = new ArrayList<>();
    protected static float lowMod = 0.05F;
    protected static float highMod = 0.2F;

    public static void initMstvTradeRegistration() {
        init();
        registerMstvBaseTrades();
        if (isMWeaponVLoaded) registerMweaponvTrades();
        if (isMToolVLoaded) registerMtoolvTrades();
        if (isMFrameVLoaded) registerMframevTrades();
        if (isMFishingRodVLoaded) registerMfrodvTrades();
        if (isNemosCampfireVLoaded) registerNemocampfirevTrades();
        if (isMShieldVLoaded) registerMshieldvTrades(isMXShieldVLoaded);
        if (isMBedVLoaded) registerMbedvTrades();
        if (isMBookshelfVLoaded) registerMbookshelfvTrades();
    }

    private static void registerMstvBaseTrades(){
        // Local Sticks
        fletcherLocalSticksBuyOffers.put(VillagerType.PLAINS, STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.TAIGA, SPRUCE_STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.SNOW, SPRUCE_STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.DESERT, JUNGLE_STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.JUNGLE, JUNGLE_STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.SAVANNA, ACACIA_STICK);
        fletcherLocalSticksBuyOffers.put(VillagerType.SWAMP, DARK_OAK_STICK);
        TradeOfferHelper.registerVillagerOffers(FLETCHER, 1, factories -> factories.add(new VillagerTrades.EmeraldsForVillagerTypeItem(32, 16, 2, fletcherLocalSticksBuyOffers)));

        // Initialize Maps to store all non-local sticks
        int maxForeignTrades = more_sticks.size() - 1; // Subtract 1 for local stick
        for (int i = 0; i < maxForeignTrades; i++) {
            fletcherForeignSticksBuyOffers.add(new HashMap<>());
        }

        for (VillagerType type : fletcherLocalSticksBuyOffers.keySet()) {
            Item localStick = fletcherLocalSticksBuyOffers.get(type);
            int mapIndex = 0;

            for (Item stick : more_sticks) {
                if (!stick.equals(localStick) && mapIndex < fletcherForeignSticksBuyOffers.size()) {
                    fletcherForeignSticksBuyOffers.get(mapIndex).put(type, stick);
                    mapIndex++;
                }
            }
        }

        TradeOfferHelper.registerVillagerOffers(FLETCHER, 1, factories -> {
            for (Map<VillagerType, Item> stickMap : fletcherForeignSticksBuyOffers) {
                factories.add(new VillagerTrades.EmeraldsForVillagerTypeItem(24, 16, 2, stickMap));
            }
        });
        if (isMFletchingTableVLoaded) {
            fletcherLocalFletchingTable.putAll(initTypeToMftMap());
            fletchingTableToStick.putAll(initMftToStickMap());
        } else {
            fletchingTableToStick.put(Blocks.FLETCHING_TABLE, STICK);
        }
        replacedTrades.add(STICK);
        affectedVillagers.add(FLETCHER);
    }

    private static void registerMweaponvTrades(){
          // Fletcher
            // Arrow Sell
            registerBiomeSpecificSellOffers(FLETCHER, 1, 1, 16, 1, 12, lowMod, false, new Item[]{ACACIA_ARROW, ARROW, DARK_OAK_ARROW, JUNGLE_ARROW, SPRUCE_ARROW});
            // Bow Sell
            registerBiomeSpecificSellOffers(FLETCHER, 2, 2, 1, 5, 12, lowMod, false, new Item[]{ACACIA_BOW, BOW, DARK_OAK_BOW, JUNGLE_BOW, SPRUCE_BOW});
            // Crossbow Sell
            registerBiomeSpecificSellOffers(FLETCHER, 3, 3, 1, 10, 12, lowMod, false, new Item[]{ACACIA_CROSSBOW, OAK_CROSSBOW, CROSSBOW, JUNGLE_CROSSBOW, SPRUCE_CROSSBOW});
            // Enchanted Bow Sell
            registerBiomeSpecificSellOffers(FLETCHER, 4, 2, 1, 15, 3, lowMod, true, new Item[]{ACACIA_BOW, BOW, DARK_OAK_BOW, JUNGLE_BOW, SPRUCE_BOW});
            // Enchanted Crossbow Sell
            registerBiomeSpecificSellOffers(FLETCHER, 5, 3, 1, 15, 3, lowMod, true, new Item[]{ACACIA_CROSSBOW, CROSSBOW, MANGROVE_CROSSBOW, JUNGLE_CROSSBOW, SPRUCE_CROSSBOW});
            // Tipped Arrow Exchange
            TradeOfferHelper.registerVillagerOffers(FLETCHER, 5,  factories -> factories.addAll(
                    List.of(new VillagerTrades.ItemListing[]{
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.TippedArrowForItemsAndEmeralds(ACACIA_ARROW, 5, ACACIA_TIPPED_ARROW, 5, 2, 12, 30), SAVANNA),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.TippedArrowForItemsAndEmeralds(ARROW, 5, TIPPED_ARROW, 5, 2, 12, 30), PLAINS),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.TippedArrowForItemsAndEmeralds(DARK_OAK_ARROW, 5, DARK_OAK_TIPPED_ARROW, 5, 2, 12, 30), SWAMP),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.TippedArrowForItemsAndEmeralds(JUNGLE_ARROW, 5, JUNGLE_TIPPED_ARROW, 5, 2, 12, 30), JUNGLE, DESERT),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.TippedArrowForItemsAndEmeralds(SPRUCE_ARROW, 5, SPRUCE_TIPPED_ARROW, 5, 2, 12, 30), TAIGA, VillagerType.SNOW)
                    })
            ));

            replacedTrades.addAll(List.of(ARROW, BOW, CROSSBOW, TIPPED_ARROW));

          // Weaponsmith
            // Enchanted Iron Sword Sell
            registerBiomeSpecificSellOffers(WEAPONSMITH, 1, 2, 1, 1, 3, lowMod, true, new Item[]{ACACIA_IRON_SWORD, IRON_SWORD, DARK_OAK_IRON_SWORD, JUNGLE_IRON_SWORD, SPRUCE_IRON_SWORD});
            // Enchanted Diamond Sword Sell
            registerBiomeSpecificSellOffers(WEAPONSMITH, 5, 8, 1, 30, 3, highMod, true, new Item[]{ACACIA_DIAMOND_SWORD, DIAMOND_SWORD, DARK_OAK_DIAMOND_SWORD, JUNGLE_DIAMOND_SWORD, SPRUCE_DIAMOND_SWORD});

            replacedTrades.addAll(List.of(IRON_SWORD, DIAMOND_SWORD));
    }

    private static void registerMtoolvTrades(){
      // Toolsmith
        // Stone Axe/Shovel/Pickaxe/Hoe Sell
        registerBiomeSpecificSellOffers(TOOLSMITH, 1, 1, 1, 1, 12, highMod, false, new Item[]{ACACIA_STONE_AXE, STONE_AXE, DARK_OAK_STONE_AXE, JUNGLE_STONE_AXE, SPRUCE_STONE_AXE});
        registerBiomeSpecificSellOffers(TOOLSMITH, 1, 1, 1, 1, 12, highMod, false, new Item[]{ACACIA_STONE_SHOVEL, STONE_SHOVEL, DARK_OAK_STONE_SHOVEL, JUNGLE_STONE_SHOVEL, SPRUCE_STONE_SHOVEL});
        registerBiomeSpecificSellOffers(TOOLSMITH, 1, 1, 1, 1, 12, highMod, false, new Item[]{ACACIA_STONE_PICKAXE, STONE_PICKAXE, DARK_OAK_STONE_PICKAXE, JUNGLE_STONE_PICKAXE, SPRUCE_STONE_PICKAXE});
        registerBiomeSpecificSellOffers(TOOLSMITH, 1, 1, 1, 1, 12, highMod, false, new Item[]{ACACIA_STONE_HOE, STONE_HOE, DARK_OAK_STONE_HOE, JUNGLE_STONE_HOE, SPRUCE_STONE_HOE});
        // Enchanted Iron Axe/Shovel/Pickaxe Sell
        registerBiomeSpecificSellOffers(TOOLSMITH, 3, 1, 1, 10, 3, highMod, true, new Item[]{ACACIA_IRON_AXE, IRON_AXE, DARK_OAK_IRON_AXE, JUNGLE_IRON_AXE, SPRUCE_IRON_AXE});
        registerBiomeSpecificSellOffers(TOOLSMITH, 3, 2, 1, 10, 3, highMod, true, new Item[]{ACACIA_IRON_SHOVEL, IRON_SHOVEL, DARK_OAK_IRON_SHOVEL, JUNGLE_IRON_SHOVEL, SPRUCE_IRON_SHOVEL});
        registerBiomeSpecificSellOffers(TOOLSMITH, 3, 3, 1, 10, 3, highMod, true, new Item[]{ACACIA_IRON_PICKAXE, IRON_PICKAXE, DARK_OAK_IRON_PICKAXE, JUNGLE_IRON_PICKAXE, SPRUCE_IRON_PICKAXE});
        // Diamond Hoe Sell
        registerBiomeSpecificSellOffers(TOOLSMITH, 3, 4, 1, 10, 12, lowMod, false, new Item[]{ACACIA_DIAMOND_HOE, DIAMOND_HOE, DARK_OAK_DIAMOND_HOE, JUNGLE_DIAMOND_HOE, SPRUCE_DIAMOND_HOE});
        // Enchanted Diamond Axe/Shovel Sell
        registerBiomeSpecificSellOffers(TOOLSMITH, 4, 12, 1, 15, 3, highMod, true, new Item[]{ACACIA_DIAMOND_AXE, DIAMOND_AXE, DARK_OAK_DIAMOND_AXE, JUNGLE_DIAMOND_AXE, SPRUCE_DIAMOND_AXE});
        registerBiomeSpecificSellOffers(TOOLSMITH, 4, 5, 1, 15, 3, highMod, true, new Item[]{ACACIA_DIAMOND_SHOVEL, DIAMOND_SHOVEL, DARK_OAK_DIAMOND_SHOVEL, JUNGLE_DIAMOND_SHOVEL, SPRUCE_DIAMOND_SHOVEL});
        // Enchanted Diamond Pickaxe Sell
        registerBiomeSpecificSellOffers(TOOLSMITH, 5, 13, 1, 30, 3, highMod, true, new Item[]{ACACIA_DIAMOND_PICKAXE, DIAMOND_PICKAXE, DARK_OAK_DIAMOND_PICKAXE, JUNGLE_DIAMOND_PICKAXE, SPRUCE_DIAMOND_PICKAXE});

        replacedTrades.addAll(List.of(STONE_AXE, STONE_SHOVEL, STONE_PICKAXE, STONE_HOE, IRON_AXE, IRON_SHOVEL, IRON_PICKAXE, IRON_HOE, DIAMOND_HOE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_PICKAXE));

      // Weaponsmith
        // Enchanted Iron Axe Sell
        registerBiomeSpecificSellOffers(WEAPONSMITH, 1, 3, 1, 1, 12, highMod, false, new Item[]{ACACIA_IRON_AXE, IRON_AXE, DARK_OAK_IRON_AXE, JUNGLE_IRON_AXE, SPRUCE_IRON_AXE});
        // Enchanted Diamond Axe Sell
        registerBiomeSpecificSellOffers(WEAPONSMITH, 4, 12, 1, 15, 3, highMod, true, new Item[]{ACACIA_DIAMOND_AXE, DIAMOND_AXE, DARK_OAK_DIAMOND_AXE, JUNGLE_DIAMOND_AXE, SPRUCE_DIAMOND_AXE});
    }

    private static void registerMframevTrades(){
      // Cartographer
        // Item Frame Sell (add unique and then all for balancing against Colored banners)
        registerBiomeSpecificSellOffers(CARTOGRAPHER, 4, 7, 1, 15, 12, lowMod, false, new Item[]{ACACIA_ITEM_FRAME, OAK_ITEM_FRAME, DARK_OAK_ITEM_FRAME, JUNGLE_ITEM_FRAME, SPRUCE_ITEM_FRAME});
        for (Item itemFrame : more_item_frames) {
            TradeOfferHelper.registerVillagerOffers(CARTOGRAPHER, 4, factories -> factories.add(new VillagerTrades.ItemsForEmeralds(itemFrame, 7, 1, 12, 15)));
        }

      // Shepherd
        // Painting Sell
        registerBiomeSpecificSellOffers(SHEPHERD, 5, 2, 3, 30, 12, lowMod, false, new Item[]{ACACIA_PAINTING, OAK_PAINTING,  DARK_OAK_PAINTING, JUNGLE_PAINTING,  SPRUCE_PAINTING});

        replacedTrades.add(PAINTING);
    }

    private static void registerMfrodvTrades(){
      // Fisher
        // Enchanted Fishing Rod Sell
        registerBiomeSpecificSellOffers(FISHERMAN, 3, 3, 1, 10, 3, highMod, true, new Item[]{ACACIA_FISHING_ROD, FISHING_ROD, DARK_OAK_FISHING_ROD, JUNGLE_FISHING_ROD, SPRUCE_FISHING_ROD});

        replacedTrades.add(FISHING_ROD);
    }

    private static void registerNemocampfirevTrades(){
      // Fisher
        // Campfire Sell
        registerBiomeSpecificSellOffers(FISHERMAN, 2, 2, 1, 5, 12, lowMod, false, new Item[]{ACACIA_CAMPFIRE, CAMPFIRE, DARK_OAK_CAMPFIRE, JUNGLE_CAMPFIRE, SPRUCE_CAMPFIRE});

        replacedTrades.add(CAMPFIRE);
    }

    private static void registerMshieldvTrades(boolean isMXShieldVLoaded){
      // Armorer
        // Shield Sell
        if (!isMXShieldVLoaded){
            LOGGER.info("Registering mShieldV VillagerTrades");
            registerBiomeSpecificSellOffers(ARMORER, 3, 5, 1, 10, 12, highMod, false, new Item[]{ACACIA_SHIELD, OAK_SHIELD, DARK_OAK_SHIELD, JUNGLE_SHIELD, SHIELD});
        } else {
            LOGGER.info("Registering mXShieldV VillagerTrades");
            registerBiomeSpecificSellOffers(ARMORER, 3, 5, 1, 10, 12, highMod, false, new Item[]{MoreExtraShieldVariantItems.ACACIA_SHIELD, MoreExtraShieldVariantItems.OAK_SHIELD, MoreExtraShieldVariantItems.DARK_OAK_SHIELD, MoreExtraShieldVariantItems.JUNGLE_SHIELD, SHIELD});
        }

        replacedTrades.add(SHIELD);
    }

    private static void registerMbedvTrades() {
        // Shepherd
        // Colored Beds Sell
        Item[][] coloredBedsByColor = {
                {ACACIA_RED_BED_I, RED_BED, DARK_OAK_RED_BED_I, JUNGLE_RED_BED_I, SPRUCE_RED_BED_I},
                {ACACIA_BLUE_BED_I, BLUE_BED, DARK_OAK_BLUE_BED_I, JUNGLE_BLUE_BED_I, SPRUCE_BLUE_BED_I},
                {ACACIA_GREEN_BED_I, GREEN_BED, DARK_OAK_GREEN_BED_I, JUNGLE_GREEN_BED_I, SPRUCE_GREEN_BED_I},
                {ACACIA_YELLOW_BED_I, YELLOW_BED, DARK_OAK_YELLOW_BED_I, JUNGLE_YELLOW_BED_I, SPRUCE_YELLOW_BED_I},
                {ACACIA_BLACK_BED_I, BLACK_BED, DARK_OAK_BLACK_BED_I, JUNGLE_BLACK_BED_I, SPRUCE_BLACK_BED_I},
                {ACACIA_WHITE_BED_I, WHITE_BED, DARK_OAK_WHITE_BED_I, JUNGLE_WHITE_BED_I, SPRUCE_WHITE_BED_I},
                {ACACIA_PINK_BED_I, PINK_BED, DARK_OAK_PINK_BED_I, JUNGLE_PINK_BED_I, SPRUCE_PINK_BED_I},
                {ACACIA_PURPLE_BED_I, PURPLE_BED, DARK_OAK_PURPLE_BED_I, JUNGLE_PURPLE_BED_I, SPRUCE_PURPLE_BED_I},
                {ACACIA_ORANGE_BED_I, ORANGE_BED, DARK_OAK_ORANGE_BED_I, JUNGLE_ORANGE_BED_I, SPRUCE_ORANGE_BED_I},
                {ACACIA_LIGHT_BLUE_BED_I, LIGHT_BLUE_BED, DARK_OAK_LIGHT_BLUE_BED_I, JUNGLE_LIGHT_BLUE_BED_I, SPRUCE_LIGHT_BLUE_BED_I},
                {ACACIA_LIGHT_GRAY_BED_I, LIGHT_GRAY_BED, DARK_OAK_LIGHT_GRAY_BED_I, JUNGLE_LIGHT_GRAY_BED_I, SPRUCE_LIGHT_GRAY_BED_I},
                {ACACIA_LIME_BED_I, LIME_BED, DARK_OAK_LIME_BED_I, JUNGLE_LIME_BED_I, SPRUCE_LIME_BED_I},
                {ACACIA_MAGENTA_BED_I, MAGENTA_BED, DARK_OAK_MAGENTA_BED_I, JUNGLE_MAGENTA_BED_I, SPRUCE_MAGENTA_BED_I},
                {ACACIA_BROWN_BED_I, BROWN_BED, DARK_OAK_BROWN_BED_I, JUNGLE_BROWN_BED_I, SPRUCE_BROWN_BED_I},
                {ACACIA_CYAN_BED_I, CYAN_BED, DARK_OAK_CYAN_BED_I, JUNGLE_CYAN_BED_I, SPRUCE_CYAN_BED_I},
                {ACACIA_GRAY_BED_I, GRAY_BED, DARK_OAK_GRAY_BED_I, JUNGLE_GRAY_BED_I, SPRUCE_GRAY_BED_I}
        };

        for (Item[] coloredBed : coloredBedsByColor) {
            registerBiomeSpecificSellOffers(SHEPHERD, 3, 5, 1, 10, 12, lowMod, false, coloredBed);
            replacedTrades.add(coloredBed[1]);
        }
    }

    private static void registerMbookshelfvTrades(){
        registerBiomeSpecificSellOffers(LIBRARIAN, 1, 9, 1, 1, 12, lowMod, false, new Item[]{ACACIA_BOOKSHELF_I, BOOKSHELF, DARK_OAK_BOOKSHELF_I, JUNGLE_BOOKSHELF_I, SPRUCE_BOOKSHELF_I});

        replacedTrades.add(BOOKSHELF);
    }
    // Internal Methods below

    public static void registerBiomeSpecificSellOffers(VillagerProfession job, int level, int cost, int sellCount, int xp, int maxUse, float priceMod, boolean isEnchanted, Item[] soldVariants){
        if (soldVariants.length == 5){
            TradeOfferHelper.registerVillagerOffers(job, level,  factories -> factories.addAll(
                    List.of(new VillagerTrades.ItemListing[]{
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(itemForEmeraldsTrade(soldVariants[0], cost, sellCount, maxUse, priceMod, xp, isEnchanted), SAVANNA),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(itemForEmeraldsTrade(soldVariants[1], cost, sellCount, maxUse, priceMod, xp, isEnchanted), PLAINS),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(itemForEmeraldsTrade(soldVariants[2], cost, sellCount, maxUse, priceMod, xp, isEnchanted), SWAMP),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(itemForEmeraldsTrade(soldVariants[3], cost, sellCount, maxUse, priceMod, xp, isEnchanted), JUNGLE, DESERT),
                            VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(itemForEmeraldsTrade(soldVariants[4], cost, sellCount, maxUse, priceMod, xp, isEnchanted), TAIGA, VillagerType.SNOW)
                    })
            ));
            if (!affectedVillagers.contains(job)){
                affectedVillagers.add(job);
            }
        } else {
            LOGGER.info("Invalid amount of sold variants: " + soldVariants.length + ", should be 5. " + soldVariants[1].getDescriptionId() + " Variant trades of " + job.toString() + " were not registered.");
        }
    }

    private static VillagerTrades.ItemListing itemForEmeraldsTrade(Item item, int cost, int count, int maxUse, float mod, int xp, boolean enchant) {
        if (!enchant) {
            if (mod == lowMod) {
                return new VillagerTrades.ItemsForEmeralds(item, cost, count, maxUse, xp);
            } else {
                return new VillagerTrades.ItemsForEmeralds(new ItemStack(item), cost, count, maxUse, xp, mod);
            }
        } else {
            if (mod == lowMod) {
                return new VillagerTrades.EnchantedItemForEmeralds(item, cost, maxUse, xp);
            } else {
                return new VillagerTrades.EnchantedItemForEmeralds(item, cost, maxUse, xp, mod);
            }
        }
    }

    private static Map<VillagerType, Block> initTypeToMftMap(){
        return Map.of(  SAVANNA,    ACACIA_FLETCHING_TABLE,
                        PLAINS,     OAK_FLETCHING_TABLE,
                        SWAMP,      DARK_OAK_FLETCHING_TABLE,
                        JUNGLE,     JUNGLE_FLETCHING_TABLE,
                        DESERT,     JUNGLE_FLETCHING_TABLE,
           VillagerType.SNOW,       SPRUCE_FLETCHING_TABLE,
                        TAIGA,      SPRUCE_FLETCHING_TABLE);
    }

    private static Map<Block, Item> initMftToStickMap(){
        Map<Block, Item> MftMap = new HashMap<>(Map.of(ACACIA_FLETCHING_TABLE, ACACIA_STICK,
                BAMBOO_FLETCHING_TABLE, BAMBOO,
                CHERRY_FLETCHING_TABLE, CHERRY_STICK,
                CRIMSON_FLETCHING_TABLE, CRIMSON_STICK,
                DARK_OAK_FLETCHING_TABLE, DARK_OAK_STICK,
                JUNGLE_FLETCHING_TABLE, JUNGLE_STICK,
                MANGROVE_FLETCHING_TABLE, MANGROVE_STICK,
                OAK_FLETCHING_TABLE, STICK,
                SPRUCE_FLETCHING_TABLE, SPRUCE_STICK,
                WARPED_FLETCHING_TABLE, WARPED_STICK
        ));
        MftMap.put(Blocks.FLETCHING_TABLE, BIRCH_STICK);
        return MftMap;
    }

    private static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    private static void init(){
        isMFletchingTableVLoaded = isLoaded("lolmft");
        isMWeaponVLoaded = isLoaded("mstv-mweaponv");
        isMToolVLoaded = isLoaded("mstv-mtoolv");
        isMFrameVLoaded = isLoaded("mstv-mframev");
        isMFishingRodVLoaded = isLoaded("mstv-mfrv");
        isNemosCampfireVLoaded = isLoaded("nemos-campfires");
        isMShieldVLoaded = isLoaded("lolmsv");
        isMXShieldVLoaded = isLoaded("shields-mxsv");
        isMBedVLoaded = isLoaded("quad-lolmbdv");
        isMBookshelfVLoaded = isLoaded("lolmbv");
    }
}
