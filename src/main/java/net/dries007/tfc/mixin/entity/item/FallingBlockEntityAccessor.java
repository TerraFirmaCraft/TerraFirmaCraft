/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.entity.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.FallingBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor
{
    /**
     * Needed by {  net.dries007.tfc.common.entities.TFCFallingBlockEntity} as it overrides the tick method and the field is private rather than protected.
     */
    @Accessor("blockState")
    void accessor$setBlockState(BlockState blockState);
}
