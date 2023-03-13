/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;

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
    public static final Map<Metal.Default, FlowingFluidRegistryObject<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal -> register(
        "metal/" + metal.getSerializedName(),
        "metal/flowing_" + metal.getSerializedName(),
        properties -> properties.block(TFCBlocks.METAL_FLUIDS.get(metal)).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.asType(metal))).explosionResistance(100),
        FluidAttributes.builder(MOLTEN_STILL, MOLTEN_FLOW)
            .translationKey("fluid.tfc.metal." + metal.getSerializedName())
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

    public static final FlowingFluidRegistryObject<ForgeFlowingFluid> SALT_WATER = register(
        "salt_water",
        "flowing_salt_water",
        properties -> properties.block(TFCBlocks.SALT_WATER).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.SALT_WATER)).canMultiply(),
        new FluidAttributes.Builder(WATER_STILL, WATER_FLOW, SaltWaterAttributes::new) {}
            .translationKey("fluid.tfc.salt_water")
            .overlay(WATER_OVERLAY)
            .color(ALPHA_MASK | 0x3F76E4)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    public static final FlowingFluidRegistryObject<ForgeFlowingFluid> SPRING_WATER = register(
        "spring_water",
        "flowing_spring_water",
        properties -> properties.block(TFCBlocks.SPRING_WATER).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.SPRING_WATER)).canMultiply(),
        FluidAttributes.builder(WATER_STILL, WATER_FLOW)
            .translationKey("fluid.tfc.spring_water")
            .color(ALPHA_MASK | 0x4ECBD7)
            .overlay(WATER_OVERLAY)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    public static final RegistryObject<RiverWaterFluid> RIVER_WATER = register("river_water", RiverWaterFluid::new);

    public static final Map<SimpleFluid, FlowingFluidRegistryObject<ForgeFlowingFluid>> SIMPLE_FLUIDS = Helpers.mapOfKeys(SimpleFluid.class, fluid -> register(
        fluid.getId(),
        "flowing_" + fluid.getId(),
        properties -> properties.block(TFCBlocks.SIMPLE_FLUIDS.get(fluid)).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.asType(fluid))),
        FluidAttributes.builder(WATER_STILL, WATER_FLOW)
            .translationKey("fluid.tfc." + fluid.getId())
            .color(fluid.isTransparent() ? ALPHA_MASK | fluid.getColor() : fluid.getColor())
            .overlay(WATER_OVERLAY)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    public static final Map<Alcohol, FlowingFluidRegistryObject<ForgeFlowingFluid>> ALCOHOLS = Helpers.mapOfKeys(Alcohol.class, fluid -> register(
        fluid.getId(),
        "flowing_" + fluid.getId(),
        properties -> properties.block(TFCBlocks.ALCOHOLS.get(fluid)).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.asType(fluid))),
        FluidAttributes.builder(WATER_STILL, WATER_FLOW)
            .translationKey("fluid.tfc." + fluid.getId())
            .color(fluid.getColor())
            .overlay(WATER_OVERLAY)
            .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    public static final Map<DyeColor, FlowingFluidRegistryObject<ForgeFlowingFluid>> COLORED_FLUIDS = Helpers.mapOfKeys(DyeColor.class, color -> {
        return register(
            color.getName() + "_dye",
            "flowing_" + color.getName() + "_dye",
            properties -> properties.block(TFCBlocks.COLORED_FLUIDS.get(color)).bucket(TFCItems.FLUID_BUCKETS.get(FluidType.asType(color))),
            FluidAttributes.builder(WATER_STILL, WATER_FLOW)
                .translationKey("fluid.tfc." + color.getName() + "_dye")
                .color(dyeColorToInt(color))
                .overlay(WATER_OVERLAY)
                .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY),
            MixingFluid.Source::new,
            MixingFluid.Flowing::new
        );
    });

    public static int dyeColorToInt(DyeColor dye)
    {
        float[] colors = dye.getTextureDiffuseColors();
        return new Color(colors[0], colors[1], colors[2]).getRGB();
    }

    /**
     * Registration helper for fluids and this stupid API
     *
     * @param sourceName  The source fluid
     * @param flowingName The flowing fluid
     * @param builder     Fluid properties
     * @param attributes  Fluid attributes
     * @return The registered fluid
     */
    private static FlowingFluidRegistryObject<ForgeFlowingFluid> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes)
    {
        return RegistrationHelpers.registerFluid(FLUIDS, sourceName, flowingName, builder, attributes);
    }

    private static <F extends FlowingFluid> FlowingFluidRegistryObject<F> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        return RegistrationHelpers.registerFluid(FLUIDS, sourceName, flowingName, builder, attributes, sourceFactory, flowingFactory);
    }

    private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> factory)
    {
        return FLUIDS.register(name, factory);
    }

}