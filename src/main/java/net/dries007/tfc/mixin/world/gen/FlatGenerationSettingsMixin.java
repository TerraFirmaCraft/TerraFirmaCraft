package net.dries007.tfc.mixin.world.gen;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;

import net.dries007.tfc.mixin.minecraftforge.registries.ForgeRegistryEntryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Carry over the biome registry name
 *
 * Required to fix a forge bug
 *
 * @see net.dries007.tfc.mixin.minecraftforge.common.ForgeHooksMixin
 */
@Mixin(FlatGenerationSettings.class)
@SuppressWarnings("ConstantConditions")
public abstract class FlatGenerationSettingsMixin
{
    @Inject(method = "getBiomeFromSettings", at = @At(value = "RETURN"), cancellable = true)
    private void inject$getBiomeFromSettings(CallbackInfoReturnable<Biome> cir)
    {
        Biome biome = cir.getReturnValue();
        if (biome.getRegistryName() == null)
        {
            ((ForgeRegistryEntryAccessor) (Object) biome).accessor$setRegistryName(((FlatGenerationSettings) (Object) this).getBiome().getRegistryName());
        }
    }
}
