/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin
{
    @Shadow private static boolean renderCutout;
    @Shadow @Final private static ChunkRenderTypeSet CUTOUT_MIPPED;
    @Shadow @Final private static ChunkRenderTypeSet SOLID;

    @Inject(method="getRenderLayers", at=@At("HEAD"), cancellable = true)
    private static void getTFCLeavesRenderType(@NotNull BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir)
    {
        final Block block = state.getBlock();
        if (block instanceof ILeavesBlock || block instanceof KrummholzBlock || block instanceof FallenLeavesBlock)
        {
            cir.setReturnValue(renderCutout ? CUTOUT_MIPPED : SOLID);
        }
    }
}
