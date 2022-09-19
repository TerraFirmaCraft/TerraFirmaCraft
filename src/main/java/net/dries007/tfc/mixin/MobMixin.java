/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin
{
    @Inject(method = "getEquipmentForSlot", at = @At("HEAD"), cancellable = true)
    private static void inject$getEquipmentForSlot(EquipmentSlot slot, int multiplier, CallbackInfoReturnable<Item> cir)
    {
        if (!TFCConfig.SERVER.enableVanillaMobsSpawningWithVanillaEquipment.get())
        {
            Helpers.getRandomElement(ForgeRegistries.ITEMS, TFCTags.Items.mobEquipmentSlotTag(slot), new Random()).ifPresent(cir::setReturnValue);
        }
    }

    @Inject(method = "populateDefaultEquipmentEnchantments", at = @At(value = "HEAD"), cancellable = true)
    private void inject$populateDefaultEquipmentEnchantments(DifficultyInstance difficulty, CallbackInfo ci)
    {
        if (!TFCConfig.SERVER.enableVanillaMobsSpawningWithEnchantments.get())
        {
            ci.cancel();
        }
    }
}
