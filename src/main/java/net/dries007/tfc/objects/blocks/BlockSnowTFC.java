/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import net.dries007.tfc.util.climate.ClimateTFC;

@ParametersAreNonnullByDefault
public class BlockSnowTFC extends BlockSnow
{
    public BlockSnowTFC()
    {
        setHardness(0.1F);
        setSoundType(SoundType.SNOW);
        setLightOpacity(0);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        // Either block light (i.e. from torches) or high enough temperature
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - getLightOpacity(state, worldIn, pos) || ClimateTFC.getActualTemp(worldIn, pos) > 4f)
        {
            worldIn.setBlockToAir(pos);
        }
    }
}
