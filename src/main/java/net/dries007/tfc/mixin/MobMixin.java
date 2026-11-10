/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tracker.WeatherHelpers;

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

    @WrapOperation(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    private boolean isSunBurnTickWithSolarDay(Level level, Operation<Boolean> original)
    {
        final Mob mob = (Mob) (Object) this;
        final BlockPos pos = BlockPos.containing(mob.getX(), mob.getEyeY(), mob.getZ());
        // Vanilla only blocks burning in rain. It uses the global rain darkening the sky to also prevent it during snow but we can just check it directly i guess
        return SolarCalculator.getSkyDarken(level, pos) < 4 && WeatherHelpers.getPrecipitationAt(level, pos, Biome.Precipitation.NONE) == Biome.Precipitation.NONE;
    }

    @WrapOperation(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getLightLevelDependentMagicValue()F"))
    private float isSunBurnTickWithSolarLight(Mob mob, Operation<Float> original)
    {
        final Level level = mob.level();
        final BlockPos pos = BlockPos.containing(mob.getX(), mob.getEyeY(), mob.getZ());
        final float brightness = level.getMaxLocalRawBrightness(pos, SolarCalculator.getSkyDarken(level, pos)) / 15f;
        return Mth.lerp(level.dimensionType().ambientLight(), brightness / (4f - 3f * brightness), 1f);
    }
}
