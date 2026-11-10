/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.LampBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.loot.CopyFluidFunction;

public class LampBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected static final VoxelShape SHAPE = Shapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
    protected static final VoxelShape HANGING_SHAPE = Shapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

    private static Direction getConnectedDirection(BlockState state)
    {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    public LampBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HANGING, false).setValue(LIT, false).setValue(POWERED, false));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).ifPresent(LampBlockEntity::checkHasRanOut);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(LIT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (random.nextInt(6) == 0 && state.getValue(LIT))
        {
            final double y = pos.getY() + (state.getValue(HANGING) ? 0.625 : 0.6875);
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, y, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).ifPresent(TickCounterBlockEntity::resetCounter);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final @Nullable LampBlockEntity lamp = level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).orElse(null);
        if (lamp != null)
        {
            lamp.checkHasRanOut();

            if (stack.isEmpty() && player.isShiftKeyDown() && state.getValue(LIT))
            {
                // Quench by shift-clicking with an empty hand
                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                level.setBlockAndUpdate(pos, state.setValue(LIT, false));
                lamp.resetCounter();
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, lamp, player, hand))
            {
                lamp.markForSync();
                if (lamp.getFuel() != null && lamp.getFuel().burnRate() == -1 && lamp.getBlockState().getValue(LIT) && player instanceof ServerPlayer serverPlayer)
                {
                    TFCAdvancements.LAVA_LAMP.trigger(serverPlayer);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!FluidHelpers.getContainedFluid(player.getItemInHand(hand)).isEmpty())
            {
                // Prevent the player from accidentally dumping out their lamp fuel (or what they think is lamp fuel)
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        CopyFluidFunction.copyToItem(stack, level.getBlockEntity(pos));
        return stack;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());

        for (Direction direction : context.getNearestLookingDirections())
        {
            if (direction.getAxis() == Direction.Axis.Y)
            {
                BlockState state = this.defaultBlockState().setValue(HANGING, direction == Direction.UP).setValue(POWERED, powered);
                if (state.canSurvive(context.getLevel(), context.getClickedPos()))
                {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(HANGING) ? HANGING_SHAPE : SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HANGING, LIT, POWERED));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile)
    {
        BlockPos blockpos = hit.getBlockPos();
        if (level instanceof ServerLevel serverLevel && projectile.mayInteract(level, blockpos) && Helpers.isEntity(projectile, EntityTypeTags.IMPACT_PROJECTILES))
        {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), projectile.getX(), projectile.getY(), projectile.getZ(), 10, 0, 0, 0, 0.15f);
            level.destroyBlock(blockpos, true, projectile);
            if (state.getValue(LIT))
            {
                projectile.igniteForTicks(5 * 20);
                final Direction fireDir = Direction.Plane.HORIZONTAL.getRandomDirection(level.random);
                final BlockPos pos = projectile.blockPosition();
                if (FireBlock.canBePlacedAt(level, pos, fireDir))
                {
                    level.setBlockAndUpdate(pos, FireBlock.getState(level, pos));
                }
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof LampBlockEntity lamp)
        {
            final int capacity = TFCConfig.SERVER.lampCapacity.get();
            if (lamp.getFuelAmount() > 0 && capacity != 0)
            {
                return Mth.clamp(lamp.getFuelAmount() * 15 / capacity, 1, 15);
            }
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        final boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED))
        {
            state = state.setValue(POWERED, signal);
            if (level.getBlockEntity(pos) instanceof LampBlockEntity lamp && signal != state.getValue(LIT))
            {
                if (!signal)
                {
                    lamp.checkHasRanOut(); // update fuel usage
                    Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                }
                state = state.setValue(LIT, signal && lamp.getFuel() != null); // don't light if there's no fuel
                lamp.resetCounter();
            }
            level.setBlockAndUpdate(pos, state);
        }
    }
}
