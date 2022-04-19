/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.dries007.tfc.common.blocks.devices.Tiered;
import net.dries007.tfc.util.Metal;

public class RockAnvilBlock extends Block implements Tiered
{
    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 14, 16);

    private final Supplier<? extends Block> raw;

    public RockAnvilBlock(Properties properties, Supplier<? extends Block> raw)
    {
        super(properties);

        this.raw = raw;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return new ItemStack(raw.get());
    }

    @Override
    public int getTier()
    {
        return Metal.Tier.TIER_0.ordinal();
    }
}
