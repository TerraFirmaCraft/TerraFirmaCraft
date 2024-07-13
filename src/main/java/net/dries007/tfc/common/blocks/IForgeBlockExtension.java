/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;

/**
 * This implements some of the more annoying methods in {@link IBlockExtension} which would otherwise require implementing across all manner of vanilla subclasses.
 */
public interface IForgeBlockExtension extends IBlockExtension
{
    ExtendedProperties getExtendedProperties();

    @Override
    default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return getExtendedProperties().getFlammability();
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return getExtendedProperties().getFireSpreadSpeed();
    }

    @Nullable
    @Override
    default PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob entity)
    {
        final PathType type = getExtendedProperties().getPathType();
        return type != null ? type : IBlockExtension.super.getBlockPathType(state, level, pos, entity);
    }

    @Override
    default float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos)
    {
        return getExtendedProperties().getEnchantmentPower(state);
    }

    @Override
    default ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return getExtendedProperties().getCloneItemStack(state.getBlock());
    }

    /**
     * @return A non-functional, dummy block codec because we don't want to bother implementing these while Mojang still doesn't use them
     */
    @SuppressWarnings("unchecked")
    default  <T extends Block> MapCodec<T> fakeBlockCodec()
    {
        return (MapCodec<T>) Block.CODEC;
    }
}
