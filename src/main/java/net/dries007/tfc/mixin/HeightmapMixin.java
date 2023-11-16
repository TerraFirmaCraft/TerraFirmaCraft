/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.common.blocks.IBlockRain;

@Mixin(Heightmap.Types.class)
public abstract class HeightmapMixin
{
    @Shadow
    @Final
    @Mutable
    private Predicate<BlockState> isOpaque;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void inject$init(String key, int usage, String opaque, Heightmap.Usage usageType, Predicate<BlockState> predicate, CallbackInfo ci)
    {
        if (key.equals("MOTION_BLOCKING"))
        {
            final Predicate<BlockState> finalPredicate = predicate;
            isOpaque = state -> finalPredicate.test(state) || state.getBlock() instanceof IBlockRain;
        }
    }
}
