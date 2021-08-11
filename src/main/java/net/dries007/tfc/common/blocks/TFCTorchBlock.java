/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;


import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCTorchBlock extends TorchBlock implements IForgeBlockProperties
{
    public static void onRandomTick(ServerLevel world, BlockPos pos, BlockState placeState)
    {
        TickCounterTileEntity entity = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (entity != null)
        {
            final int torchTicks = TFCConfig.SERVER.torchTicks.get();
            if (entity.getTicksSinceUpdate() > torchTicks && torchTicks > 0)
            {
                world.setBlockAndUpdate(pos, placeState);
            }
        }
    }

    private final ForgeBlockProperties properties;

    public TFCTorchBlock(ForgeBlockProperties properties, ParticleOptions particle)
    {
        super(properties.properties(), particle);
        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (!world.isClientSide())
        {
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem().is(TFCTags.Items.CAN_BE_LIT_ON_TORCH))
            {
                held.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.TORCH.get()));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand)
    {
        onRandomTick(world, pos, TFCBlocks.DEAD_TORCH.get().defaultBlockState());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(worldIn, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }
}
