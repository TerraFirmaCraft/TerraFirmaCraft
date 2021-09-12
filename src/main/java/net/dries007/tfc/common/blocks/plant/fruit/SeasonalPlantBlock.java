/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

public abstract class SeasonalPlantBlock extends BushBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    /**
     * This function is essentially min(blocks to reach the ground, provided distance value)
     */
    public static int distanceToGround(Level world, BlockPos pos, int distance)
    {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        for (int i = 1; i <= distance; i++)
        {
            mutablePos.move(Direction.DOWN);
            if (world.getBlockState(mutablePos).isFaceSturdy(world, pos, Direction.UP))
            {
                return i;
            }
        }
        return distance;
    }

    private final Supplier<? extends Item> productItem;
    private final Lifecycle[] stages;
    private final ForgeBlockProperties properties;

    public SeasonalPlantBlock(ForgeBlockProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties.properties());

        this.properties = properties;
        this.stages = stages;
        this.productItem = productItem;

        if (stages.length != 12)
        {
            throw new IllegalArgumentException("stages array must be of length 12 (number of months per year)");
        }
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide() && handIn == InteractionHand.MAIN_HAND && state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            BerryBushBlockEntity te = Helpers.getBlockEntity(worldIn, pos, BerryBushBlockEntity.class);
            if (te != null)
            {
                te.use();
                if (te.willStopUsing())
                {
                    Helpers.spawnItem(worldIn, pos, getProductItem());
                    te.setHarvested(true);
                    te.stopUsing();
                    worldIn.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
                    return InteractionResult.CONSUME;
                }
                if (worldIn.getGameTime() % 3 == 0)
                    Helpers.playSound(worldIn, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.block() : PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        BerryBushBlockEntity te = Helpers.getBlockEntity(world, pos, BerryBushBlockEntity.class);
        if (te == null) return;

        ChunkData chunkData = ChunkData.get(world, pos);

        // todo: improve temperature and rainfall checks. temperature should be actual, rainfall should be player-useful hydration
        /*
        if (!bush.isValidConditions(chunkData.getAverageTemp(pos), chunkData.getRainfall(pos)))
        {
            te.setGrowing(false);
        }

         */

        /*Lifecycle old = state.getValue(LIFECYCLE);
        if (old != Lifecycle.HEALTHY)
        {
            te.resetCounter(); // todo Prevent long calendar changes from causing runaway growth. Needs improvement.
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
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
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
    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {

    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || this.mayPlaceOn(worldIn.getBlockState(belowPos), worldIn, belowPos);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BerryBushBlockEntity te = Helpers.getBlockEntity(worldIn, pos, BerryBushBlockEntity.class);
        if (te != null)
        {
            te.resetCounter();
            te.setGrowing(true);
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE);
    }

    /**
     * Queries the lifecycle based on the data, but catches certain conditions that would
     */
    protected Lifecycle updateLifecycle(BerryBushBlockEntity te)
    {
        Lifecycle cycle = stages[Calendars.SERVER.getCalendarMonthOfYear().ordinal()];

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

    protected ItemStack getProductItem()
    {
        return new ItemStack(productItem.get());
    }

}
