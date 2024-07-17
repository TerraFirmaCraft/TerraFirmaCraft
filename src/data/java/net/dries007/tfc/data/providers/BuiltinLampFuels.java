package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.LampFuel;

public class BuiltinLampFuels extends DataManagerProvider<LampFuel> implements Accessors
{
    public BuiltinLampFuels(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(LampFuel.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("tallow", fluidOf(SimpleFluid.TALLOW), BlockIngredient.of(TFCTags.Blocks.LAMPS), 1800);
        add("olive_oil", fluidOf(SimpleFluid.OLIVE_OIL), BlockIngredient.of(TFCTags.Blocks.LAMPS), 8000);
        add("lava", Fluids.LAVA, BlockIngredient.of(Stream.of(
            TFCBlocks.METALS.get(Metal.BLUE_STEEL).get(Metal.BlockType.LAMP).get(),
            TFCBlocks.METALS.get(Metal.RED_STEEL).get(Metal.BlockType.LAMP).get()
            )), -1);
    }

    private void add(String name, Fluid fluid, BlockIngredient blocks, int burnRate)
    {
        add(name, new LampFuel(FluidIngredient.of(fluid), blocks, burnRate));
    }
}
