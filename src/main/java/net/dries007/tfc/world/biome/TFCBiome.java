/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.collections.DelayedRunnable;
import net.dries007.tfc.world.feature.BoulderConfig;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.feature.trees.ForestFeatureConfig;
import net.dries007.tfc.world.noise.INoise2D;

public abstract class TFCBiome extends Biome implements ITFCBiome
{
    // todo: replace with actual blocks
    protected static final BlockState SALT_WATER = Blocks.WATER.defaultBlockState(); // Custom salt water block
    protected static final BlockState FRESH_WATER = Blocks.WATER.defaultBlockState(); // Vanilla water

    /**
     * Used for initial biome assignments. TFC overrides this to use out temperature models
     */
    private static RainType getDefaultRainType(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        if (rainfall == BiomeRainfall.ARID)
        {
            return RainType.NONE;
        }
        else if (temperature == BiomeTemperature.FROZEN || temperature == BiomeTemperature.COLD)
        {
            return RainType.SNOW;
        }
        else
        {
            return RainType.RAIN;
        }
    }

    protected final DelayedRunnable biomeFeatures;

    private final BiomeTemperature temperature;
    private final BiomeRainfall rainfall;

    // These are assigned later as they are dependent on registry objects
    protected ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig> lazySurfaceBuilder;
    private BiomeVariants variantHolder;

