/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


// todo: 1.20. inline and remove... alc come and look
public final class LegacyMaterials
{
    @SuppressWarnings("deprecation")
    public static boolean isSolid(BlockState state)
    {
        return state.isSolid();
    }

    @SuppressWarnings("deprecation")
    public static boolean isLiquid(BlockState state)
    {
        return state.liquid();
    }

    @SuppressWarnings("deprecation")
    public static boolean blocksMotion(BlockState state)
    {
        return state.blocksMotion();
    }

    public static boolean isReplaceable(BlockState state)
    {
        // IMPORTANT: this should be state.canBeReplaced() (no params)
        // the tag is for datapack use only!
        return state.canBeReplaced();
    }

    public static boolean isMeltyIce(BlockState state)
    {
        // probably can be merged into fluid helpers or something else
        return state.getBlock() == Blocks.ICE || state.getBlock() == Blocks.FROSTED_ICE;
    }

    public static boolean isSolidIce(BlockState state)
    {
        // probably can be merged into fluid helpers or something else
        return state.getBlock() == Blocks.PACKED_ICE || state.getBlock() == Blocks.BLUE_ICE;
    }

}
