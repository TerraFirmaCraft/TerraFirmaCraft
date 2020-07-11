package net.dries007.tfc.tests;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.config.NoiseLayerType;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @see net.dries007.tfc.world.chunkdata.ChunkDataProvider constructor
 */
@Disabled
class ClimateNoiseTests
{
    static final ImageUtil<INoise2D> IMAGES = ImageUtil.noise(target -> (x, y) -> target.noise((float) x, (float) y), builder -> builder.scale(ImageUtil.Scales.DYNAMIC_RANGE).color(ImageUtil.Colors.LINEAR_BLUE_RED).size(1000).dimensions(-40_000, -40_000, 40_000, 40_000));

    @Test
    void testTemperaturePeriodic()
    {
        IMAGES.draw("temperature_periodic_z_80km", NoiseLayerType.PERIODIC_Z.create(1234, TFCConfig.COMMON.temperatureLayerScale.get()).scaled(-10, 30));
    }

    @Test
    void testTemperatureGradient()
    {
        IMAGES.draw("temperature_gradient_z_80km", NoiseLayerType.GRADIENT_Z.create(1234, TFCConfig.COMMON.temperatureLayerScale.get()).scaled(-10, 30));
    }

    @Test
    void testTemperatureNoisy()
    {
        IMAGES.draw("temperature_noisy_80km", NoiseLayerType.NOISE.create(1234, TFCConfig.COMMON.temperatureLayerScale.get()).scaled(-10, 30));
    }

    @Test
    void testRainfallPeriodic()
    {
        IMAGES.draw("rainfall_periodic_x_80km", NoiseLayerType.PERIODIC_X.create(1234, TFCConfig.COMMON.rainfallLayerScale.get()).scaled(0, 500).flattened(0, 500));
    }

    @Test
    void testRainfallGradient()
    {
        IMAGES.draw("rainfall_gradient_x_80km", NoiseLayerType.GRADIENT_X.create(1234, TFCConfig.COMMON.rainfallLayerScale.get()).scaled(0, 500).flattened(0, 500));
    }
}
