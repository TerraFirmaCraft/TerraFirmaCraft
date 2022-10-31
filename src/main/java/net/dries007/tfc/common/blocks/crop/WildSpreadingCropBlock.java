/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class WildSpreadingCropBlock extends WildCropBlock
{
    private final Supplier<Supplier<? extends Block>> fruit;

    public WildSpreadingCropBlock(ExtendedProperties properties, Supplier<Supplier<? extends Block>> fruit)
    {
        super(properties);
        this.fruit = fruit;
    }

    public Block getFruit()
    {
        return fruit.get().get();
    }
}
