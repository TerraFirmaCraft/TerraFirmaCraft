/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import java.util.Set;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin
{
    @Shadow @Final
    private static Set<ResourceLocation> ONLY_WARN_ONCE;

    @ModifyArg(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;getSoundEvent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/sounds/WeighedSoundEvents;"))
    private ResourceLocation preventLogSpamFromMissingVanillaSounds(ResourceLocation res)
    {
        if (((SoundEngine) (Object) this).soundManager.getSoundEvent(res) == null && res.getNamespace().equals("minecraft"))
        {
            ONLY_WARN_ONCE.add(res);
            return SoundEvents.FIRE_EXTINGUISH.getLocation();
        }
        return res;
    }
}
