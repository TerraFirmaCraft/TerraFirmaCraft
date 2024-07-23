package net.dries007.tfc.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.data.providers.BuiltinFluidHeat;
import net.dries007.tfc.data.providers.BuiltinItemHeat;
import net.dries007.tfc.data.providers.DataManagerProvider;
import net.dries007.tfc.util.data.FluidHeat;

public interface TestSetup
{
    AtomicBoolean LOADED = new AtomicBoolean(false);
    Object LOCK = new Object();

    @BeforeAll
    static void beforeAll()
    {
        synchronized (LOCK)
        {
            if (LOADED.get()) return;
            BuiltInRegistries.FLUID.bindTags(Map.of(
                TFCTags.Fluids.USABLE_IN_WOODEN_BUCKET, List.of(BuiltInRegistries.FLUID.wrapAsHolder(Fluids.WATER)),
                TFCTags.Fluids.USABLE_IN_INGOT_MOLD, TFCFluids.METALS.values()
                    .stream()
                    .map(f -> BuiltInRegistries.FLUID.wrapAsHolder(f.getSource()))
                    .toList()
            ));

            DataManagerProvider.setup(BuiltinFluidHeat::new);
            DataManagerProvider.setup(BuiltinItemHeat::new);
            FluidHeat.updateCache();
            LOADED.set(true);
        }
    }
}
