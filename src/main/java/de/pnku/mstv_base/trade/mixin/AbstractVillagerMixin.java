package de.pnku.mstv_base.trade.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import de.pnku.mstv_base.MoreStickVariants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.pnku.mstv_base.item.MoreStickVariantItems.*;
import static de.pnku.mstv_base.trade.MstvVillagerTrades.*;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

    @Unique
    boolean hasLocalFletchingTable = false;
    @Unique
    boolean hasForeignStickTrade = false;
    @Unique
    boolean hasNonStickTrade = false;
    @Unique
    Block myFletchingTable = Blocks.AIR;
    @Unique
    Item localStick = Items.AIR;
    @Unique
    List<Item> initializedTrades = new ArrayList<>();
    @Unique
    private Item getDefaultStick(ResourceKey<VillagerType> typeKey){
            return (!FabricLoader.getInstance().isModLoaded("lolmft") && !typeKey.equals(VillagerType.PLAINS)) ? Items.STICK : BIRCH_STICK;
    }

    @Unique
    AbstractVillager abstractVillager = (AbstractVillager) (Object) this;

    @Redirect(method = "addOffersFromItemListings", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;add(Ljava/lang/Object;)Z"))
    protected boolean redirectedAddOffersFromItemListings(MerchantOffers givenOffers, Object object, @Local(ordinal = 1) LocalIntRef counter) {
        if (abstractVillager instanceof Villager villager && object instanceof MerchantOffer addedOffer) {
            VillagerData vData = villager.getVillagerData();
            ItemStack playerOffer = addedOffer.getBaseCostA();
            ItemStack villagerOffer = addedOffer.getResult();
            int vLevel = vData.level();
            ResourceKey<VillagerProfession> professionKey = vData.profession().unwrapKey().isPresent() ? vData.profession().unwrapKey().get() : VillagerProfession.NONE;
            ResourceKey<VillagerType> typeKey = vData.type().unwrapKey().isPresent() ? vData.type().unwrapKey().get() : VillagerType.PLAINS;
            if (affectedVillagers.contains(professionKey)) {
                boolean paintingSpecialCase = FabricLoader.getInstance().isModLoaded("mstv-mframev")
                                            && professionKey.equals(VillagerProfession.SHEPHERD)
                                            && villagerOffer.getItem().equals(Items.PAINTING);

                boolean crossbowSpecialCase = FabricLoader.getInstance().isModLoaded("mstv-mweaponv")
                                            && typeKey.equals(VillagerType.SWAMP)
                                            && villagerOffer.getItem().equals(Items.CROSSBOW);

                boolean shieldSpecialCase   = FabricLoader.getInstance().isModLoaded("lolmsv")
                                            && (typeKey.equals(VillagerType.TAIGA) || typeKey.equals(VillagerType.SNOW))
                                            && villagerOffer.getItem().equals(Items.SHIELD);

                boolean defaultCase = !typeKey.equals(VillagerType.PLAINS)
                                    && !crossbowSpecialCase
                                    && !shieldSpecialCase;

                if (    (defaultCase
                    || (!shieldSpecialCase && villagerOffer.getItem().equals(Items.SHIELD))
                    || (!crossbowSpecialCase && villagerOffer.getItem().equals(Items.CROSSBOW))
                    || (paintingSpecialCase))
                && replacedTrades.contains(villagerOffer.getItem())) {
                    counter.set(counter.get() - 1);
                    return false;
                }

                if (this.initializedTrades.contains(villagerOffer.getItem()) && !villagerOffer.getItem().equals(Items.EMERALD)) {
                    counter.set(counter.get() - 1);
                    return false;
                }

                if (counter.get() >= 1) {
                    this.initializedTrades.clear();
                }
            }
            if (professionKey == VillagerProfession.FLETCHER && vLevel == 1) {
                if (this.hasForeignStickTrade && this.hasNonStickTrade) {
                    this.hasForeignStickTrade = false;
                    this.hasNonStickTrade = false;
                    counter.set(2);
                    return true;
                } else {
                    Optional<GlobalPos> jobSite = ((Villager) abstractVillager).getBrain().getMemory(MemoryModuleType.JOB_SITE);
                    if (jobSite.isPresent() && this.myFletchingTable.equals(Blocks.AIR)) {
                        this.myFletchingTable = abstractVillager.level().getBlockState(jobSite.get().pos()).getBlock();
                    }
                    this.localStick = fletcherLocalSticksBuyOffers.get(typeKey);
                    if (typeKey != VillagerType.PLAINS) {
                        if (playerOffer.getItem() == Items.STICK && playerOffer.getCount() == 32) {
                            counter.set(0);
                            return false;
                        }
                    }
                    if (all_sticks.contains((playerOffer.getItem())) && !(fletcherLocalSticksBuyOffers.get(typeKey).equals(playerOffer.getItem())) && !fletchingTableToStick.getOrDefault(this.myFletchingTable, this.getDefaultStick(typeKey)).equals(playerOffer.getItem()) && !this.hasForeignStickTrade) {
                        this.hasForeignStickTrade = true;
                        counter.set(0);
                        return givenOffers.add(addedOffer);
                    }
                    if (!all_sticks.contains((playerOffer.getItem())) && !this.hasNonStickTrade) {
                        this.hasNonStickTrade = true;
                        counter.set(0);
                        return givenOffers.add(addedOffer);
                    }
                    counter.set(0);
                    return false;
                }
            }
            this.initializedTrades.add(villagerOffer.getItem());
        }
        return givenOffers.add((MerchantOffer) object);
    }


    @Inject(method = "addOffersFromItemListings", at = @At("TAIL"))
    protected void injectedAddOffersFromItemListingsAtTail(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] newTrades, int maxNumbers, CallbackInfo ci) {
        if (abstractVillager instanceof Villager villager) {
            VillagerData vData = villager.getVillagerData();
            Item tableBasedStick;
            int vLevel = vData.level();
            ResourceKey<VillagerProfession> professionKey = vData.profession().unwrapKey().isPresent() ? vData.profession().unwrapKey().get() : VillagerProfession.NONE;
            ResourceKey<VillagerType> typeKey = vData.type().unwrapKey().isPresent() ? vData.type().unwrapKey().get() : VillagerType.PLAINS;
            if (professionKey == VillagerProfession.FLETCHER && vLevel == 1) {
                MerchantOffer tableBasedOffer;
                if (fletcherLocalFletchingTable.containsKey(typeKey)) {
                Block fletchingTable = this.myFletchingTable;
                    tableBasedStick = fletchingTableToStick.getOrDefault(fletchingTable, this.getDefaultStick(typeKey));
                    if (fletcherLocalFletchingTable.get(typeKey).equals(fletchingTable)) {
                        this.hasLocalFletchingTable = true;
                    }
                } else {tableBasedStick = this.getDefaultStick(typeKey);}
                tableBasedOffer = new MerchantOffer(new ItemCost(tableBasedStick, !this.getDefaultStick(typeKey).equals(Items.STICK) ? 24 : 32), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
                Item localStick = this.localStick == null ? Items.AIR : this.localStick;
                MerchantOffer localBasedOffer = new MerchantOffer(new ItemCost(localStick, 32), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
                if(givenMerchantOffers.stream().noneMatch(merchantOffer -> merchantOffer.getBaseCostA().getItem().equals(tableBasedOffer.getBaseCostA().getItem()))) {
                    givenMerchantOffers.add(tableBasedOffer);
                } else {
                    MoreStickVariants.LOGGER.info("Fletching Table based Stick trade was already added as Foreign Stick Trade. This should not happen!");
                }
                if (!this.hasLocalFletchingTable) {
                    givenMerchantOffers.add(localBasedOffer);
                } else {
                    this.hasLocalFletchingTable = false;
                }
            }
        }
        this.myFletchingTable = Blocks.AIR;
        this.localStick = Items.AIR;
    }
}
