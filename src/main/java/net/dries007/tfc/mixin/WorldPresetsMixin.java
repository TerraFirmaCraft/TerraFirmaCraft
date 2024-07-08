/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

@Mixin(WorldPresets.class)
public abstract class WorldPresetsMixin
{
    @Inject(method = "createNormalWorldDimensions", at = @At("HEAD"), cancellable = true)
    private static void useTFCWorldAsDefault(RegistryAccess registryAccess, CallbackInfoReturnable<WorldDimensions> cir)
    {
        cir.setReturnValue(registryAccess.registryOrThrow(Registries.WORLD_PRESET)
            .getHolderOrThrow(ResourceKey.create(Registries.WORLD_PRESET, Helpers.resourceLocation(TFCConfig.COMMON.defaultWorldPreset.get())))
            .value()
            .createWorldDimensions());
    }
}
