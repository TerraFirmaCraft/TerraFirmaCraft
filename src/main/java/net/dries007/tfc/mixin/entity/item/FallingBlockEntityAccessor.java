package net.dries007.tfc.mixin.entity.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.FallingBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor
{
    /**
     * Needed by {@link net.dries007.tfc.common.entities.TFCFallingBlockEntity} as it overrides the tick method and the field is private rather than protected.
     */
    @Accessor("blockState")
    void accessor$setBlockState(BlockState blockState);
}
