/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.TFCTags;

public class MetalBucketItem extends TFCBucketItem
{
    private final boolean containsLava;

    public MetalBucketItem(Properties properties, Supplier<Integer> capacity, boolean containsLava)
    {
        super(properties, capacity);
        this.containsLava = containsLava;
    }

    @Override
    public TagKey<Fluid> getWhitelistTag()
    {
        return containsLava ? TFCTags.Fluids.USABLE_IN_BLUE_STEEL_BUCKET : TFCTags.Fluids.USABLE_IN_RED_STEEL_BUCKET;
    }
}
