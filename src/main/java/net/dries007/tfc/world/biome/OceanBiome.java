/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

import net.dries007.tfc.world.biome.ITFCBiome.LargeGroup;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;

public class OceanBiome extends TFCBiome
{
    private final float depthMin, depthMax;

    public OceanBiome(boolean isDeep, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().biomeCategory(Category.OCEAN), temperature, rainfall);

        if (isDeep)
        {
            this.depthMin = TFCConfig.COMMON.seaLevel.get() - 36;
            this.depthMax = TFCConfig.COMMON.seaLevel.get() - 10;
        }
        else
        {
            this.depthMin = TFCConfig.COMMON.seaLevel.get() - 24;
            this.depthMax = TFCConfig.COMMON.seaLevel.get() - 6;
        }

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addOceanCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.UNDERWATER.get(), SurfaceBuilder.CONFIG_EMPTY);

            DefaultBiomeFeatures.addDefaultSeagrass(this);
            if (temperature == BiomeTemperature.FROZEN)
            {
                DefaultBiomeFeatures.addIcebergs(this);
                DefaultBiomeFeatures.addBlueIce(this);
            }
            else if (temperature == BiomeTemperature.COLD || temperature == BiomeTemperature.NORMAL)
            {
                DefaultBiomeFeatures.addColdOceanExtraVegetation(this);
            }
            else if (temperature == BiomeTemperature.LUKEWARM)
            {
                DefaultBiomeFeatures.addDeepWarmSeagrass(this);
                DefaultBiomeFeatures.addLukeWarmKelp(this);
            }
            else if (temperature == BiomeTemperature.WARM)
            {
                DefaultBiomeFeatures.addDeepWarmSeagrass(this);
                addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(ImmutableList.of(Feature.CORAL_TREE.configured(IFeatureConfig.NONE), Feature.CORAL_CLAW.configured(IFeatureConfig.NONE), Feature.CORAL_MUSHROOM.configured(IFeatureConfig.NONE)))).decorated(Placement.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(20, 400.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));
                addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEA_PICKLE.configured(new CountConfig(20)).decorated(Placement.CHANCE_TOP_SOLID_HEIGHTMAP.configured(new ChanceConfig(16))));
            }
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Uses domain warping to achieve a swirly hills effect
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, depthMin, depthMax);
    }

    @Override
    public BlockState getWaterState()
    {
        return SALT_WATER;
    }

    @Override
    public LargeGroup getLargeGroup()
    {
        return LargeGroup.OCEAN;
    }
}