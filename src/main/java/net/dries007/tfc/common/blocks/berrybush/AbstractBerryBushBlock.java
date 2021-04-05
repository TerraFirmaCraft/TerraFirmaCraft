package net.dries007.tfc.common.blocks.berrybush;

import java.util.Random;
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
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

public abstract class AbstractBerryBushBlock extends BushBlock implements IForgeBlockProperties
{
    public static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    /**
     * This function is essentially min(blocks to reach the ground, provided distance value)
     */
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
    protected final BerryBush bush;
    private final ForgeBlockProperties properties;

    public AbstractBerryBushBlock(ForgeBlockProperties properties, BerryBush bush)
    {
        super(properties.properties());
        this.properties = properties;
        this.bush = bush;
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return state.getValue(STAGE) == 2 ? VoxelShapes.block() : PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        BerryBushTileEntity te = Helpers.getTileEntity(world, pos, BerryBushTileEntity.class);
        if (te == null) return;

        ChunkData chunkData = ChunkData.get(world, pos);
        if (!bush.isValidConditions(chunkData.getAverageTemp(pos), chunkData.getRainfall(pos)))
        {
            te.setGrowing(false);
        }

        /*Lifecycle old = state.getValue(LIFECYCLE);
        if (old != Lifecycle.HEALTHY)
        {
            te.resetCounter(); // Prevent long calendar changes from causing runaway growth. Needs improvement.
        }*/

        Lifecycle lifecycle = updateLifecycle(te);
        world.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, lifecycle));

        int stage = state.getValue(STAGE);
        long days = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;

        if (days >= 1)
        {
            int cycles = (int) days * 2;
            for (int i = cycles; i > 0; i--)
            {
                this.cycle(te, world, pos, state, stage, lifecycle, random);
            }
            te.resetCounter();
        }
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

    /**
     * A means of performing X amount of random ticks to catch up with the calendar.
     */
    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {

    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || this.mayPlaceOn(worldIn.getBlockState(belowPos), worldIn, belowPos);
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

    public BerryBush getBush()
    {
        return bush;
    }

    /**
     * Queries the lifecycle based on the data, but catches certain conditions that would
     */
    protected Lifecycle updateLifecycle(BerryBushTileEntity te)
    {
        Lifecycle cycle = bush.getStages()[Calendars.SERVER.getCalendarMonthOfYear().ordinal()];

        if ((cycle == Lifecycle.HEALTHY || cycle == Lifecycle.FLOWERING) && te.isGrowing())
        {
            te.setHarvested(false); // prepare to make fruit
        }
        if (cycle == Lifecycle.FRUITING || cycle == Lifecycle.FLOWERING)
        {
            if (te.isHarvested())
            {
                cycle = Lifecycle.DORMANT; // turn dormant after harvesting
            }
            else if (!te.isGrowing())
            {
                cycle = Lifecycle.HEALTHY; // if it can't grow, we prevent it from flowering or fruiting
            }
        }
        return cycle;
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
