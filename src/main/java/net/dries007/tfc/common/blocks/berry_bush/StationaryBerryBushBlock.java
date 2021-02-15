package net.dries007.tfc.common.blocks.berry_bush;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;

public class StationaryBerryBushBlock extends AbstractBerryBushBlock
{
    public StationaryBerryBushBlock(ForgeBlockProperties properties, BerryBush bush)
    {
        super(properties, bush);
    }

    @Override
    protected void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (random.nextInt(3) != 0) return;
            if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
            }
            else if (stage == 2)
            {
                if (random.nextInt(bush.getDeathFactor()) == 0)
                {
                    for (int i = 0; i < random.nextInt(3) + 1; i++)
                        propagate(world, pos, random);
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT && !te.isGrowing())
        {
            te.addDeath();
            if (te.willDie() && random.nextInt(5) == 0)
            {
                world.setBlockAndUpdate(pos, TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, stage));
            }
        }
    }

    private void propagate(World world, BlockPos pos, Random rand)
    {
        BlockPos.Mutable mutablePos = pos.mutable();
        for (int i = 0; i < 12; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(10) - rand.nextInt(10), 0, rand.nextInt(10) - rand.nextInt(10));
            if (world.isEmptyBlock(mutablePos))
            {
                final BlockState placeState = defaultBlockState().setValue(STAGE, 0).setValue(LIFECYCLE, Lifecycle.HEALTHY);
                if (world.isEmptyBlock(mutablePos) && ((StationaryBerryBushBlock) placeState.getBlock()).canSurvive(placeState, world, mutablePos))
                {
                    world.setBlockAndUpdate(mutablePos, placeState);
                    return;
                }
            }
        }
    }
}
