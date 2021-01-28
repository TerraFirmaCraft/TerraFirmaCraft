/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Pairs are {Flowing First, Source Second}
 */
@SuppressWarnings("unused")
public final class TFCFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);

    /**
     * Texture locations for both vanilla and TFC fluid textures
     */
    public static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");

    public static final ResourceLocation MOLTEN_STILL = Helpers.identifier("block/molten_still");
    public static final ResourceLocation MOLTEN_FLOW = Helpers.identifier("block/molten_flow");

    /**
     * A mask for fluid color - most fluids should be using this
     */
    public static final int ALPHA_MASK = 0xFF000000;

    /**
     * Fluid instances
     */
    public static final Map<Metal.Default, FluidPair<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal -> register(
        "metal/" + metal.name().toLowerCase(),
        "metal/flowing_" + metal.name().toLowerCase(),
        properties -> properties.block(TFCBlocks.METAL_FLUIDS.get(metal)).bucket(TFCItems.METAL_FLUID_BUCKETS.get(metal)).explosionResistance(100),
        FluidAttributes.builder(MOLTEN_STILL, MOLTEN_FLOW)
            .translationKey("fluid.tfc.metal." + metal.name().toLowerCase())
            .color(ALPHA_MASK | metal.getColor())
            .rarity(metal.getRarity())
            .luminosity(15)
            .density(3000)
            .viscosity(6000)
            .temperature(1300)
            .sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new
    ));

    public static final FluidPair<ForgeFlowingFluid> SALT_WATER = register(
        "salt_water",
        "flowing_salt_water",
        properties -> properties.block(TFCBlocks.SALT_WATER).bucket(TFCItems.SALT_WATER_BUCKET).canMultiply(),
        builder(WATER_STILL, WATER_FLOW, SaltWaterAttributes::new)
            .translationKey("fluid.tfc.salt_water")
            .overlay(WATER_OVERLAY)
            .color(ALPHA_MASK | 0x3F76E4)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    public static final FluidPair<ForgeFlowingFluid> SPRING_WATER = register(
        "spring_water",
        "flowing_spring_water",
        properties -> properties.block(TFCBlocks.SPRING_WATER).bucket(TFCItems.SPRING_WATER_BUCKET),
        FluidAttributes.builder(WATER_STILL, WATER_FLOW)
            .translationKey("fluid.tfc.spring_water")
            .color(ALPHA_MASK | 0x4ECBD7)
            .overlay(WATER_OVERLAY)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    /**
     * Registration helper for fluids and this stupid API
     *
     * @param sourceName  The source fluid
     * @param flowingName The flowing fluid
     * @param builder     Fluid properties
     * @param attributes  Fluid attributes
     * @return The registered fluid
     */
    private static FluidPair<ForgeFlowingFluid> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes)
    {
        return register(sourceName, flowingName, builder, attributes, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new);
    }

    private static <F extends FlowingFluid> FluidPair<F> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        // The properties needs a reference to both source and flowing
        // In addition, the properties builder cannot be invoked statically, as it has hard references to registry objects, which may not be populated based on class load order - it must be invoked at registration time.
        // So, first we prepare the source and flowing registry objects, referring to the properties box (which will be opened during registration, which is ok)
        // Then, we populate the properties box lazily, (since it's a mutable lazy), so the properties inside are only constructed when the box is opened (again, during registration)
        final Mutable<Lazy<ForgeFlowingFluid.Properties>> propertiesBox = new MutableObject<>();
        final RegistryObject<F> source = register(sourceName, () -> sourceFactory.apply(propertiesBox.getValue().get()));
        final RegistryObject<F> flowing = register(flowingName, () -> flowingFactory.apply(propertiesBox.getValue().get()));

        propertiesBox.setValue(Lazy.of(() -> {
            ForgeFlowingFluid.Properties lazyProperties = new ForgeFlowingFluid.Properties(source, flowing, attributes);
            builder.accept(lazyProperties);
            return lazyProperties;
        }));

        return new FluidPair<>(flowing, source);
    }

    private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> factory)
    {
        return FLUIDS.register(name, factory);
    }

    /**
     * Helper for the stupid protected constructor on {@link FluidAttributes.Builder}
     */
    private static FluidAttributes.Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory)
    {
        return new FluidAttributes.Builder(stillTexture, flowingTexture, factory) {};
    }

    /**
     * This exists for simpler labels and type parameters
     */
    public static class FluidPair<F extends FlowingFluid> extends Pair<RegistryObject<F>, RegistryObject<F>>
    {
        private FluidPair(RegistryObject<F> first, RegistryObject<F> second)
        {
            super(first, second);
        }

        public F getFlowing()
        {
            return getFirst().get();
        }

        public F getSource()
        {
            return getSecond().get();
        }

        public BlockState getSourceBlock()
        {
            return getSource().defaultFluidState().createLegacyBlock();
        }
    }
}