/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

@ParametersAreNonnullByDefault
public class WorldGenTallPlant extends WorldGenerator
{
    private IPlantable plant;

    public WorldGenTallPlant(IPlantable plant)
    {
        this.plant = plant;
    }

    public boolean generate(World world, Random rng, BlockPos start)
    {
        for (int i = 0; i < 20; ++i)
        {
            BlockPos spot = start.add(rng.nextInt(4) - rng.nextInt(4), -1, rng.nextInt(4) - rng.nextInt(4));
            BlockPos prevSpot = spot;
            IBlockState prevState = world.getBlockState(spot);
            final int n = 2 + rng.nextInt(rng.nextInt(3) + 1);
            for (int y = 0; y < n; y++)
            {
                spot = spot.add(0, +1, 0);
                IBlockState toPlant = plant.getPlant(world, spot);
                if (!world.isAirBlock(spot) || !prevState.getBlock().canSustainPlant(prevState, world, prevSpot, EnumFacing.UP, plant) ||
                    !plant.getPlant(world, spot).getBlock().canPlaceBlockAt(world, spot))
                    break;
                setBlockAndNotifyAdequately(world, spot, toPlant);
                prevState = toPlant;
                prevSpot = spot;
            }
        }

        return true;
    }
}
