/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;

/**
 * Use this as a helper to detect multiblocks
 *
 * todo: add a rotation detector / helper? (bloomery would benefit from this)
 */
public class Multiblock implements BiPredicate<World, BlockPos>
{
    private final List<BiPredicate<World, BlockPos>> conditions;

    public Multiblock()
    {
        this.conditions = new ArrayList<>();
    }

    public Multiblock match(BlockPos posOffset, BiPredicate<World, BlockPos> condition)
    {
        conditions.add((world, pos) -> condition.test(world, pos.add(posOffset)));
        return this;
    }

    public Multiblock match(BlockPos posOffset, Predicate<IBlockState> stateMatcher)
    {
        conditions.add((world, pos) -> stateMatcher.test(world.getBlockState(pos.add(posOffset))));
        return this;
    }

    public <T extends TileEntity> Multiblock match(BlockPos posOffset, Predicate<T> tileEntityPredicate, Class<T> teClass)
    {
        conditions.add((world, pos) -> {
            T tile = Helpers.getTE(world, pos.add(posOffset), teClass);
            if (tile != null)
            {
                return tileEntityPredicate.test(tile);
            }
            return false;
        });
        return this;
    }

    public Multiblock matchOneOf(BlockPos baseOffset, Multiblock subMultiblock)
    {
        conditions.add((world, pos) -> {
            for (BiPredicate<World, BlockPos> condition : subMultiblock.conditions)
            {
                if (condition.test(world, pos.add(baseOffset)))
                {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    @Override
    public boolean test(World world, BlockPos pos)
    {
        for (BiPredicate<World, BlockPos> condition : conditions)
        {
            if (!condition.test(world, pos))
            {
                return false;
            }
        }
        return true;
    }
}
