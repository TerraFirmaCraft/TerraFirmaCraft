/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.entities.TFCEntities;

@Mixin(Tadpole.class)
public class TadpoleMixin
{
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void inject$spawnTadpoles(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }

    @Inject(method = "ageUp()V", at = @At("HEAD"), cancellable = true)
    private void inject$ageUp(CallbackInfo ci)
    {
        final Tadpole pole = (Tadpole) (Object) this;
        if (pole.level() instanceof ServerLevel server)
        {
            final Frog frog = TFCEntities.FROG.get().create(server);
            if (frog != null)
            {
                frog.moveTo(pole.getX(), pole.getY(), pole.getZ(), pole.getYRot(), pole.getXRot());
                frog.finalizeSpawn(server, server.getCurrentDifficultyAt(frog.blockPosition()), MobSpawnType.CONVERSION, null);
                frog.setNoAi(pole.isNoAi());
                if (pole.hasCustomName())
                {
                    frog.setCustomName(pole.getCustomName());
                    frog.setCustomNameVisible(pole.isCustomNameVisible());
                }

                frog.setPersistenceRequired();
                pole.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15F, 1.0F);
                server.addFreshEntityWithPassengers(frog);
                pole.discard();
                ci.cancel();
            }
        }
    }
}
