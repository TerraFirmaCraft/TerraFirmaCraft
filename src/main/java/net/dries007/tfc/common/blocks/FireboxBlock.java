/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.FireboxBlockEntity;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.util.Helpers;

public class FireboxBlock extends DeviceBlock implements IBellowsConsumer
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FireboxBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof FireboxBlockEntity firebox)
        {
            final ItemStackHandler inv = firebox.getInventory();
            if (firebox.isItemValid(0, stack))
            {
                final ItemStack leftover = Helpers.insertAllSlots(inv, stack.split(1));
                if (!leftover.isEmpty())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, leftover);
                    return ItemInteractionResult.FAIL;
                }
                else
                {
                    return ItemInteractionResult.SUCCESS;
                }
            }
            else if (stack.isEmpty() && player.isShiftKeyDown() && !inv.getStackInSlot(0).isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, inv.getStackInSlot(0));
                return ItemInteractionResult.SUCCESS;
            }
            else if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(firebox, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (entity instanceof LivingEntity && state.getValue(LIT))
        {
            entity.hurt(entity.damageSources().hotFloor(), 1f);
        }
        super.stepOn(level, pos, state, entity);
    }


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIT) && random.nextInt(7) == 0)
        {
            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                ParticleUtils.spawnParticleOnFace(level, pos, dir, ParticleTypes.FLAME, Helpers.getRandomSpeedRanges(random).scale(0.1), 0.55);
            }
            if (random.nextInt(14) == 0)
            {
                level.playLocalSound(pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }
        }
    }

    @Override
    public void intakeAir(Level level, BlockPos pos, BlockState state, int amount)
    {
        if (level.getBlockEntity(pos) instanceof FireboxBlockEntity firebox)
        {
            firebox.intakeAir(amount);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIT));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }
}
