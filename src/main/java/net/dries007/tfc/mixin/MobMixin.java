/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

@Mixin(Mob.class)
public abstract class MobMixin
{
    @Inject(method = "getEquipmentForSlot", at = @At("HEAD"), cancellable = true)
    private static void inject$getEquipmentForSlot(EquipmentSlot slot, int multiplier, CallbackInfoReturnable<Item> cir)
    {
        if (!TFCConfig.SERVER.enableVanillaMobsSpawningWithVanillaEquipment.get())
        {
            // Disable mobs spawning with
            final TagKey<Item> tag = switch (slot)
            {
                case FEET -> TFCTags.Items.MOB_FEET_ARMOR;
                case LEGS -> TFCTags.Items.MOB_LEG_ARMOR;
                case CHEST -> TFCTags.Items.MOB_CHEST_ARMOR;
                case HEAD -> TFCTags.Items.MOB_HEAD_ARMOR;
                default -> null;
            };
            // Always set the return value, so we prevent vanilla equipment, even if we can't find a replacement
            cir.setReturnValue(tag == null ? null : Helpers.randomItem(tag, RandomSource.create()).orElse(null));
        }
    }

    @Inject(method = "populateDefaultEquipmentEnchantments", at = @At(value = "HEAD"), cancellable = true)
    private void inject$populateDefaultEquipmentEnchantments(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty, CallbackInfo ci)
    {
        if (!TFCConfig.SERVER.enableVanillaMobsSpawningWithEnchantments.get())
        {
            ci.cancel();
        }
    }
}
