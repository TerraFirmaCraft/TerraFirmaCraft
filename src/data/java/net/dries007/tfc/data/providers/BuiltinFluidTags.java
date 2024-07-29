package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.crafting.CompoundFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SingleFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.fluids.FluidHolder;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.Drinkable;

import static net.dries007.tfc.common.TFCTags.Fluids.*;
import static net.dries007.tfc.common.fluids.SimpleFluid.*;

public class BuiltinFluidTags extends TagsProvider<Fluid> implements Accessors
{
    private final CompletableFuture<?> before;

    public BuiltinFluidTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<?> before)
    {
        super(event.getGenerator().getPackOutput(), Registries.FLUID, provider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
        this.before = before;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return before.thenCompose(v -> super.createContentsProvider());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // Any = including flowing
        // Fresh = Only fresh water
        // Infinite = All infinite water fluids
        tag(ANY_FRESH_WATER).add(Fluids.WATER, Fluids.FLOWING_WATER, TFCFluids.RIVER_WATER.get());
        tag(ANY_INFINITE_WATER)
            .addTag(ANY_FRESH_WATER)
            .add(
                TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getFlowing(),
                TFCFluids.SPRING_WATER.getSource(), TFCFluids.SPRING_WATER.getFlowing());
        tag(FRESH_WATER).add(Fluids.WATER);
        tag(INFINITE_WATER)
            .addTag(FRESH_WATER)
            .add(
                TFCFluids.SALT_WATER.getSource(),
                TFCFluids.SPRING_WATER.getSource());

        tag(MIXABLE).addTag(WATER_LIKE);
        tag(HYDRATING).addTag(ANY_FRESH_WATER);

        tag(ALCOHOLS).add(
            fluidOf(BEER),
            fluidOf(CIDER),
            fluidOf(RUM),
            fluidOf(SAKE),
            fluidOf(VODKA),
            fluidOf(WHISKEY),
            fluidOf(CORN_WHISKEY),
            fluidOf(RYE_WHISKEY));
        tag(MOLTEN_METALS).add(TFCFluids.METALS);

        Drinkable.MANAGER.getValues().forEach(drink -> tag(DRINKABLES).add(drink.ingredient()));
        tag(INGREDIENTS)
            .addTag(DRINKABLES)
            .add(TFCFluids.SIMPLE_FLUIDS)
            .add(TFCFluids.COLORED_FLUIDS);

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
        tag(USABLE_IN_BELL_MOLD).add(
            fluidOf(Metal.BRONZE),
            fluidOf(Metal.GOLD),
            fluidOf(Metal.BRASS));
    }

    @Override
    protected FluidTagAppender tag(TagKey<Fluid> tag)
    {
        return new FluidTagAppender(getOrCreateRawBuilder(tag), modId);
    }

    @SuppressWarnings("UnusedReturnValue")
    static class FluidTagAppender extends TagAppender<Fluid> implements Accessors
    {
        FluidTagAppender(TagBuilder builder, String modId)
        {
            super(builder, modId);
        }

        FluidTagAppender add(Fluid... fluids) { return add(Arrays.stream(fluids)); }
        FluidTagAppender add(Stream<Fluid> fluids) { fluids.forEach(b -> add(key(b))); return this; }
        FluidTagAppender add(Map<?, ? extends FluidHolder<? extends Fluid>> fluids) { fluids.values().forEach(v -> add(v.getSource())); return this; }
        FluidTagAppender add(FluidIngredient ingredient)
        {
            switch (ingredient)
            {
                case TagFluidIngredient tag -> addTag(tag.tag());
                case SingleFluidIngredient item -> add(item.fluid().value());
                case CompoundFluidIngredient comp -> comp.children().forEach(this::add);
                default -> throw new AssertionError("Unhandled ingredient type: " + ingredient);
            }
            return this;
        }

        @Override public FluidTagAppender addTag(TagKey<Fluid> tag) { return (FluidTagAppender) super.addTag(tag); }

        private ResourceKey<Fluid> key(Fluid fluid)
        {
            return BuiltInRegistries.FLUID.getResourceKey(fluid).orElseThrow();
        }
    }
}
