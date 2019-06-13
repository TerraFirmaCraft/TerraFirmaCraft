/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.functionalinterfaces;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface OreBlockQuantity
{
    int quantityDropped(IBlockState state, int fortune, @Nonnull Random random);

    /**
     * @param max imum number of items dropped per ore
     * @return from 1 to max inclusive
     */
    static OreBlockQuantity rng(int max)
    {
        return (state, fortune, random) -> 1 + random.nextInt(max);
    }
}
