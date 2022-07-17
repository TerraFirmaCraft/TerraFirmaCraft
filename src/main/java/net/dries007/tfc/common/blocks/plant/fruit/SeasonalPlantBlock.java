/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateRange;

public abstract class SeasonalPlantBlock extends BushBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_2;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    /**
     * This function is essentially min(blocks to reach the ground, provided distance value)
     */
    public static int distanceToGround(Level level, BlockPos pos, int distance)
    {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        for (int i = 1; i <= distance; i++)
        {
            mutablePos.move(Direction.DOWN);
            if (!Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.ANY_SPREADING_BUSH))
            {
                return i;
            }
        }
        return distance;
    }

    /**
     * Checks if the plant is outside its growing season, and if so sets it to dormant.
     *
     * @return if the plant is dormant
     */
    public static boolean checkAndSetDormant(Level level, BlockPos pos, BlockState state, Lifecycle current, Lifecycle expected)
    {
        if (expected == Lifecycle.DORMANT)
        {
            // When we're in dormant time, no matter what conditions, or time since appearance, the bush will be dormant.
            if (expected != current)
            {
                level.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
            }
            return true;
        }
        return false;
    }

    public static void randomDestroyTick(ServerLevel level, BlockPos pos, int days)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(be -> {
            if (be.getTicksSinceUpdate() > (long) ICalendar.TICKS_IN_DAY * days)
            {
                be.setRemoved();
                level.destroyBlock(pos, true);
            }
        });
    }

    protected final Supplier<? extends Item> productItem;
    protected final Supplier<ClimateRange> climateRange;
    private final Lifecycle[] lifecycle;
    private final ExtendedProperties properties;

    public SeasonalPlantBlock(ExtendedProperties properties, Supplier<ClimateRange> climateRange, Supplier<? extends Item> productItem, Lifecycle[] lifecycle)
    {
        super(properties.properties());

        Preconditions.checkArgument(lifecycle.length == 12, "Lifecycle length must be 12");

        this.properties = properties;
        this.climateRange = climateRange;
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
        if (!getTrimItemStack().isEmpty())
        {
            // Flowering bushes can be cut to create trimmings. This is how one moves or creates new bushes.
            // The larger the bush is (higher stage), the better chance you have of
            // 1. damaging it less (i.e. reducing the stage, or killing it), and
            // 2. making a clipping.
            if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
            {
                final ItemStack held = player.getItemInHand(hand);
                if (Helpers.isItem(held.getItem(), TFCTags.Items.BUSH_CUTTING_TOOLS))
                {
                    level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.5f, 1.0f);
                    if (!level.isClientSide())
                    {
                        level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> {
                            final int finalStage = state.getValue(STAGE) - 1 - level.getRandom().nextInt(2);
                            if (finalStage >= 0)
                            {
                                // We didn't kill the bush, but we have cut the flowers off
                                level.setBlock(pos, state.setValue(STAGE, finalStage).setValue(LIFECYCLE, Lifecycle.HEALTHY), 3);
                            }
                            else
                            {
                                // Oops
                                level.destroyBlock(pos, false, player);
                            }

                            held.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));

                            // But, if we were successful, we have obtained a clipping (2 / 3 chance)
                            if (level.getRandom().nextInt(3) != 0)
                            {
                                ItemHandlerHelper.giveItemToPlayer(player, getTrimItemStack());
                            }
                        });
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            level.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.PLAYERS, 1.0f, level.getRandom().nextFloat() + 0.7f + 0.3f);
            if (!level.isClientSide())
            {
                level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> {
                    ItemHandlerHelper.giveItemToPlayer(player, getProductItem(level.random));
                    level.setBlockAndUpdate(pos, stateAfterPicking(state));
                });
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected ItemStack getTrimItemStack()
    {
        return ItemStack.EMPTY;
    }

    public BlockState stateAfterPicking(BlockState state)
    {
        return state.setValue(LIFECYCLE, Lifecycle.HEALTHY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
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
        if (entity.getType() != EntityType.ITEM && Helpers.isBlock(this, TFCTags.Blocks.THORNY_BUSHES))
        {
            entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0f);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE);
    }

    public ItemStack getProductItem(Random random)
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
        return Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        return mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }
}
