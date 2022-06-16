/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.mixin.accessor.BiomeGenerationSettingsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forge are you serious?
 * At least this goes away in 1.19
 *
 * https://github.com/MinecraftForge/MinecraftForge/issues/8743
 */
@Mixin(ForgeHooks.class)
public abstract class ForgeHooksMixin
{
    @Inject(method = "enhanceBiome", at = @At("HEAD"), cancellable = true, remap = false)
    private static void enhanceBiomeNotBlowingUpTagsThankYouVeryMuch(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings gen, MobSpawnSettings spawns, RecordCodecBuilder.Instance<Biome> codec, ForgeHooks.BiomeCallbackFunction callback, CallbackInfoReturnable<Biome> cir)
    {
        BiomeGenerationSettingsBuilder genBuilder = new BiomeGenerationSettingsBuilder(gen);
        MobSpawnSettingsBuilder spawnBuilder = new MobSpawnSettingsBuilder(spawns);
        BiomeLoadingEvent event = new BiomeLoadingEvent(name, climate, category, effects, genBuilder, spawnBuilder);
        MinecraftForge.EVENT_BUS.post(event);

        final BiomeGenerationSettings newGen = event.getGeneration().build();
        final List<HolderSet<PlacedFeature>> newHolders = new ArrayList<>(newGen.features());

        for (int i = 0; i < Math.min(newHolders.size(), gen.features().size()); i++)
        {
            final HolderSet<PlacedFeature> set = gen.features().get(i);
            if (set instanceof HolderSet.Named<PlacedFeature>)
            {
                newHolders.set(i, set);
            }
        }

        ((BiomeGenerationSettingsAccessor) newGen).accessor$setFeatures(newHolders);

        cir.setReturnValue(callback.apply(event.getClimate(), event.getCategory(), event.getEffects(), newGen, event.getSpawns().build()).setRegistryName(name));
    }
}
