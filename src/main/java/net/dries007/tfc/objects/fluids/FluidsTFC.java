package net.dries007.tfc.objects.fluids;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import static net.dries007.tfc.Constants.MOD_ID;

public class FluidsTFC
{
    private static final ResourceLocation STILL = new ResourceLocation(MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(MOD_ID, "blocks/fluid_flow");

    public static final Fluid SALT_WATER = new Fluid("salt_water", STILL, FLOW, 0xFF1F5099);
    public static final Fluid FRESH_WATER = new Fluid("fresh_water", STILL, FLOW, 0xFF1F32DA);
    public static final Fluid HOT_WATER = new Fluid("hot_water", STILL, FLOW, 0xFF345FDA).setTemperature(350);

    private static ImmutableList<Fluid> allFluids;

    static
    {
        allFluids = ImmutableList.of(SALT_WATER, FRESH_WATER, HOT_WATER);

        for (Fluid f : allFluids)
        {
            if (!FluidRegistry.isFluidRegistered(f)) FluidRegistry.registerFluid(f);
            FluidRegistry.addBucketForFluid(f);
        }
    }

    public static void preInit()
    {
//        FluidRegistry.WATER.setBlock(BlocksTFC.FRESH_WATER);
        SALT_WATER.setBlock(BlocksTFC.SALT_WATER);
        FRESH_WATER.setBlock(BlocksTFC.FRESH_WATER);
        HOT_WATER.setBlock(BlocksTFC.HOT_WATER);
    }

    public static ImmutableList<Fluid> getAllFluids()
    {
        return allFluids;
    }
}
