/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.soil.SoilBlockType;

/**
 * Interface for use in {@link SoilBlockType} registration calls.
 * <br>
 * For the methods that return {@link Supplier}s, it is <strong>not required</strong> to implement all possible inputs - only the ones that are personally needed, as {@link RegistrySoilVariant} should never leave your own mod/addon's control.
 */
public interface RegistrySoilVariant
{
    /**
     * @return A block of this soil variant, of the provided type.
     */
    Supplier<? extends Block> getBlock(SoilBlockType type);

    /**
     * @return A dried mud brick item of this soil variant.
     */
    Supplier<? extends Item> mudBrick();
}
