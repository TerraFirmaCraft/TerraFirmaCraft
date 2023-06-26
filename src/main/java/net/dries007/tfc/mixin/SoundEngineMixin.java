package net.dries007.tfc.mixin;

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
public class SoundEngineMixin
{
    @Shadow @Final
    private static Set<ResourceLocation> ONLY_WARN_ONCE;

    private static final ResourceLocation VALID_SOUND_EVENT = SoundEvents.FIRE_EXTINGUISH.getLocation();

    @ModifyArg(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;getSoundEvent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/sounds/WeighedSoundEvents;"))
    private ResourceLocation preventLogSpamFromMissingVanillaSounds(ResourceLocation res)
    {
        if (((SoundEngine) (Object) this).soundManager.getSoundEvent(res) == null && res.getNamespace().equals("minecraft"))
        {
            ONLY_WARN_ONCE.add(res);
            return VALID_SOUND_EVENT;
        }
        return res;
    }

}
