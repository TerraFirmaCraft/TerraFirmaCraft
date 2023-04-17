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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;


public class PotBlock extends FirepitBlock
{
    private static final VoxelShape POT_SHAPE = Shapes.or(BASE_SHAPE, box(2, 0, 2, 14, 14, 14));

    public PotBlock(ExtendedProperties properties)
    {
        super(properties, POT_SHAPE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        super.animateTick(state, level, pos, rand);
        level.getBlockEntity(pos, TFCBlockEntities.POT.get()).ifPresent(pot -> {
            if (!pot.shouldRenderAsBoiling()) return;
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < rand.nextInt(5) + 4; i++)
            {
                level.addParticle(TFCParticles.BUBBLE.get(), false, x + rand.nextFloat() * 0.375 - 0.1875, y + 0.625, z + rand.nextFloat() * 0.375 - 0.1875, 0, 0.05D, 0);
            }
            level.addParticle(TFCParticles.STEAM.get(), false, x, y + 0.8, z, Helpers.triangle(rand), 0.5, Helpers.triangle(rand));
            level.playLocalSound(x, y, z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.7F + 0.4F, false);
        });
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.POT.get()).map(pot -> {
            final ItemStack stack = player.getItemInHand(hand);
            if (!pot.isBoiling() && stack.isEmpty() && player.isShiftKeyDown())
            {
                if (state.getValue(LIT))
                {
                    player.hurt(TFCDamageSources.POT, 1.0F);
                    Helpers.playSound(level, pos, SoundEvents.LAVA_EXTINGUISH);
                }
                else
                {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCItems.POT.get()));
                    AbstractFirepitBlockEntity.convertTo(level, pos, state, pot, TFCBlocks.FIREPIT.get());
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (Helpers.isItem(stack.getItem(), TFCTags.Items.EXTINGUISHER))
            {
                pot.extinguish(state);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (!pot.isBoiling() && FluidHelpers.transferBetweenBlockEntityAndItem(stack, pot, player, hand))
            {
                pot.markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                if (!pot.isBoiling())
                {
                    final InteractionResult interactResult = pot.interactWithOutput(player, stack);
                    if (interactResult != InteractionResult.PASS)
                    {
                        return interactResult;
                    }
                }

                if (player instanceof ServerPlayer serverPlayer)
                {
                    Helpers.openScreen(serverPlayer, pot, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }).orElse(InteractionResult.PASS);
    }

    @Override
    protected double getParticleHeightOffset()
    {
        return 0.8D;
    }
}
