package de.pnku.mstv_base.trade.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.pnku.mstv_base.MoreStickVariants.LOGGER;
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
    int counter;
    @Unique
    private boolean isMFletchingTableVLoaded(){
        return FabricLoader.getInstance().isModLoaded("lolmft");
    }

    @Unique
    AbstractVillager abstractVillager = (AbstractVillager) (Object) this;

    // Re-implemented addOffersFromItemListings from 1.20.2-pre1+

    @Inject(method = "addOffersFromItemListings", at = @At("HEAD"), cancellable = true)
    public void addOffersFromItemListings(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] newTrades, int maxNumbers, CallbackInfo ci) {
        if (abstractVillager instanceof Villager villager) {
            VillagerData vData = villager.getVillagerData();
            if (affectedVillagers.contains(vData.getProfession())) {
                ArrayList<VillagerTrades.ItemListing> arrayList = Lists.newArrayList(newTrades);
                int i = 0;

                while (i < maxNumbers && !arrayList.isEmpty()) {
                    MerchantOffer merchantOffer = ((VillagerTrades.ItemListing) arrayList.remove(abstractVillager.getRandom().nextInt(arrayList.size()))).getOffer(abstractVillager, abstractVillager.getRandom());
                    if (merchantOffer != null) {
                        i = this.mstv$redirectedAddOffersFromItemListings(givenMerchantOffers, merchantOffer, i, villager, vData);
                        ++i;
                    }
                }

                this.mstv$injectedAddOffersFromItemListingsAtTail(givenMerchantOffers, newTrades, maxNumbers, (CallbackInfo) null);
                ci.cancel();
            }
        }
    }

    @Unique
    protected int mstv$redirectedAddOffersFromItemListings(MerchantOffers givenOffers, MerchantOffer addedOffer, int counter, Villager villager, VillagerData vData) {
        ItemStack playerOffer = addedOffer.getBaseCostA();
        ItemStack villagerOffer = addedOffer.getResult();
        if (affectedVillagers.contains(vData.getProfession())) {
            boolean paintingSpecialCase = FabricLoader.getInstance().isModLoaded("mstv-mframev")
                                        && vData.getProfession().equals(VillagerProfession.SHEPHERD)
                                        && villagerOffer.getItem().equals(Items.PAINTING);

            boolean crossbowSpecialCase = FabricLoader.getInstance().isModLoaded("mstv-mweaponv")
                                        && vData.getType().equals(VillagerType.SWAMP)
                                        && villagerOffer.getItem().equals(Items.CROSSBOW);

            boolean shieldSpecialCase   = FabricLoader.getInstance().isModLoaded("lolmsv")
                                        && (vData.getType().equals(VillagerType.TAIGA) || vData.getType().equals(VillagerType.SNOW))
                                        && villagerOffer.getItem().equals(Items.SHIELD);

            boolean defaultCase = !vData.getType().equals(VillagerType.PLAINS)
                                && !crossbowSpecialCase
                                && !shieldSpecialCase;

            if (    (defaultCase
                || (!shieldSpecialCase && villagerOffer.getItem().equals(Items.SHIELD))
                || (!crossbowSpecialCase && villagerOffer.getItem().equals(Items.CROSSBOW))
                || (paintingSpecialCase))
            && replacedTrades.contains(villagerOffer.getItem())) {
                counter = (counter - 1);
                return counter;
            }

            if (this.initializedTrades.contains(villagerOffer.getItem()) && !villagerOffer.getItem().equals(Items.EMERALD)) {
                counter = (counter - 1);
                return counter;
            }

            if (counter >= 1) {
                this.initializedTrades.clear();
            }
        }
        if (vData.getProfession() == VillagerProfession.FLETCHER && vData.getLevel() == 1) {
            if (this.hasForeignStickTrade && this.hasNonStickTrade) {
                this.hasForeignStickTrade = false;
                this.hasNonStickTrade = false;
                counter = 2;
                return counter;
            } else {
                Optional<GlobalPos> jobSite = ((Villager) abstractVillager).getBrain().getMemory(MemoryModuleType.JOB_SITE);
                if (jobSite.isPresent() && this.myFletchingTable.equals(Blocks.AIR)) {
                    this.myFletchingTable = abstractVillager.level().getBlockState(jobSite.get().pos()).getBlock();
                }
                this.localStick = fletcherLocalSticksBuyOffers.get(vData.getType());
                if (vData.getType() != VillagerType.PLAINS) {
                    if (playerOffer.getItem() == Items.STICK && playerOffer.getCount() == 32) {

                        counter = 0;
                        return counter;
                    }
                }
                if (all_sticks.contains((playerOffer.getItem())) && !(fletcherLocalSticksBuyOffers.get(vData.getType()).equals(playerOffer.getItem())) && !fletchingTableToStick.getOrDefault(this.myFletchingTable, this.isMFletchingTableVLoaded() ? BIRCH_STICK : Items.STICK).equals(playerOffer.getItem()) && !this.hasForeignStickTrade) {
                    this.hasForeignStickTrade = true;
                    counter = 0;
                    givenOffers.add((MerchantOffer) addedOffer);
                    return counter;
                }
                if (!all_sticks.contains((playerOffer.getItem())) && !this.hasNonStickTrade) {
                    this.hasNonStickTrade = true;
                    counter = 0;
                    givenOffers.add((MerchantOffer) addedOffer);
                    return counter;
                }
                counter = 0;
                return counter;
            }
        }
        this.initializedTrades.add(villagerOffer.getItem());
        givenOffers.add(addedOffer);
        return counter;
    }


    @Unique
    protected void mstv$injectedAddOffersFromItemListingsAtTail(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] newTrades, int maxNumbers, CallbackInfo ci) {
        if (abstractVillager instanceof Villager villager) {
            VillagerData vData = villager.getVillagerData();
            Item tableBasedStick;
            if (vData.getProfession() == VillagerProfession.FLETCHER && vData.getLevel() == 1) {
                MerchantOffer tableBasedOffer;
                if (fletcherLocalFletchingTable.containsKey(vData.getType())) {
                Block fletchingTable = this.myFletchingTable;
                    tableBasedStick = fletchingTableToStick.getOrDefault(fletchingTable, this.isMFletchingTableVLoaded() ? BIRCH_STICK : Items.STICK);
                    if (fletcherLocalFletchingTable.get(vData.getType()).equals(fletchingTable)) {
                        this.hasLocalFletchingTable = true;
                    }
                } else {tableBasedStick = this.isMFletchingTableVLoaded() ? BIRCH_STICK : Items.STICK;}
                tableBasedOffer = new MerchantOffer(new ItemStack(tableBasedStick, this.isMFletchingTableVLoaded() ? 24 : 32), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
                Item localStick = this.localStick == null ? Items.AIR : this.localStick;
                MerchantOffer localBasedOffer = new MerchantOffer(new ItemStack(localStick, 32), new ItemStack(Items.EMERALD), 16, 2, 0.05F);
                if(givenMerchantOffers.stream().noneMatch(merchantOffer -> merchantOffer.getBaseCostA().getItem().equals(tableBasedOffer.getBaseCostA().getItem()))) {
                    givenMerchantOffers.add((MerchantOffer) tableBasedOffer);
                } else {
                    LOGGER.info("Fletching Table based Stick trade was already added as Foreign Stick Trade. This should not happen!");
                }
                if (!this.hasLocalFletchingTable) {
                    givenMerchantOffers.add((MerchantOffer) localBasedOffer);
                } else {
                    this.hasLocalFletchingTable = false;
                }
            }
        }
        this.myFletchingTable = Blocks.AIR;
        this.localStick = Items.AIR;
    }
}
