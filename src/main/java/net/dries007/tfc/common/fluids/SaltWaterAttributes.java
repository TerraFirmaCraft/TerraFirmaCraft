package net.dries007.tfc.common.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.fluids.FluidAttributes;

import net.dries007.tfc.client.TFCColors;

public class SaltWaterAttributes extends FluidAttributes
{
    public SaltWaterAttributes(Builder builder, Fluid fluid)
    {
        super(builder, fluid);
    }

    @Override
    public int getColor(IBlockDisplayReader world, BlockPos pos)
    {
        return world.getBlockTint(pos, TFCColors.SALT_WATER) | TFCFluids.ALPHA_MASK;
    }
}
