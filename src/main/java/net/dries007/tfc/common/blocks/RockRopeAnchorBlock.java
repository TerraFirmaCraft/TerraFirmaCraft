/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;

/**
 * A rope anchor created in-world by tying a rope to a {@link RockSpikeBlock} tip. It has no item, when
 * its rope is recalled it reverts back into the spike it was made from.
 */
public class RockRopeAnchorBlock extends RopeAnchorBlock
{
    private final Supplier<? extends Block> spike;

    public RockRopeAnchorBlock(ExtendedProperties properties, Supplier<? extends Block> spike)
    {
        super(properties);
        this.spike = spike;
    }

    public Supplier<? extends Block> getSpike()
    {
        return spike;
    }

    @Override
    protected BlockState getStateAfterRemoval(BlockState state)
    {
        return spike.get().defaultBlockState().setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return spike.get().getCloneItemStack(state, target, level, pos, player);
    }
}
