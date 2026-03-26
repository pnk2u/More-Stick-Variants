package de.pnku.mstv_base.trade;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record VillagerMissingTradesCondition(
        List<ItemEntry> items,
        Optional<Boolean> buy,
        Optional<Boolean> sell
) implements LootItemCondition {

    public record ItemEntry(
            ItemSelector selector,
            Optional<Integer> count,
            boolean exclude
    ) {
        private static final Codec<ItemSelector> SELECTOR_CODEC = Codec.STRING.comapFlatMap(
                ItemEntry::parseSelector,
                ItemSelector::asString
        );

        private static final Codec<ItemEntry> OBJECT_CODEC = RecordCodecBuilder.<ItemEntry>create(instance ->
                instance.group(
                        SELECTOR_CODEC.fieldOf("id").forGetter(ItemEntry::selector),
                        Codec.INT.optionalFieldOf("count").forGetter(ItemEntry::count),
                        Codec.BOOL.optionalFieldOf("exclude", false).forGetter(ItemEntry::exclude)
                ).apply(instance, ItemEntry::new)
        );

        public static final Codec<ItemEntry> CODEC = Codec.either(SELECTOR_CODEC, OBJECT_CODEC).xmap(
                either -> either.map(
                        selector -> new ItemEntry(selector, Optional.empty(), false),
                        entry -> entry
                ),
                entry -> {
                    if (entry.count().isEmpty() && !entry.exclude()) {
                        return Either.left(entry.selector());
                    }
                    return Either.right(entry);
                }
        );

        private static DataResult<ItemSelector> parseSelector(String raw) {
            boolean isTag = raw.startsWith("#");
            String idText = isTag ? raw.substring(1) : raw;

            // If your Identifier class does not have tryParse, replace with your available parse helper.
            Identifier id = Identifier.tryParse(idText);
            if (id == null) {
                return DataResult.error(() -> "Invalid item id/tag: " + raw);
            }

            return DataResult.success(new ItemSelector(id, isTag));
        }

        boolean matches(ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (count.isPresent() && stack.getCount() != count.get()) return false;

            if (selector.isTag()) {
                TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), selector.id());
                return stack.is(tagKey);
            } else {
                Identifier stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                return selector.id().equals(stackId);
            }
        }
    }

    public record ItemSelector(Identifier id, boolean isTag) {
        String asString() {
            return (isTag ? "#" : "") + id;
        }
    }

    public static final MapCodec<VillagerMissingTradesCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemEntry.CODEC.listOf().fieldOf("items").forGetter(VillagerMissingTradesCondition::items),
                    Codec.BOOL.optionalFieldOf("buy").forGetter(VillagerMissingTradesCondition::buy),
                    Codec.BOOL.optionalFieldOf("sell").forGetter(VillagerMissingTradesCondition::sell)
            ).apply(instance, VillagerMissingTradesCondition::new)
    );

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Villager villager)) return false;
        if (items.isEmpty()) return true;

        boolean checkBuy;
        boolean checkSell;

        if (buy.isPresent() && sell.isPresent()) {
            checkBuy = buy.get();
            checkSell = sell.get();
        } else if (buy.isPresent()) {
            checkBuy = buy.get();
            checkSell = !checkBuy;
        } else if (sell.isPresent()) {
            checkSell = sell.get();
            checkBuy = !checkSell;
        } else {
            checkBuy = true;
            checkSell = true;
        }

        MerchantOffers offers = villager.getOffers();
        if (offers.isEmpty()) return true;

        for (MerchantOffer offer : offers) {
            if (checkBuy) {
                if (isDenied(offer.getBaseCostA())) return false;
                if (isDenied(offer.getCostA())) return false;
                if (isDenied(offer.getCostB())) return false;
            }
            if (checkSell) {
                if (isDenied(offer.getResult())) return false;
            }
        }

        return true;
    }

    private boolean isDenied(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Excluded entries always win over includes.
        for (ItemEntry entry : items) {
            if (entry.exclude() && entry.matches(stack)) {
                return false;
            }
        }

        for (ItemEntry entry : items) {
            if (!entry.exclude() && entry.matches(stack)) {
                return true;
            }
        }

        return false;
    }
}
