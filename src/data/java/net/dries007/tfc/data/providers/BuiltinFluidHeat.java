package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public class BuiltinFluidHeat extends DataManagerProvider<FluidHeat> implements Accessors
{
    public static final float HEAT_CAPACITY = 0.003f;

    public BuiltinFluidHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(FluidHeat.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(Metal.BISMUTH, 0.14f, 270);
        add(Metal.BISMUTH_BRONZE, 0.35f, 985);
        add(Metal.BLACK_BRONZE, 0.35f, 1070);
        add(Metal.BRONZE, 0.35f, 950);
        add(Metal.BRASS, 0.35f, 930);
        add(Metal.COPPER, 0.35f, 1080);
        add(Metal.GOLD, 0.6f, 1060);
        add(Metal.NICKEL, 0.48f, 1453);
        add(Metal.ROSE_GOLD, 0.35f, 960);
        add(Metal.SILVER, 0.48f, 961);
        add(Metal.TIN, 0.14f, 230);
        add(Metal.ZINC, 0.21f, 420);
        add(Metal.STERLING_SILVER, 0.35f, 950);
        add(Metal.WROUGHT_IRON, 0.35f, 1535);
        add(Metal.CAST_IRON, 0.35f, 1535);
        add(Metal.PIG_IRON, 0.35f, 1535);
        add(Metal.STEEL, 0.35f, 1540);
        add(Metal.BLACK_STEEL, 0.35f, 1485);
        add(Metal.BLUE_STEEL, 0.35f, 1540);
        add(Metal.RED_STEEL, 0.35f, 1540);
        add(Metal.WEAK_STEEL, 0.35f, 1540);
        add(Metal.WEAK_BLUE_STEEL, 0.35f, 1540);
        add(Metal.WEAK_RED_STEEL, 0.35f, 1540);
        add(Metal.HIGH_CARBON_STEEL, 0.35f, 1540);
        add(Metal.HIGH_CARBON_BLACK_STEEL, 0.35f, 1540);
        add(Metal.HIGH_CARBON_BLUE_STEEL, 0.35f, 1540);
        add(Metal.HIGH_CARBON_RED_STEEL, 0.35f, 1540);
        add(Metal.UNKNOWN, 0.5f, 400);
    }

    private void add(Metal metal, float baseHeatCapacity, float meltTemperature)
    {
        add(metal.getSerializedName(), new FluidHeat(TFCFluids.METALS.get(metal).getSource(), meltTemperature, HEAT_CAPACITY / baseHeatCapacity));
    }
}
