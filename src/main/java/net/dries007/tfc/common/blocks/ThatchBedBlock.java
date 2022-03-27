/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.util.Helpers;

/**
 * Lots of parts borrowed from {@link BedBlock}
 * Avoid extending directly as it implements {@link EntityBlock} which we don't want, and also defines the 'occupied' state which we don't use.
 */
public class ThatchBedBlock extends HorizontalDirectionalBlock
{
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    private static final VoxelShape BED_SHAPE = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F);

    public static Direction getNeighbourDirection(BedPart part, Direction direction)
    {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    public ThatchBedBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == getNeighbourDirection(state.getValue(PART), state.getValue(FACING)))
        {
            return Helpers.isBlock(facingState, this) && facingState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        }
        else
        {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClientSide())
        {
            if (BedBlock.canSetSpawn(worldIn))
            {
                if (!worldIn.isThundering())
                {
                    player.displayClientMessage(new TranslatableComponent("tfc.thatch_bed.use"), true);
                }
                else
                {
                    player.displayClientMessage(new TranslatableComponent("tfc.thatch_bed.thundering"), true);
                }
            }
            else
            {
                worldIn.explode(null, DamageSource.badRespawnPointExplosion(), null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Explosion.BlockInteraction.DESTROY);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return BED_SHAPE;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection())).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()) : null;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        super.fallOn(level, state, pos, entity, fallDistance * 0.5f);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity)
    {
        if (entity.isSuppressingBounce())
        {
            super.updateEntityAfterFallOn(level, entity);
        }
        else
        {
            bounceUp(entity);
        }
    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if (!level.isClientSide && player.isCreative())
        {
            BedPart part = state.getValue(PART);
            if (part == BedPart.FOOT)
            {
                BlockPos neighbourPos = pos.relative(getNeighbourDirection(part, state.getValue(FACING)));
                BlockState neighbourState = level.getBlockState(neighbourPos);
                if (Helpers.isBlock(neighbourState, this) && neighbourState.getValue(PART) == BedPart.HEAD)
                {
                    level.setBlock(neighbourPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, neighbourPos, Block.getId(neighbourState));
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * {@link BedBlock#bounceUp(Entity)}
     */
    private void bounceUp(Entity entity)
    {
        final Vec3 v = entity.getDeltaMovement();
        if (v.y < 0.0D)
        {
            double factor = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(v.x, -v.y * (double) 0.66F * factor, v.z);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction facing = state.getValue(FACING);
        if (!(Helpers.isBlock(world.getBlockState(pos.relative(facing)), TFCBlocks.THATCH_BED.get())) || world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP))
        {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(PART, FACING));
    }
}
