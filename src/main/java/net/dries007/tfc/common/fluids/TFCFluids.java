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
import net.dries007.tfc.common.capabilities.heat.Heat;
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
        properties -> properties
            .block(() -> null) // todo: block
            .bucket(() -> null), // todo: item
        FluidAttributes.builder(Helpers.identifier("block/lava_still"), Helpers.identifier("block/lava_flow"))
            .color(metal.getColor())
            .luminosity(15)
            .density(7000)
            .temperature((int) Heat.maxVisibleTemperature())
            .rarity(metal.getRarity())
            .translationKey("fluid.tfc.metal." + metal.name().toLowerCase()),
        ForgeFlowingFluid.Source::new,
        ForgeFlowingFluid.Flowing::new
    ));

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