package de.pnku.mstv_base.datagen;

import de.pnku.mstv_base.item.MoreStickVariantItem;
import de.pnku.mstv_base.item.MoreStickVariantItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.item.Item;
import org.apache.commons.text.WordUtils;

public class MoreStickVariantLangGenerator extends FabricLanguageProvider {
    public MoreStickVariantLangGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        for (Item stickItem :MoreStickVariantItems.more_sticks) {
            String stickName = WordUtils.capitalizeFully(((MoreStickVariantItem) stickItem).mstvWoodType + " Stick");
            translationBuilder.add(stickItem, stickName);
        }
    }
}
