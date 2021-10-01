/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;

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
    private final Lifecycle[] lifecycle;
    private final ExtendedProperties properties;

    public SeasonalPlantBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] lifecycle)
    {
        super(properties.properties());

        Preconditions.checkArgument(lifecycle.length == 12, "Lifecycle length must be 12");

        this.properties = properties;
        this.lifecycle = lifecycle;
        this.productItem = productItem;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            level.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.PLAYERS, 1.0f, level.getRandom().nextFloat() + 0.7f + 0.3f);
            if (!level.isClientSide())
            {
                level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> {
                    ItemHandlerHelper.giveItemToPlayer(player, getProductItem());
                    level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.HEALTHY));
                });
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.block() : PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (TFCConfig.SERVER.enableLeavesSlowEntities.get())
        {
            Helpers.slowEntityInBlock(entity, 0.2f, 5);
        }
        // todo: move this to bushes
        if (entity.getType() != EntityType.ITEM && TFCTags.Blocks.THORNY_BUSHES.contains(this))
        {
            entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0f);
        }
    }

    /**
     * A means of performing X amount of random ticks to catch up with the calendar.
     */
    @Deprecated
    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {

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
        Lifecycle cycle = lifecycle[Calendars.SERVER.getCalendarMonthOfYear().ordinal()];

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

    protected Lifecycle getLifecycleForCurrentMonth()
    {
        return getLifecycleForMonth(Calendars.SERVER.getCalendarMonthOfYear());
    }

    protected Lifecycle getLifecycleForMonth(Month month)
    {
        return lifecycle[month.ordinal()];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return level.getBlockState(pos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        return mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }
}
