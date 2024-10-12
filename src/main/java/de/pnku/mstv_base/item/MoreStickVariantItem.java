package de.pnku.mstv_base.item;

import de.pnku.mstv_base.MoreStickVariants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;


public class MoreStickVariantItem extends Item {
    public final String mstvWoodType;


    public MoreStickVariantItem(String mstvWoodType, Item.Properties properties) {
        super(properties.setId(ResourceKey.create(Registries.ITEM, MoreStickVariants.asId(mstvWoodType + "_stick"))));
        this.mstvWoodType = mstvWoodType;
    }

    public static Item getPlanksItem(String planksWood) {
        switch (planksWood) {
            case "acacia" -> {
                return Items.ACACIA_PLANKS;
            }
            case "bamboo" -> {
                return Items.BAMBOO_PLANKS;
            }
            case "birch" -> {
                return Items.BIRCH_PLANKS;
            }
            case "cherry" -> {
                return Items.CHERRY_PLANKS;
            }
            case "crimson" -> {
                return Items.CRIMSON_PLANKS;
            }
            case "dark_oak" -> {
                return Items.DARK_OAK_PLANKS;
            }
            case "jungle" -> {
                return Items.JUNGLE_PLANKS;
            }
            case "mangrove" -> {
                return Items.MANGROVE_PLANKS;
            }
            case "oak" -> {
                return Items.OAK_PLANKS;
            }
            case "spruce" -> {
                return Items.SPRUCE_PLANKS;
            }
            case "warped" -> {
                return Items.WARPED_PLANKS;
            }
            case null, default -> {
                return null;
            }
        }
    }
    public static Item getStickItem(String stickWood) {
        switch (stickWood) {
            case "acacia" -> {
                return MoreStickVariantItems.ACACIA_STICK;
            }
            case "bamboo" -> {
                return Items.BAMBOO;
            }
            case "birch" -> {
                return MoreStickVariantItems.BIRCH_STICK;
            }
            case "cherry" -> {
                return MoreStickVariantItems.CHERRY_STICK;
            }
            case "crimson" -> {
                return MoreStickVariantItems.CRIMSON_STICK;
            }
            case "dark_oak" -> {
                return MoreStickVariantItems.DARK_OAK_STICK;
            }
            case "jungle" -> {
                return MoreStickVariantItems.JUNGLE_STICK;
            }
            case "mangrove" -> {
                return MoreStickVariantItems.MANGROVE_STICK;
            }
            case "spruce" -> {
                return MoreStickVariantItems.SPRUCE_STICK;
            }
            case "warped" -> {
                return MoreStickVariantItems.WARPED_STICK;
            }
            case null, default -> {
                return Items.STICK;
            }
        }
    }
}
