/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static net.dries007.tfc.TerraFirmaCraft.*;


@SuppressWarnings("unused")
public final class TFCFluids
{
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MOD_ID);

    /**
     * Texture locations for both vanilla and TFC fluid textures
     */
    public static final ResourceLocation WATER_STILL = Helpers.identifierMC("block/water_still");
    public static final ResourceLocation WATER_FLOW = Helpers.identifierMC("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY = Helpers.identifierMC("block/water_overlay");
    /** @see net.minecraft.client.renderer.ScreenEffectRenderer#UNDERWATER_LOCATION */
    public static final ResourceLocation UNDERWATER_LOCATION = Helpers.identifierMC("textures/misc/underwater.png");

    public static final ResourceLocation MOLTEN_STILL = Helpers.identifier("block/molten_still");
    public static final ResourceLocation MOLTEN_FLOW = Helpers.identifier("block/molten_flow");

    /**
     * A mask for fluid color - most fluids should be using this
     */
    public static final int ALPHA_MASK = 0xFF000000;

    /**
     * Fluid instances
     */
    public static final Map<Metal.Default, FluidHolder<BaseFlowingFluid>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal -> register(
        "metal/" + metal.getSerializedName(),
        properties -> properties
            .block(TFCBlocks.METAL_FLUIDS.get(metal))
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.asType(metal)))
            .explosionResistance(100),
        lavaLike()
            .descriptionId("fluid.tfc.metal." + metal.getSerializedName())
            .rarity(metal.rarity()),
        new FluidTypeClientProperties(ALPHA_MASK | metal.getColor(), MOLTEN_STILL, MOLTEN_FLOW, null, null),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new
    ));

    public static final FluidHolder<BaseFlowingFluid> SALT_WATER = register(
        "salt_water",
        properties -> properties
            .block(TFCBlocks.SALT_WATER)
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.SALT_WATER)),
        waterLike()
            .descriptionId("fluid.tfc.salt_water"),
        new FluidTypeClientProperties(
            ALPHA_MASK | 0x3F76E4, (level, pos) -> level.getBlockTint(pos, TFCColors.SALT_WATER) | TFCFluids.ALPHA_MASK,
            WATER_STILL, WATER_FLOW, WATER_OVERLAY, UNDERWATER_LOCATION
        ),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    public static final FluidHolder<BaseFlowingFluid> SPRING_WATER = register(
        "spring_water",
        properties -> properties
            .block(TFCBlocks.SPRING_WATER)
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.SPRING_WATER)),
        waterLike()
            .descriptionId("fluid.tfc.spring_water"),
        new FluidTypeClientProperties(ALPHA_MASK | 0x4ECBD7, WATER_STILL, WATER_FLOW, WATER_OVERLAY, UNDERWATER_LOCATION),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    );

    public static final DeferredHolder<Fluid, RiverWaterFluid> RIVER_WATER = FLUIDS.register("river_water", RiverWaterFluid::new);

    public static final Map<SimpleFluid, FluidHolder<BaseFlowingFluid>> SIMPLE_FLUIDS = Helpers.mapOfKeys(SimpleFluid.class, fluid -> register(
        fluid.getId(),
        properties -> properties
            .block(TFCBlocks.SIMPLE_FLUIDS.get(fluid))
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.asType(fluid))),
        waterLike()
            .descriptionId("fluid.tfc." + fluid.getId())
            .canConvertToSource(false),
        new FluidTypeClientProperties(fluid.isTransparent() ? ALPHA_MASK | fluid.getColor() : fluid.getColor(), WATER_STILL, WATER_FLOW, WATER_OVERLAY, UNDERWATER_LOCATION),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    public static final Map<Alcohol, FluidHolder<BaseFlowingFluid>> ALCOHOLS = Helpers.mapOfKeys(Alcohol.class, fluid -> register(
        fluid.getId(),
        properties -> properties
            .block(TFCBlocks.ALCOHOLS.get(fluid))
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.asType(fluid))),
        waterLike()
            .descriptionId("fluid.tfc." + fluid.getId())
            .canConvertToSource(false),
        new FluidTypeClientProperties(fluid.getColor(), WATER_STILL, WATER_FLOW, WATER_OVERLAY, null),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    public static final Map<DyeColor, FluidHolder<BaseFlowingFluid>> COLORED_FLUIDS = Helpers.mapOfKeys(DyeColor.class, color -> register(
        color.getName() + "_dye",
        properties -> properties
            .block(TFCBlocks.COLORED_FLUIDS.get(color))
            .bucket(TFCItems.FLUID_BUCKETS.get(FluidId.asType(color))),
        waterLike()
            .descriptionId("fluid.tfc." + color.getName() + "_dye")
            .canConvertToSource(false),
        new FluidTypeClientProperties(color.getTextureDiffuseColor(), WATER_STILL, WATER_FLOW, WATER_OVERLAY, null),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new
    ));

    private static FluidType.Properties lavaLike()
    {
        return FluidType.Properties.create()
            .adjacentPathType(PathType.LAVA)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .lightLevel(15)
            .density(3000)
            .viscosity(6000)
            .temperature(1300)
            .canConvertToSource(false)
            .canDrown(false)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canSwim(false)
            .supportsBoating(false);
    }

    private static FluidType.Properties waterLike()
    {
        return FluidType.Properties.create()
            .adjacentPathType(PathType.WATER)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .canConvertToSource(true)
            .canDrown(true)
            .canExtinguish(true)
            .canHydrate(true)
            .canPushEntity(true)
            .canSwim(true)
            .supportsBoating(true);
    }

    private static <F extends FlowingFluid> FluidHolder<F> register(String name, Consumer<BaseFlowingFluid.Properties> builder, FluidType.Properties typeProperties, FluidTypeClientProperties clientProperties, Function<BaseFlowingFluid.Properties, F> sourceFactory, Function<BaseFlowingFluid.Properties, F> flowingFactory)
    {
        // Names `metal/foo` to `metal/flowing_foo`
        final int index = name.lastIndexOf('/');
        final String flowingName = index == -1 ? "flowing_" + name : name.substring(0, index) + "/flowing_" + name.substring(index + 1);

        return RegistrationHelpers.registerFluid(FLUID_TYPES, FLUIDS, name, name, flowingName, builder, () -> new ExtendedFluidType(typeProperties, clientProperties), sourceFactory, flowingFactory);
    }
}