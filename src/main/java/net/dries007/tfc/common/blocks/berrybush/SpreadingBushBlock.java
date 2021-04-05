package net.dries007.tfc.common.blocks.berrybush;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class SpreadingBushBlock extends AbstractBerryBushBlock implements IForgeBlockProperties
{
    protected final BerryBush bush;
    protected final Supplier<? extends Block> companion;

    public SpreadingBushBlock(ForgeBlockProperties properties, BerryBush bush, Supplier<? extends Block> companion)
    {
        super(properties, bush);
        this.bush = bush;
        this.companion = companion;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (distanceToGround(world, pos, bush.getMaxHeight()) >= bush.getMaxHeight())
            {
                te.setGrowing(false);
            }
            else if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1 && random.nextInt(7) == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
                if (world.isEmptyBlock(pos.above()))
                    world.setBlockAndUpdate(pos.above(), state.setValue(STAGE, 1));
            }
            else if (stage == 2)
            {
                Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos offsetPos = pos.relative(d);
                if (world.isEmptyBlock(offsetPos))
                {
                    world.setBlockAndUpdate(offsetPos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, d));
                    TickCounterTileEntity cane = Helpers.getTileEntity(world, offsetPos, TickCounterTileEntity.class);
                    if (cane != null)
                    {
                        cane.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * te.getTicksSinceUpdate());
                    }
                }
                if (random.nextInt(bush.getDeathFactor()) == 0)
                {
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT && !te.isGrowing())
        {
            te.addDeath();
            if (te.willDie() && random.nextInt(3) == 0)
            {
                if (!world.getBlockState(pos.above()).is(TFCTags.Blocks.SPREADING_BUSH))
                    world.setBlockAndUpdate(pos, TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, stage));
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.ANY_SPREADING_BUSH) || this.mayPlaceOn(worldIn.getBlockState(belowPos), worldIn, belowPos);
    }
}
