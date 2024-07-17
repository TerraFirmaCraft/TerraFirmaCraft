package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.data.DataAccessor;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.Drinkable;

import static net.dries007.tfc.common.TFCTags.Fluids.*;
import static net.dries007.tfc.common.fluids.SimpleFluid.*;

public class BuiltinFluidTags extends FluidTagsProvider implements Accessors
{
    private final DataAccessor<Drinkable> drinkables;

    public BuiltinFluidTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> provider, DataAccessor<Drinkable> drinkables)
    {
        super(event.getGenerator().getPackOutput(), provider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
        this.drinkables = drinkables;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return drinkables.future().thenCompose(v -> super.createContentsProvider());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // Any = including flowing
        // Fresh = Only fresh water
        // Infinite = All infinite water fluids
        tag(ANY_FRESH_WATER).add(Fluids.WATER, Fluids.FLOWING_WATER, TFCFluids.RIVER_WATER.get());
        tag(ANY_INFINITE_WATER).addTag(ANY_FRESH_WATER).add(
            TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getFlowing(),
            TFCFluids.SPRING_WATER.getSource(), TFCFluids.SPRING_WATER.getFlowing());
        tag(FRESH_WATER).add(Fluids.WATER);
        tag(INFINITE_WATER).addTag(FRESH_WATER).add(
            TFCFluids.SALT_WATER.getSource(),
            TFCFluids.SPRING_WATER.getSource());

        tag(ALCOHOLS).add(
            fluidOf(BEER),
            fluidOf(CIDER),
            fluidOf(RUM),
            fluidOf(SAKE),
            fluidOf(VODKA),
            fluidOf(WHISKEY),
            fluidOf(CORN_WHISKEY),
            fluidOf(RYE_WHISKEY));

        tag(DRINKABLES, drinkables.all().flatMap(v -> RecipeHelpers.stream(v.ingredient())));
        tag(MOLTEN_METALS, Arrays.stream(Metal.values()).map(m -> TFCFluids.METALS.get(m).getSource()));
        tag(INGREDIENTS).addTag(DRINKABLES);
        tag(INGREDIENTS, Arrays.stream(SimpleFluid.values()).map(this::fluidOf));
        tag(INGREDIENTS, Arrays.stream(DyeColor.values()).map(this::fluidOf));

        tag(USABLE_IN_POT).addTag(INGREDIENTS);
        tag(USABLE_IN_JUG).addTag(DRINKABLES);
        tag(USABLE_IN_WOODEN_BUCKET).addTag(INGREDIENTS);
        tag(USABLE_IN_RED_STEEL_BUCKET).addTag(INGREDIENTS).add(Fluids.LAVA);
        tag(USABLE_IN_BLUE_STEEL_BUCKET).addTag(INGREDIENTS).add(Fluids.LAVA);
        tag(USABLE_IN_BARREL).addTag(INGREDIENTS);
        tag(USABLE_IN_SCRIBING_TABLE).add(fluidOf(DyeColor.BLACK));
        tag(USABLE_IN_SLUICE).addTag(ANY_FRESH_WATER);
        tag(USABLE_IN_INGOT_MOLD).addTag(MOLTEN_METALS);
        tag(USABLE_IN_TOOL_HEAD_MOLD).add(
            fluidOf(Metal.COPPER),
            fluidOf(Metal.BRONZE),
            fluidOf(Metal.BLACK_BRONZE),
            fluidOf(Metal.BISMUTH_BRONZE));
    }

    private void tag(TagKey<Fluid> tag, Stream<Fluid> blocks)
    {
        blocks.forEach(tag(tag)::add);
    }
}
