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
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.api.Wood;
import net.dries007.tfc.api.calendar.Climate;
import net.dries007.tfc.util.collections.DelayedRunnable;
import net.dries007.tfc.world.feature.BoulderConfig;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.feature.trees.ForestFeatureConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.placement.TFCPlacements;

public abstract class TFCBiome extends Biome implements ITFCBiome
{
    // todo: replace with actual blocks
    protected static final BlockState SALT_WATER = Blocks.WATER.getDefaultState(); // Custom salt water block
    protected static final BlockState FRESH_WATER = Blocks.WATER.getDefaultState(); // Vanilla water

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
            .precipitation(Climate.getDefaultRainType(temperature, rainfall))
            .temperature(temperature.getTemperature())
            .downfall(rainfall.getDownfall())
            // Since this is a registry object, we just do them all later for consistency
            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG))
        );
        this.temperature = temperature;
        this.rainfall = rainfall;

        this.biomeFeatures = new DelayedRunnable();
        this.biomeFeatures.enqueue(() -> {
            addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TFCFeatures.VEINS.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(NoPlacementConfig.NO_PLACEMENT_CONFIG)));

            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().withConfiguration(new BlockStateFeatureConfig(Blocks.LAVA.getDefaultState())).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(80))));

            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().withConfiguration(new BoulderConfig(Rock.BlockType.RAW, Rock.BlockType.RAW)).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().withConfiguration(new BoulderConfig(Rock.BlockType.RAW, Rock.BlockType.COBBLE)).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(60))));
            addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().withConfiguration(new BoulderConfig(Rock.BlockType.COBBLE, Rock.BlockType.MOSSY_COBBLE)).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(60))));

            addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TFCFeatures.FORESTS.get().withConfiguration(new ForestFeatureConfig(Stream.of(
                new ForestFeatureConfig.Entry(30f, 210f, 19f, 31f, Wood.Default.ACACIA.getTree().getFeature()),
                new ForestFeatureConfig.Entry(60f, 140f, -6f, 12f, Wood.Default.ASH.getTree().getFeature()),
                new ForestFeatureConfig.Entry(10f, 80f, -10f, 16f, Wood.Default.ASPEN.getTree().getFeature()),
                new ForestFeatureConfig.Entry(20f, 180f, -15f, 7f, Wood.Default.BIRCH.getTree().getFeature()),
                new ForestFeatureConfig.Entry(0f, 120f, 4f, 33f, Wood.Default.BLACKWOOD.getTree().getFeature()),
                new ForestFeatureConfig.Entry(160f, 320f, 11f, 35f, Wood.Default.CHESTNUT.getTree().getFeature()),
                new ForestFeatureConfig.Entry(280f, 480f, -2f, 14f, Wood.Default.DOUGLAS_FIR.getTree().getFeature()),
                new ForestFeatureConfig.Entry(80f, 250f, 7f, 29f, Wood.Default.HICKORY.getTree().getFeature()),
                new ForestFeatureConfig.Entry(210f, 500f, 15f, 35f, Wood.Default.KAPOK.getTree().getFeature()),
                new ForestFeatureConfig.Entry(140f, 360f, 3f, 20f, Wood.Default.MAPLE.getTree().getFeature()),
                new ForestFeatureConfig.Entry(180f, 430f, -8f, 12f, Wood.Default.OAK.getTree().getFeature()),
                new ForestFeatureConfig.Entry(280f, 500f, 16f, 35f, Wood.Default.PALM.getTree().getFeature()),
                new ForestFeatureConfig.Entry(60f, 250f, -15f, 7f, Wood.Default.PINE.getTree().getFeature()),
                new ForestFeatureConfig.Entry(10f, 190f, 8f, 18f, Wood.Default.ROSEWOOD.getTree().getFeature()),
                new ForestFeatureConfig.Entry(250f, 420f, -5f, 12f, Wood.Default.SEQUOIA.getTree().getFeature()),
                new ForestFeatureConfig.Entry(120f, 380f, -11f, 6f, Wood.Default.SPRUCE.getTree().getFeature()),
                new ForestFeatureConfig.Entry(120f, 290f, 17f, 33f, Wood.Default.SYCAMORE.getTree().getFeature()),
                new ForestFeatureConfig.Entry(10f, 240f, -8f, 17f, Wood.Default.WHITE_CEDAR.getTree().getFeature()),
                new ForestFeatureConfig.Entry(230f, 400f, 15f, 32f, Wood.Default.WILLOW.getTree().getFeature())
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
    public float getTemperatureRaw(BlockPos pos)
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
    public void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        getSurfaceBuilder().setSeed(seed);
        getSurfaceBuilder().buildSurface(random, chunkIn, this, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
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
        return getSurfaceBuilder().getConfig();
    }
}
