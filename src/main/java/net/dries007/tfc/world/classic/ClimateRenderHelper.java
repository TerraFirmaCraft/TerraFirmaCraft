/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * This class is only to be used for rendering
 * It stores cached versions of the climate data on CLIENT ONLY
 */
public final class ClimateRenderHelper
{
    private static final Map<ChunkPos, ClimateData> MAP = new HashMap<>();
    private static final ClimateData DEFAULT = new ClimateData(0, 250, 0);

    @Nonnull
    public static ClimateData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    @Nonnull
    public static ClimateData get(ChunkPos pos)
    {
        return MAP.getOrDefault(pos, DEFAULT);
    }

    public static void update(ChunkPos pos, float temperature, float rainfall)
    {
        MAP.put(pos, new ClimateData(temperature, rainfall, pos.z * 16));
    }

    public static class ClimateData
    {
        private final float regionalTemp;
        private final float rainfall;
        private final int z;

        ClimateData(float regionalTemp, float rainfall, int z)
        {
            this.regionalTemp = regionalTemp;
            this.rainfall = rainfall;
            this.z = z;
        }

        public float getTemperature()
        {
            return ClimateTFC.getMonthAdjTemp(regionalTemp, z);
        }

        public float getRainfall()
        {
            return rainfall;
        }
    }
}
