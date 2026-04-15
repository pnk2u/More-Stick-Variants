package de.pnku.mstv_base.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Repairable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.tags.ItemTags.*;
import static net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags.*;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean wrappedCreateResultItemStackIs(ItemStack firstInputStack, Item secondInputItem, Operation<Boolean> original) {
        if (original.call(firstInputStack, secondInputItem)) return true;

        if (isSameToolType(firstInputStack, new ItemStack(secondInputItem))) {
            Repairable firstRepairable = firstInputStack.get(DataComponents.REPAIRABLE);
            Repairable secondRepairable = secondInputItem.getDefaultInstance().get(DataComponents.REPAIRABLE);
            if (firstRepairable != null) {
                return firstRepairable.equals(secondRepairable);
            }
            return true;
        }
        return false;
    }
    
    @Unique
    private boolean isSameToolType(ItemStack stack1, ItemStack stack2) {
        return (stack1.is(AXES) && stack2.is(AXES))
                || (stack1.is(HOES) && stack2.is(HOES))
                || (stack1.is(PICKAXES) && stack2.is(PICKAXES))
                || (stack1.is(SHOVELS) && stack2.is(SHOVELS))
                || (stack1.is(SWORDS) && stack2.is(SWORDS))
                || (stack1.is(BOW_TOOLS) && stack2.is(BOW_TOOLS))
                || (stack1.is(CROSSBOW_TOOLS) && stack2.is(CROSSBOW_TOOLS))
                || (stack1.is(FISHING_ROD_TOOLS) && stack2.is(FISHING_ROD_TOOLS))
                || (stack1.is(SHIELD_TOOLS) && stack2.is(SHIELD_TOOLS))
                || (stack1.is(SPEAR_TOOLS) && stack2.is(SPEAR_TOOLS));
    }
}
