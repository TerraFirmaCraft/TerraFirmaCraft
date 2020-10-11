package net.dries007.tfc.mixin.block;

import net.minecraft.block.AbstractBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This allows editing of other blocks via two methods: adjusting their properties values, and the values that the properties are copied to in {@link AbstractBlock}
 */
@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor
{
    @Accessor("properties")
    AbstractBlock.Properties accessor$getProperties();

    @Accessor("speedFactor")
    void accessor$setSpeedFactor(float speedFactor);
}
