/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.fluids;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.heat.Heat;
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

    public static final Map<Metal.Default, FluidPair<ForgeFlowingFluid>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal -> register(
        "metal/" + metal.name().toLowerCase(),
        "metal/flowing_" + metal.name().toLowerCase(),
        properties -> properties.block(TFCBlocks.METAL_FLUIDS.get(metal)).bucket(TFCItems.METAL_FLUID_BUCKETS.get(metal)),
        lavaAttributes("metal." + metal.name().toLowerCase()).color(metal.getColor()).rarity(metal.getRarity())
    ));

    public static final FluidPair<ForgeFlowingFluid> SALT_WATER = register(
        "salt_water",
        "flowing_salt_water",
        properties -> properties.block(TFCBlocks.SALT_WATER).bucket(TFCItems.SALT_WATER_BUCKET).canMultiply(),
        defaultAttributes("salt_water")
    );

    public static final FluidPair<ForgeFlowingFluid> SPRING_WATER = register(
        "spring_water",
        "flowing_spring_water",
        properties -> properties.block(TFCBlocks.SPRING_WATER).bucket(TFCItems.SPRING_WATER_BUCKET),
        defaultAttributes("spring_water")
    );

    private static FluidAttributes.Builder defaultAttributes(String fluidName)
    {
        return FluidAttributes.builder(Helpers.identifier("block/fluid_still"), Helpers.identifier("block/fluid_flow"))
            .translationKey("fluid.tfc." + fluidName);
    }

    private static FluidAttributes.Builder lavaAttributes(String fluidName)
    {
        return FluidAttributes.builder(Helpers.identifier("block/lava_still"), Helpers.identifier("block/lava_flow"))
            .luminosity(15)
            .density(7000)
            .temperature((int) Heat.maxVisibleTemperature())
            .translationKey("fluid.tfc" + fluidName);
    }

    private static FluidPair<ForgeFlowingFluid> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes)
    {
        return register(sourceName, flowingName, builder, attributes, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new);
    }

    private static <F extends FlowingFluid> FluidPair<F> register(String sourceName, String flowingName, Consumer<ForgeFlowingFluid.Properties> builder, FluidAttributes.Builder attributes, Function<ForgeFlowingFluid.Properties, F> sourceFactory, Function<ForgeFlowingFluid.Properties, F> flowingFactory)
    {
        final Mutable<ForgeFlowingFluid.Properties> propertiesBox = new MutableObject<>();
        final RegistryObject<F> source = register(sourceName, () -> sourceFactory.apply(propertiesBox.getValue()));
        final RegistryObject<F> flowing = register(flowingName, () -> flowingFactory.apply(propertiesBox.getValue()));
        final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(source, flowing, attributes);

        builder.accept(properties);
        propertiesBox.setValue(properties);

        return new FluidPair<>(flowing, source);
    }

    private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> factory)
    {
        return FLUIDS.register(name, factory);
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

        @Override
        public FluidPair<F> swap()
        {
            return new FluidPair<>(getSecond(), getFirst());
        }
    }
}