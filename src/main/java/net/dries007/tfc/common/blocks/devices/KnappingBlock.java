/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.KnappingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class KnappingBlock extends DeviceBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final Vec3 PLANE_NORMAL = new Vec3(0.0, 1.0, 0.0);

    /**
     * Projects the player's look vector onto the top face plane to determine which point on the block was hit.
     * Returns a Vec3 with coordinates in [0, 1] range relative to the block's local origin.
     */
    private static Vec3 calculatePoint(Vec3 rayVector, Vec3 rayPoint)
    {
        return rayPoint.subtract(rayVector.scale(rayPoint.dot(PLANE_NORMAL) / rayVector.dot(PLANE_NORMAL)));
    }

    /**
     * Normalizes block-local hit coordinates (world X/Z in [0,1]) to canonical north-facing grid coordinates,
     * so that recipe patterns always match regardless of which direction the block was placed facing.
     *
     * Derivation: the canonical orientation is north-facing (col increases east, row increases south).
     * For other facings we rotate so that the player's left→right maps to col 0→4 and far→near maps to row 0→4.
     */
    public static float[] normalizeHit(float x, float z, Direction facing)
    {
        return switch (facing)
        {
            case SOUTH -> new float[] { 1f - x, 1f - z };
            case WEST  -> new float[] { z,       1f - x };
            case  EAST -> new float[] { 1f - z,  x      };
            default    -> new float[] { x,        z      }; // NORTH
        };
    }

    public KnappingBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        // Remove the knapping block if the ground below is no longer face-sturdy
        if (facing == Direction.DOWN && !facingState.isFaceSturdy(worldIn, facingPos, Direction.UP, SupportType.CENTER))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        // Only accept rock items used as knapping tools
        if (Helpers.isItem(stack, TFCTags.Items.ROCK_KNAPPING))
        {
            if (level.getBlockEntity(pos) instanceof KnappingBlockEntity knapping)
            {
                // Project the hit location into block-local coordinates [0, 1]
                final Vec3 point = calculatePoint(
                    player.getLookAngle(),
                    hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ())
                );

                final float[] norm = normalizeHit((float) point.x, (float) point.z, state.getValue(FACING));
                knapping.onClicked(norm[0], norm[1]);
                doParticles(level, pos, knapping, point);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static void doParticles(Level level, BlockPos pos, KnappingBlockEntity knapping, Vec3 point)
    {
        if (level instanceof ServerLevel server)
        {
            final ItemStack rockItem = knapping.getInventory().getStackInSlot(0);
            if (!rockItem.isEmpty())
            {
                server.sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, rockItem),
                    pos.getX() + point.x,
                    pos.getY() + 0.0625,
                    pos.getZ() + point.z,
                    3,
                    Helpers.triangle(level.random) / 2.0D,
                    level.random.nextDouble() / 4.0D,
                    Helpers.triangle(level.random) / 2.0D,
                    0.15f
                );
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
