package net.dries007.tfc.common.blocks.berry_bush;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class SpreadingBushBlock extends BushBlock implements IForgeBlockProperties
{
    protected static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    private final ForgeBlockProperties properties;
    protected final SpreadingBush bush;
    protected final Supplier<? extends Block> companion;

    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.BERRY_BUSH_LIFE;

    public SpreadingBushBlock(ForgeBlockProperties properties, SpreadingBush bush, Supplier<? extends Block> companion)
    {
        super(properties.properties());
        this.properties = properties;
        this.bush = bush;
        this.companion = companion;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isClientSide() && handIn == Hand.MAIN_HAND && state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            BerryBushTileEntity te = Helpers.getTileEntity(worldIn, pos, BerryBushTileEntity.class);
            if (te != null)
            {
                te.use();
                if (te.willStopUsing())
                {
                    Helpers.spawnItem(worldIn, pos, new ItemStack(bush.getBerry()));
                    te.setHarvested(true);
                    te.stopUsing();
                    worldIn.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
                    return ActionResultType.CONSUME;
                }
                if (worldIn.getGameTime() % 3 == 0)
                    Helpers.playSound(worldIn, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        BerryBushTileEntity te = Helpers.getTileEntity(world, pos, BerryBushTileEntity.class);
        if (te == null) return;

        Lifecycle old = state.getValue(LIFECYCLE);
        if (old != Lifecycle.HEALTHY)
        {
            te.resetCounter();
        }

        Lifecycle lifecycle = updateLifecycle(te);
        world.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, lifecycle));

        int stage = state.getValue(STAGE);
        long days = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;

        if (days >= 1)
        {
            int cycles = (int) days * 2;
            for (int i = cycles; i > 0; i--)
            {
                if (lifecycle == Lifecycle.HEALTHY)
                {
                    if (!te.isGrowing() || te.isRemoved()) break;

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
                        if (!world.getBlockState(pos.above()).is(TFCTags.Blocks.BERRY_BUSH))
                            world.setBlockAndUpdate(pos, TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, stage));
                    }
                }
            }
            te.resetCounter();
        }
    }

    protected static int distanceToGround(World world, BlockPos pos, int distance)
    {
        BlockPos.Mutable mutablePos = pos.mutable();
        for (int i = 1; i <= distance; i++)
        {
            mutablePos.move(Direction.DOWN);
            if (world.getBlockState(mutablePos).isFaceSturdy(world, pos, Direction.UP))
                return i;
        }
        return distance;
    }

    protected Lifecycle updateLifecycle(BerryBushTileEntity te)
    {
        Lifecycle cycle = bush.getStages()[Calendars.SERVER.getCalendarMonthOfYear().ordinal()];

        if ((cycle == Lifecycle.HEALTHY || cycle == Lifecycle.FLOWERING) && te.isGrowing())
        {
            te.setHarvested(false);
        }
        if (cycle == Lifecycle.FRUITING || cycle == Lifecycle.FLOWERING)
        {
            if (te.isHarvested())
            {
                cycle = Lifecycle.DORMANT;
            }
            else if (!te.isGrowing())
            {
                cycle = Lifecycle.HEALTHY;
            }
        }
        return cycle;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return super.mayPlaceOn(state, worldIn, pos) || canGrowOn(state);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    protected static boolean canGrowOn(BlockState state)
    {
        return TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(state.getBlock()) || state.is(TFCTags.Blocks.ALIVE_OR_DEAD_BUSH);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (TFCConfig.SERVER.enableLeavesSlowEntities.get())
        {
            Helpers.slowEntityInBlock(entityIn, 0.2f, 5);
        }
        if (!(entityIn.getType() == EntityType.ITEM))
        {
            entityIn.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0f);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return state.getValue(STAGE) == 2 ? VoxelShapes.block() : PLANT_SHAPE;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BerryBushTileEntity te = Helpers.getTileEntity(worldIn, pos, BerryBushTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
            te.setGrowing(true);
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE);
    }

    public enum Lifecycle implements IStringSerializable
    {
        HEALTHY, FLOWERING, FRUITING, DORMANT;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase();
        }
    }
}
