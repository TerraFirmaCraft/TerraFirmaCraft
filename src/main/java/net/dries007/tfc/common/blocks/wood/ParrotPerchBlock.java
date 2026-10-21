/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.List;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.entities.prey.TFCParrot;
import net.dries007.tfc.mixin.accessor.PlayerAccessor;
import net.dries007.tfc.util.Helpers;

public class ParrotPerchBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension
{
    private static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 4, 0, 4, 12, 2, 12),
        Helpers.rotateShape(dir, 7, 2, 7, 9, 13, 9),
        Helpers.rotateShape(dir, 1, 13, 7, 15, 15, 9)
    ));
    private static final Vec3[] SEATS = {new Vec3(3.5, 15, 8), new Vec3(12, 15, 8)};

    private static Vec3 rotateSeat(Direction facing, Vec3 seat)
    {
        return switch (facing)
        {
            case EAST -> new Vec3(16 - seat.z, seat.y, seat.x);
            case SOUTH -> new Vec3(16 - seat.x, seat.y, 16 - seat.z);
            case WEST -> new Vec3(seat.z, seat.y, 16 - seat.x);
            default -> seat;
        };
    }

    private final ExtendedProperties properties;

    public ParrotPerchBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        final boolean left = TFCParrot.isTFCParrot(player.getShoulderEntityLeft());
        if (left || TFCParrot.isTFCParrot(player.getShoulderEntityRight()))
        {
            final Direction facing = state.getValue(FACING);
            for (Vec3 seat : getSeats())
            {
                final Vec3 seatPos = Vec3.atLowerCornerOf(pos).add(rotateSeat(facing, seat).scale(1 / 16f));
                if (level.getEntitiesOfClass(TFCParrot.class, AABB.ofSize(seatPos, 0.4, 0.4, 0.4).move(0, 0.2, 0)).isEmpty())
                {
                    if (level instanceof ServerLevel serverLevel)
                    {
                        final CompoundTag shoulder = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
                        EntityType.create(shoulder, level).ifPresent(entity -> {
                            if (entity instanceof TFCParrot parrot)
                            {
                                parrot.perchOn(seatPos, getPerchedYaw(facing));
                                serverLevel.addWithUUID(parrot);
                            }
                        });
                        if (left)
                        {
                            ((PlayerAccessor) player).invoke$setShoulderEntityLeft(new CompoundTag());
                        }
                        else
                        {
                            ((PlayerAccessor) player).invoke$setShoulderEntityRight(new CompoundTag());
                        }
                        level.playSound(null, seatPos.x, seatPos.y, seatPos.z, SoundEvents.PARROT_FLY, SoundSource.NEUTRAL, 1f, 1f);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        if (TFCParrot.canCarryOnShoulder(player))
        {
            final List<TFCParrot> perched = level.getEntitiesOfClass(TFCParrot.class, new AABB(pos).expandTowards(0, 0.5, 0), parrot -> parrot.isPerched() && parrot.isOwnedBy(player));
            if (!perched.isEmpty())
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    perched.getFirst().pickUpFromPerch(serverPlayer);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    protected Vec3[] getSeats()
    {
        return SEATS;
    }

    protected float getPerchedYaw(Direction facing)
    {
        return facing.toYRot();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return fakeBlockCodec();
    }
}