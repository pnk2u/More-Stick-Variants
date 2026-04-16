package de.pnku.mstv_base.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.BOWS;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.SHIELDS;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.SPEARS;
import static net.minecraft.tags.ItemTags.*;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean wrappedCreateResultItemStackIs(ItemStack firstInputStack, Item secondInputItem, Operation<Boolean> original) {
        if (original.call(firstInputStack, secondInputItem)) return true;

        if (isSameEnchantabilityType(firstInputStack, new ItemStack(secondInputItem))) {
            if (firstInputStack.getItem() instanceof TieredItem firstTiered && secondInputItem instanceof TieredItem secondTiered) {
                return firstTiered.getTier() == secondTiered.getTier();
            }
            return true;
        }
        return false;
    }
    
    @Unique
    private boolean isSameEnchantabilityType(ItemStack stack1, ItemStack stack2) {
        List <TagKey<Item>> enchantableTags = new ArrayList<>();
        enchantableTags.add(AXES); enchantableTags.add(HOES); enchantableTags.add(PICKAXES); enchantableTags.add(SHOVELS); enchantableTags.add(SWORDS);
        enchantableTags.add(BOWS); enchantableTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("c", "crossbows"))); enchantableTags.add(TagKey.create(Registries.ITEM, new ResourceLocation("c", "fishing_rods")));
        enchantableTags.add(SHIELDS); enchantableTags.add(SPEARS);
        for (TagKey<Item> tag : enchantableTags) {
            if (stack1.is(tag) && stack2.is(tag)) return true;
        }
        return false;
    }
}
