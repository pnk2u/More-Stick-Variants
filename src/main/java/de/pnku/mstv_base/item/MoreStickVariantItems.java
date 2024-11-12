package de.pnku.mstv_base.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.core.Registry;
import de.pnku.mstv_base.MoreStickVariants;

import java.util.ArrayList;
import java.util.List;


public class MoreStickVariantItems {

    // Sticks
    public static final Item ACACIA_STICK = new MoreStickVariantItem("acacia", new Item.Properties());
    public static final Item BIRCH_STICK = new MoreStickVariantItem("birch", new Item.Properties());
    public static final Item CHERRY_STICK = new MoreStickVariantItem("cherry", new Item.Properties());
    public static final Item CRIMSON_STICK = new MoreStickVariantItem("crimson", new Item.Properties().fireResistant());
    public static final Item DARK_OAK_STICK = new MoreStickVariantItem("dark_oak", new Item.Properties());
    public static final Item PALE_OAK_STICK = new MoreStickVariantItem("pale_oak", new Item.Properties());
    public static final Item JUNGLE_STICK = new MoreStickVariantItem("jungle", new Item.Properties());
    public static final Item MANGROVE_STICK = new MoreStickVariantItem("mangrove", new Item.Properties());
    public static final Item SPRUCE_STICK = new MoreStickVariantItem("spruce", new Item.Properties());
    public static final Item WARPED_STICK = new MoreStickVariantItem("warped", new Item.Properties().fireResistant());

    public static final List<Item> more_sticks = new ArrayList<>();

    public static void registerStickItems() {
        registerStickItem(ACACIA_STICK, Items.STICK);
        registerStickItem(BIRCH_STICK, ACACIA_STICK);
        registerStickItem(CHERRY_STICK, BIRCH_STICK);
        registerStickItem(CRIMSON_STICK, CHERRY_STICK);
        registerStickItem(DARK_OAK_STICK, CRIMSON_STICK);
        registerStickItem(PALE_OAK_STICK, DARK_OAK_STICK);
        registerStickItem(JUNGLE_STICK, PALE_OAK_STICK);
        registerStickItem(MANGROVE_STICK, JUNGLE_STICK);
        registerStickItem(SPRUCE_STICK, MANGROVE_STICK);
        registerStickItem(WARPED_STICK, SPRUCE_STICK);
    }

    private static void registerStickItem(Item stickItem, Item stickAfter) {
        String stickName = ((MoreStickVariantItem) stickItem).mstvWoodType + "_stick";
        Registry.register(BuiltInRegistries.ITEM, MoreStickVariants.asId(stickName), stickItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.addAfter(stickAfter, stickItem));
        more_sticks.add(stickItem);
        MoreStickVariants.LOGGER.info("Registered: " + stickName);
    }
    
}
