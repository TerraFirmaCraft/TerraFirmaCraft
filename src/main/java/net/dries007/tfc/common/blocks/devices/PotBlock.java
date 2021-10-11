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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;


public class PotBlock extends FirepitBlock
{
    private static final VoxelShape POT_SHAPE = Shapes.or(
        BASE_SHAPE,
        box(4, 6, 3, 12, 9, 4),
        box(5, 9, 4, 12, 10, 5),
        box(4, 10, 3, 12, 11, 4),
        box(12, 6, 4, 13, 9, 12),
        box(11, 9, 5, 12, 10, 12),
        box(12, 10, 4, 13, 11, 12),
        box(4, 6, 12, 12, 9, 13),
        box(4, 9, 11, 11, 10, 12),
        box(4, 10, 12, 12, 11, 13),
        box(3, 6, 4, 4, 9, 12),
        box(4, 9, 4, 5, 10, 11),
        box(3, 10, 4, 4, 11, 12),
        box(4, 5, 4, 12, 7, 12),
        box(0, 12, 7.5, 16, 13, 8.5),
        box(1, 0, 7.5, 2, 12, 8.5),
        box(14, 0, 7.5, 15, 12, 8.5),
        box(7.5, 11, 3, 8.5, 13, 4),
        box(7.5, 13, 4, 8.5, 14, 12),
        box(7.5, 11, 12, 8.5, 13, 13));

    public PotBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand)
    {
        super.animateTick(state, world, pos, rand);
        PotBlockEntity te = Helpers.getBlockEntity(world, pos, PotBlockEntity.class);
        if (te != null && te.isBoiling())
        {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < rand.nextInt(5) + 4; i++)
            {
                world.addParticle(TFCParticles.BUBBLE.get(), false, x + rand.nextFloat() * 0.375 - 0.1875, y + 0.625, z + rand.nextFloat() * 0.375 - 0.1875, 0, 0.05D, 0);
            }
            world.addParticle(TFCParticles.STEAM.get(), false, x, y + 0.8, z, Helpers.triangle(rand), 0.5, Helpers.triangle(rand));
            world.playLocalSound(x, y, z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.7F + 0.4F, false);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final PotBlockEntity firepit = Helpers.getBlockEntity(world, pos, PotBlockEntity.class);
        if (firepit != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                if (state.getValue(LIT))
                {
                    player.hurt(TFCDamageSources.POT, 1.0F);
                    Helpers.playSound(world, pos, SoundEvents.LAVA_EXTINGUISH);
                }
                else
                {
                    AbstractFirepitBlockEntity.convertTo(world, pos, state, firepit, TFCBlocks.FIREPIT.get());
                }
                return InteractionResult.SUCCESS;
            }
            else if (TFCTags.Items.EXTINGUISHER.contains(stack.getItem()))
            {
                firepit.extinguish(state);
                return InteractionResult.SUCCESS;
            }
            else if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, null))
            {
                firepit.markForSync();
                return InteractionResult.SUCCESS;
            }
            else
            {
                final InteractionResult interactResult = firepit.interactWithOutput(player, stack);
                if (interactResult != InteractionResult.PASS)
                {
                    return interactResult;
                }

                if (player instanceof ServerPlayer)
                {
                    NetworkHooks.openGui((ServerPlayer) player, firepit, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return POT_SHAPE;
    }

    @Override
    protected double getParticleHeightOffset()
    {
        return 0.8D;
    }
}
