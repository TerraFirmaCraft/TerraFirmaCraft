/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class PowderkegBlock extends SealableDeviceBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_UNSEALED = Shapes.join(SHAPE, box(2, 1, 2, 14, 16, 14), BooleanOp.ONLY_FIRST);
    private static final int[] IMAGE_TOOLTIP = {4, 3, 0, PowderkegBlockEntity.SLOTS - 1};

    public static void toggleSeal(Level level, BlockPos pos, BlockState state)
    {
        level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).ifPresent(powderkeg -> {
            final boolean previousSealed = state.getValue(SEALED);
            level.setBlockAndUpdate(pos, state.setValue(SEALED, !previousSealed));
            if (previousSealed)
            {
                powderkeg.onUnseal();
            }
            else
            {
                powderkeg.onSeal();
            }
        });
    }

    public PowderkegBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(SEALED, false).setValue(LIT, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final PowderkegBlockEntity powderkeg = level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).orElse(null);
        if (powderkeg != null)
        {
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                if (state.getValue(LIT))
                {
                    powderkeg.setLit(false, player);
                    Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                }
                else
                {
                    toggleSeal(level, pos, state);
                    Helpers.playPlaceSound(level, pos, state);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, powderkeg, powderkeg.getBlockPos());
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public int[] getImageTooltipParameters()
    {
        return IMAGE_TOOLTIP;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion)
    {
        if (!state.getValue(LIT))
        {
            level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).ifPresent(keg -> keg.setLit(true, explosion.getExploder()));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIT))
        {
            final int count = random.nextInt(3) + 5;
            for (int i = 0; i < count; i++)
            {
                final double x = pos.getX() + random.nextFloat();
                final double z = pos.getZ() + random.nextFloat();
                final double y = pos.getY() + 0.98f + (random.nextFloat() / 5f);
                level.addParticle(TFCParticles.SPARK.get(), x, y, z, Helpers.uniform(random, -5f, 5f), 3f + random.nextFloat(), Helpers.uniform(random, -5f, 5f));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT));
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (level.hasNeighborSignal(pos) && !state.getValue(LIT) && state.getValue(SEALED))
        {
            level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).ifPresent(keg -> keg.setLit(true, null));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(SEALED) ? SHAPE : SHAPE_UNSEALED;
    }

}
