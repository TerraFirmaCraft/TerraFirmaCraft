package net.dries007.tfc.common.entities;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.decorator.ClimateConfig;

public class PlacementPredicate<T extends Entity> implements EntitySpawnPlacementRegistry.IPlacementPredicate<T>
{
    private Fluid fluid = Fluids.EMPTY;
    private int chance = 1;
    private int distBelowSeaLevel = -1;
    private ClimateConfig climateConfig = null;

    /**
     * Required fluid to spawn in
     */
    public PlacementPredicate<T> fluid(Fluid fluid)
    {
        this.fluid = fluid;
        return this;
    }

    /**
     * 1/N chance to spawn in a given attempt
     */
    public PlacementPredicate<T> chance(int chance)
    {
        this.chance = chance;
        return this;
    }

    /**
     * Minimum distance below sea level to spawn at. Queries {@link World#getSeaLevel()}
     */
    public PlacementPredicate<T> belowSeaLevel(int distance)
    {
        this.distBelowSeaLevel = distance;
        return this;
    }

    public PlacementPredicate<T> climate(ClimateConfig config)
    {
        climateConfig = config;
        return this;
    }

    public PlacementPredicate<T> simpleClimate(float minTemp, float maxTemp, float minRain, float maxRain)
    {
        climateConfig = new ClimateConfig(minTemp, maxTemp, ClimateConfig.TemperatureType.AVERAGE, minRain, maxRain, ForestType.NONE, ForestType.OLD_GROWTH, true);
        return this;
    }

    @Override
    public boolean test(EntityType entity, IServerWorld world, SpawnReason reason, BlockPos pos, Random rand)
    {
        if (rand.nextInt(chance) != 0) return false;

        if (fluid != Fluids.EMPTY)
        {
            Fluid fluid = world.getFluidState(pos).getType();
            if (fluid != this.fluid)
            {
                return false;
            }
        }

        final int seaLevel = world.getLevel().getChunkSource().generator.getSeaLevel();
        if (distBelowSeaLevel != -1 && pos.getY() > seaLevel - distBelowSeaLevel)
        {
            return false;
        }

        if (climateConfig != null)
        {
            final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(world);
            final ChunkData data = provider.get(pos, ChunkData.Status.CLIMATE);
            if (!climateConfig.isValid(data, pos, rand))
            {
                return false;
            }
        }
        return true;
    }
}
