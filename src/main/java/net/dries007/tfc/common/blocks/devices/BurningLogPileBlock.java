/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.LegacyMaterials;

public class BurningLogPileBlock extends BaseEntityBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static void tryLightLogPile(Level level, BlockPos pos)
    {
        LogPileBlockEntity pile = level.getBlockEntity(pos, TFCBlockEntities.LOG_PILE.get()).orElse(null);
        if (pile != null)
        {
            int logs = pile.logCount();
            pile.clearContent(); // avoid dumping when onRemove is called
            level.setBlockAndUpdate(pos, TFCBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);

            BurningLogPileBlockEntity burningPile = level.getBlockEntity(pos, TFCBlockEntities.BURNING_LOG_PILE.get()).orElse(null);
            if (burningPile != null)
            {
                burningPile.light(logs);
                tryLightNearby(level, pos);
            }
        }
    }

    private static boolean isValidCoverBlock(BlockState offsetState, Level world, BlockPos pos, Direction side)
    {
        if (Helpers.isBlock(offsetState, TFCTags.Blocks.CHARCOAL_COVER_WHITELIST))// log pile, charcoal pile, this
        {
            return true;
        }
        return !LegacyMaterials.isFlammable(offsetState) && offsetState.isFaceSturdy(world, pos, side);
    }

    private static void tryLightNearby(Level world, BlockPos pos)
    {
        if (world.isClientSide()) return;
        for (Direction side : Helpers.DIRECTIONS)
        {
            final BlockPos offsetPos = pos.relative(side);
            final BlockState offsetState = world.getBlockState(offsetPos);
            if (isValidCoverBlock(offsetState, world, offsetPos, side.getOpposite()))
            {
                if (Helpers.isBlock(offsetState, TFCBlocks.LOG_PILE.get()))
                {
                    tryLightLogPile(world, offsetPos);
                }
            }
            else if (offsetState.isAir())
            {
                // If we can, try and spawn fire in the offset position - but don't delete anything in the process
                world.setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
            }
            else if (world.random.nextInt(7) == 0)
            {
                // If we can't spawn fire directly above, but we don't have a valid cover, then this block is invalid, but it can't spawn fire and let it burn itself away
                // So, we have a low chance of replacing this block, with fire.
                world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                return;
            }
        }
    }

    private final ExtendedProperties properties;

    public BurningLogPileBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        tryLightNearby(level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
    {
        double x = pos.getX() + rand.nextFloat();
        double y = pos.getY() + 1;
        double z = pos.getZ() + rand.nextFloat();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0f, 0.1f + 0.1f * rand.nextFloat(), 0f);
        if (rand.nextInt(12) == 0)
        {
            level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
        for (int i = 0; i < rand.nextInt(3); i++)
        {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, (0.5F - rand.nextFloat()) / 10, 0.1f + rand.nextFloat() / 8, (0.5F - rand.nextFloat()) / 10);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return new ItemStack(Items.CHARCOAL);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }
}
