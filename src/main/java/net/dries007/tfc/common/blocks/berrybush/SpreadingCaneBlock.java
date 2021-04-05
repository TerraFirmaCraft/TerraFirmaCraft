package net.dries007.tfc.common.blocks.berrybush;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class SpreadingCaneBlock extends SpreadingBushBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape CANE_EAST = Block.box(0.0D, 3.0D, 0.0D, 8.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_WEST = Block.box(8.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_SOUTH = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    private static final VoxelShape CANE_NORTH = Block.box(0.0D, 3.0D, 8.0D, 16.0D, 12.0D, 16.0D);

    public SpreadingCaneBlock(ForgeBlockProperties properties, BerryBush bush, Supplier<? extends Block> companion)
    {
        super(properties, bush, companion);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isClientSide() || handIn != Hand.MAIN_HAND) return ActionResultType.FAIL;
        if (state.getValue(STAGE) == 2)
        {
            ItemStack held = player.getItemInHand(handIn);
            if (held.getItem().is(Tags.Items.SHEARS))
            {
                BerryBushTileEntity te = Helpers.getTileEntity(worldIn, pos, BerryBushTileEntity.class);
                if (te != null)
                {
                    if (state.getValue(LIFECYCLE) == Lifecycle.DORMANT)
                    {
                        te.setGrowing(true);
                        te.resetDeath();
                        held.hurt(1, worldIn.getRandom(), null);
                        Helpers.playSound(worldIn, pos, SoundEvents.SHEEP_SHEAR);
                        worldIn.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
                        return ActionResultType.SUCCESS;
                    }
                    else if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
                    {
                        held.hurt(1, worldIn.getRandom(), null);
                        Helpers.playSound(worldIn, pos, SoundEvents.SHEEP_SHEAR);
                        if (worldIn.getRandom().nextInt(3) != 0)
                            Helpers.spawnItem(worldIn, pos, new ItemStack(companion.get()));
                        worldIn.destroyBlock(pos, true, null);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.getValue(FACING))
        {
            case NORTH:
                return CANE_NORTH;
            case WEST:
                return CANE_WEST;
            case EAST:
                return CANE_EAST;
            case SOUTH:
                return CANE_SOUTH;
        }
        return CANE_EAST;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE, FACING);
    }

    @Override
    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1 && random.nextInt(7) == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
                if (random.nextInt(bush.getDeathFactor()) == 0)
                {
                    te.setGrowing(false);
                }
            }
            else if (stage == 2 && random.nextInt(7) == 0)
            {
                if (((SpreadingCaneBlock) state.getBlock()).canSurvive(state, world, pos))
                {
                    world.setBlockAndUpdate(pos, companion.get().defaultBlockState().setValue(STAGE, 1));
                    TickCounterTileEntity bush = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
                    if (bush != null)
                    {
                        bush.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * te.getTicksSinceUpdate());
                    }
                }
                else
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
                    world.setBlockAndUpdate(pos, TFCBlocks.DEAD_CANE.get().defaultBlockState().setValue(STAGE, stage).setValue(FACING, state.getValue(FACING)));
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).is(TFCTags.Blocks.ANY_SPREADING_BUSH);
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return true;
    }
}
