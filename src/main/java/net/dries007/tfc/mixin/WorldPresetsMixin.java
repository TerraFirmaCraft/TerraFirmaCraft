package net.dries007.tfc.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.config.TFCConfig;

@Mixin(WorldPresets.class)
public abstract class WorldPresetsMixin
{
    @Inject(method = "createNormalWorldDimensions", at = @At("HEAD"), cancellable = true)
    private static void useTFCWorldAsDefault(RegistryAccess registryAccess, CallbackInfoReturnable<WorldDimensions> cir)
    {
        cir.setReturnValue(registryAccess.registryOrThrow(Registries.WORLD_PRESET)
            .getHolderOrThrow(ResourceKey.create(Registries.WORLD_PRESET, new ResourceLocation(TFCConfig.COMMON.defaultWorldPreset.get())))
            .value()
            .createWorldDimensions());
    }
}