    protected TFCBiome(Builder builder, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(builder
            // Unused
            .depth(0).scale(0).parent(null)
            // Provide reasonable defaults, TFC doesn't use these
            .waterColor(temperature.getWaterColor())
            .waterFogColor(temperature.getWaterFogColor())
            .precipitation(getDefaultRainType(temperature, rainfall))
            .temperature(temperature.getTemperature())
            .downfall(rainfall.getDownfall())
            // Since this is a registry object, we just do them all later for consistency
            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.CONFIG_EMPTY))
        );
        this.temperature = temperature;
        this.rainfall = rainfall;

        this.biomeFeatures = new DelayedRunnable();
        this.biomeFeatures.enqueue(() -> {
            addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TFCFeatures.VEINS.get().configured(NoFeatureConfig.NONE).decorated(Placement.NOPE.configured(NoPlacementConfig.NONE)));

            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().configured(new BlockStateFeatureConfig(Blocks.WATER.defaultBlockState())).decorated(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configured(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().configured(new BlockStateFeatureConfig(Blocks.LAVA.defaultBlockState())).decorated(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configured(new ChanceConfig(80))));

            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().configured(new BoulderConfig(Rock.BlockType.RAW, Rock.BlockType.RAW)).decorated(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configured(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().configured(new BoulderConfig(Rock.BlockType.RAW, Rock.BlockType.COBBLE)).decorated(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configured(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().configured(new BoulderConfig(Rock.BlockType.COBBLE, Rock.BlockType.MOSSY_COBBLE)).decorated(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configured(new ChanceConfig(60))));

            addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, TFCFeatures.EROSION.get().configured(NoFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));

            addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TFCFeatures.FORESTS.get().configured(new ForestFeatureConfig(Stream.of(
                new ForestFeatureConfig.Entry(30f, 210f, 21f, 31f, Wood.Default.ACACIA.getTree().getNormalFeature(), Wood.Default.ACACIA.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(60f, 140f, -6f, 12f, Wood.Default.ASH.getTree().getNormalFeature(), Wood.Default.ASH.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(10f, 180f, -10f, 16f, Wood.Default.ASPEN.getTree().getNormalFeature(), Wood.Default.ASPEN.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(20f, 180f, -15f, 7f, Wood.Default.BIRCH.getTree().getNormalFeature(), Wood.Default.BIRCH.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(0f, 120f, 15f, 35f, Wood.Default.BLACKWOOD.getTree().getNormalFeature(), Wood.Default.BLACKWOOD.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(160f, 320f, 11f, 35f, Wood.Default.CHESTNUT.getTree().getNormalFeature(), Wood.Default.CHESTNUT.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(290f, 500f, -4f, 15f, Wood.Default.DOUGLAS_FIR.getTree().getNormalFeature(), Wood.Default.DOUGLAS_FIR.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(90f, 250f, 7f, 27f, Wood.Default.HICKORY.getTree().getNormalFeature(), Wood.Default.HICKORY.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(240f, 500f, 15f, 35f, Wood.Default.KAPOK.getTree().getNormalFeature(), Wood.Default.KAPOK.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(140f, 410f, -5f, 20f, Wood.Default.MAPLE.getTree().getNormalFeature(), Wood.Default.MAPLE.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(180f, 430f, -10f, 12f, Wood.Default.OAK.getTree().getNormalFeature(), Wood.Default.OAK.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(280f, 500f, 20f, 35f, Wood.Default.PALM.getTree().getNormalFeature(), Wood.Default.PALM.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(60f, 250f, -15f, 7f, Wood.Default.PINE.getTree().getNormalFeature(), Wood.Default.PINE.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(10f, 190f, 5f, 20f, Wood.Default.ROSEWOOD.getTree().getNormalFeature(), Wood.Default.ROSEWOOD.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(250f, 420f, -5f, 12f, Wood.Default.SEQUOIA.getTree().getNormalFeature(), Wood.Default.SEQUOIA.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(120f, 430f, -14f, 7f, Wood.Default.SPRUCE.getTree().getNormalFeature(), Wood.Default.SPRUCE.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(120f, 290f, 17f, 33f, Wood.Default.SYCAMORE.getTree().getNormalFeature(), Wood.Default.SYCAMORE.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(10f, 240f, -8f, 17f, Wood.Default.WHITE_CEDAR.getTree().getNormalFeature(), Wood.Default.WHITE_CEDAR.getTree().getOldGrowthFeature()),
                new ForestFeatureConfig.Entry(260f, 480f, 15f, 32f, Wood.Default.WILLOW.getTree().getNormalFeature(), Wood.Default.WILLOW.getTree().getOldGrowthFeature())
            ).collect(Collectors.toList()))));
        });
    }

    public BiomeVariants getVariants()
    {
        return variantHolder;
    }

    public void setVariantHolder(BiomeVariants variantHolder)
    {
        this.variantHolder = variantHolder;
    }

    public BiomeTemperature getTemperature()
    {
        return temperature;
    }

    public BiomeRainfall getRainfall()
    {
        return rainfall;
    }

    public abstract INoise2D createNoiseLayer(long seed);

    public BlockState getWaterState()
    {
        return FRESH_WATER;
    }

    public <C extends ISurfaceBuilderConfig> void setSurfaceBuilder(SurfaceBuilder<C> surfaceBuilder, C config)
    {
        setSurfaceBuilder(new ConfiguredSurfaceBuilder<>(surfaceBuilder, config));
    }

    /**
     * Called after registry events have been fired, runs biome-specific feature registration.
     */
    public void registerFeatures()
    {
        biomeFeatures.run();
    }

    @Override
    public float getTemperatureNoCache(BlockPos pos)
    {
        // Vanilla expects < 0.15 = snowy, and generally returns values between -1 and 2.
        return MathHelper.clamp(0.15f + Climate.getTemperature(pos) / 30f, -1, 2);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getGrassColor(double posX, double posZ)
    {
        BlockPos pos = new BlockPos(posX, 0, posZ);
        double temp = MathHelper.clamp((Climate.getTemperature(pos) + 30) / 60, 0, 1);
        double rain = MathHelper.clamp((Climate.getRainfall(pos) - 50) / 400, 0, 1);
        return GrassColors.get(temp, rain);
    }

    @Override
    public void buildSurfaceAt(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        getSurfaceBuilder().initNoise(seed);
        getSurfaceBuilder().apply(random, chunkIn, this, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
    }

    @Override
    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder()
    {
        if (lazySurfaceBuilder == null)
        {
            LOGGER.warn("Surface builder was null when accessed, falling back to vanilla surface builder instead! Biome = {}", this);
            lazySurfaceBuilder = surfaceBuilder;
        }
        return lazySurfaceBuilder;
    }

    public void setSurfaceBuilder(ConfiguredSurfaceBuilder<? extends ISurfaceBuilderConfig> surfaceBuilder)
    {
        this.lazySurfaceBuilder = surfaceBuilder;
    }

    @Override
    public ISurfaceBuilderConfig getSurfaceBuilderConfig()
    {
        return getSurfaceBuilder().getSurfaceBuilderConfiguration();
    }
}