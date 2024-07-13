/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCTorchBlock extends TorchBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static void onRandomTick(ServerLevel level, BlockPos pos, BlockState placeState)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(torch ->
        {
            final int torchTicks = TFCConfig.SERVER.torchTicks.get();
            if (torch.getTicksSinceUpdate() > torchTicks && torchTicks > 0)
            {
                level.setBlockAndUpdate(pos, placeState);
            }
        });
    }

    private final ExtendedProperties properties;

    public TFCTorchBlock(ExtendedProperties properties, SimpleParticleType particle)
    {
        super(particle, properties.properties());
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!level.isClientSide())
        {
            ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held.getItem(), TFCTags.Items.CAN_BE_LIT_ON_TORCH))
            {
                held.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.TORCH.get()));
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        onRandomTick(level, pos, TFCBlocks.DEAD_TORCH.get().defaultBlockState());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
