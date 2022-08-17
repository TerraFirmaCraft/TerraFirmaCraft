/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.List;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.biome.TFCBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin
{
    /**
     * Flatten top-level {@link net.dries007.tfc.world.feature.MultipleFeature}s found in the biome generation source, if the biome source is a {@link net.dries007.tfc.world.biome.TFCBiomeSource}.
     */
    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "buildFeaturesPerStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/BiomeGenerationSettings;features()Ljava/util/List;"))
    private List<HolderSet<PlacedFeature>> flattenTopLevelMultipleFeature(BiomeGenerationSettings settings)
    {
        return (Object) this instanceof TFCBiomeSource ?
            Helpers.flattenTopLevelMultipleFeature(settings) :
            settings.features();
    }
}
