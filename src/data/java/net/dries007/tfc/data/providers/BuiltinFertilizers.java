package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.Fertilizer;

public class BuiltinFertilizers extends DataManagerProvider<Fertilizer> implements Accessors
{
    public BuiltinFertilizers(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Fertilizer.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(TFCItems.ORE_POWDERS.get(Ore.SYLVITE), 0, 0, 0.5f);
        add(TFCItems.POWDERS.get(Powder.WOOD_ASH), 0, 0.1f, 0.2f);
        add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.GUANO), 0.8f, 0.5f, 0.1f);
        add(TFCItems.ORE_POWDERS.get(Ore.SALTPETER), 0.1f, 0, 0.4f);
        add(Items.BONE_MEAL, 0, 0.1f, 0);
        add(TFCItems.COMPOST, 0.2f, 0f, 0.2f);
        add(TFCItems.PURE_NITROGEN, 0.1f, 0, 0);
        add(TFCItems.PURE_PHOSPHORUS, 0, 0.1f, 0);
        add(TFCItems.PURE_POTASSIUM, 0, 0, 0.1f);
    }

    private void add(ItemLike input, float n, float p, float k)
    {
        add(nameOf(input), new Fertilizer(Ingredient.of(input), n, p, k));
    }
}
