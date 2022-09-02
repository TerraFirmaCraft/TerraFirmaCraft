/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;


import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class PowderkegBlock extends SealableDeviceBlock
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

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
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).map(powderkeg -> {
            final ItemStack stack = player.getItemInHand(hand);
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
                    Helpers.playSound(level, pos, SoundEvents.WOOD_PLACE);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                NetworkHooks.openGui(serverPlayer, powderkeg, powderkeg.getBlockPos());
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }).orElse(InteractionResult.PASS);
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
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
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
}
