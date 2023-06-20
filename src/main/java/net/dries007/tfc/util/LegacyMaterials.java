/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

// todo: 1.20. inline and remove
public final class LegacyMaterials
{
    public static boolean isSolid(BlockState state)
    {
        return state.getMaterial().isSolid();
    }

    public static boolean isLiquid(BlockState state)
    {
        return state.getMaterial().isLiquid();
    }

    public static boolean blocksMotion(BlockState state)
    {
        return state.getMaterial().blocksMotion();
    }

    public static boolean isFlammable(BlockState state)
    {
        return state.getMaterial().isFlammable();
    }

    public static boolean isReplaceable(BlockState state)
    {
        // IMPORTANT: this should be state.canBeReplaced() (no params)
        // the tag is for datapack use only!
        return state.getMaterial().isReplaceable();
    }

    public static boolean isMeltyIce(BlockState state)
    {
        // probably can be merged into fluid helpers or something else
        return state.getMaterial() == Material.ICE;
    }

    public static boolean isSolidIce(BlockState state)
    {
        // probably can be merged into fluid helpers or something else
        return state.getMaterial() == Material.ICE_SOLID;
    }

    public static boolean isStructuralAir(BlockState state)
    {
        // this one is a little weird, we'll have to see what mojo does
        return state.getMaterial() == Material.STRUCTURAL_AIR;
    }
}
