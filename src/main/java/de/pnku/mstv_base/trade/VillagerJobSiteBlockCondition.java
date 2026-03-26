package de.pnku.mstv_base.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

public record VillagerJobSiteBlockCondition(BlockSelector id) implements LootItemCondition {

    private static final Codec<BlockSelector> SELECTOR_CODEC = Codec.STRING.comapFlatMap(
            BlockSelector::parse,
            BlockSelector::asString
    );

    public static final MapCodec<VillagerJobSiteBlockCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SELECTOR_CODEC.fieldOf("id").forGetter(VillagerJobSiteBlockCondition::id)
            ).apply(instance, VillagerJobSiteBlockCondition::new)
    );

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public @NonNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Villager villager)) return false;

        GlobalPos globalPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).orElse(null);
        if (globalPos == null) return false;
        if (!globalPos.dimension().equals(villager.level().dimension())) return false;

        BlockPos pos = globalPos.pos();
        if (!villager.level().hasChunkAt(pos)) return false;

        BlockState state = villager.level().getBlockState(pos);

        if (id.isTag()) {
            TagKey<Block> tagKey = TagKey.create(BuiltInRegistries.BLOCK.key(), id.value());
            return state.is(tagKey);
        }

        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.value().equals(blockId);
    }

    public record BlockSelector(Identifier value, boolean isTag) {
        static DataResult<BlockSelector> parse(String raw) {
            boolean tag = raw.startsWith("#");
            String plain = tag ? raw.substring(1) : raw;

            Identifier parsed = Identifier.tryParse(plain);
            if (parsed == null) {
                return DataResult.error(() -> "Invalid block id/tag id: " + raw);
            }

            return DataResult.success(new BlockSelector(parsed, tag));
        }

        String asString() {
            return (isTag ? "#" : "") + value;
        }
    }
}