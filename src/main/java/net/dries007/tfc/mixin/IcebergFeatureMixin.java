/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.feature.TFCIcebergFeature;

@Mixin(IcebergFeature.class)
public class IcebergFeatureMixin
{
    @WrapOperation(
        method = "carve",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;WATER:Lnet/minecraft/world/level/block/Block;")
    )
    private Block replaceWaterWithTFCWaterInCarve(Operation<Block> original)
    {
        return TFCIcebergFeature.is(this) ? TFCBlocks.SALT_WATER.get() : original.call();
    }

    @WrapOperation(
        method = "carve",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;WATER:Lnet/minecraft/world/level/block/Block;")
    )
    private Block replaceWaterWithTFCWaterInSetIcebergBlock(Operation<Block> original)
    {
        return TFCIcebergFeature.is(this) ? TFCBlocks.SALT_WATER.get() : original.call();
    }

}
