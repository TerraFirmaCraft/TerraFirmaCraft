/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface ItemPropertyProviderBlock
{
    /**
     * Types are allowed to be compared with reference equality semantics, do not create new instances for these!
     */
    static Type of(ResourceLocation id)
    {
        return new Type(id);
    }

    static int getValue(Block block, Type type)
    {
        return block instanceof ItemPropertyProviderBlock provider ? provider.getValue(type) : 0;
    }

    int getValue(Type type);

    record Type(ResourceLocation id) {}
}
