package net.dries007.tfc.data.providers;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.data.DataAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.BlockType;
import net.dries007.tfc.util.Metal.ItemType;
import net.dries007.tfc.util.data.FluidHeat;

public class BuiltinItemHeat extends DataManagerProvider<HeatDefinition> implements Accessors
{
    private final DataAccessor<FluidHeat> fluidHeat;

    public BuiltinItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, DataAccessor<FluidHeat> fluidHeat)
    {
        super(HeatCapability.MANAGER, output, lookup);
        this.fluidHeat = fluidHeat;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return fluidHeat.future().thenCompose(v -> super.beforeRun());
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        for (Metal metal : Metal.values())
        {
            for (ItemType type : ItemType.values())
                if (type.has(metal))
                    add(metal, type.name(), ingredientOf(metal, type), units(type));
            for (BlockType type : BlockType.values())
                if (type.has(metal))
                    add(metal, type.name(), ingredientOf(metal, type), units(type));
        }

        // todo: more
    }

    private void add(Metal metal, String typeName, Ingredient ingredient, int units)
    {
        final FluidHeat fluidHeat = this.fluidHeat.get(Helpers.identifier(metal.getSerializedName()));
        add(metal.getSerializedName() + "/" + typeName.toLowerCase(Locale.ROOT), new HeatDefinition(
            ingredient,
            (fluidHeat.specificHeatCapacity() / BuiltinFluidHeat.HEAT_CAPACITY) * (units / 100f),
            fluidHeat.meltTemperature() * 0.6f,
            fluidHeat.meltTemperature() * 0.8f
        ));
    }
}
