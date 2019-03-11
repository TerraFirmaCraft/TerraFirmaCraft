/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.blocks.plants.*;

import static net.dries007.tfc.world.classic.ChunkGenTFC.FRESH_WATER;
import static net.dries007.tfc.world.classic.ChunkGenTFC.SALT_WATER;

public class Plant extends IForgeRegistryEntry.Impl<Plant>
{
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final int minSun;
    private final int maxSun;
    private final int minWaterDepth;
    private final int maxWaterDepth;

    private final PlantType plantType;
    private final Material material;
    private final Boolean isClayMarking;

    /**
     * Addon mods that want to add flowers should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your flowers. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name          the ResourceLocation registry name of this flower
     * @param plantType     the type of plant
     * @param minTemp       min temperature
     * @param maxTemp       max temperature
     * @param minRain       min rainfall
     * @param maxRain       max rainfall
     * @param minSun        min light level
     * @param maxSun        max light level
     * @param minWaterDepth min water depth for water plants
     * @param maxWaterDepth max water depth for water plants
     */
    public Plant(@Nonnull ResourceLocation name, PlantType plantType, Boolean isClayMarking, float minTemp, float maxTemp, float minRain, float maxRain, int minSun, int maxSun, int minWaterDepth, int maxWaterDepth)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.minSun = minSun;
        this.maxSun = maxSun;
        this.minWaterDepth = minWaterDepth;
        this.maxWaterDepth = maxWaterDepth;

        this.plantType = plantType;
        this.material = plantType.getPlantMaterial();
        this.isClayMarking = isClayMarking;

        setRegistryName(name);
    }

    public Plant(@Nonnull ResourceLocation name, PlantType plantType, Boolean isClayMarking, float minTemp, float maxTemp, float minRain, float maxRain, int minSun, int maxSun)
    {
        this(name, plantType, isClayMarking, minTemp, maxTemp, minRain, maxRain, minSun, maxSun, 0, 0);
    }

    public boolean getIsClayMarking()
    {
        return isClayMarking;
    }

    public boolean isValidLocation(float temp, float rain, int sunlight)
    {
        return isValidTemp(temp) && isValidRain(rain) && isValidSunlight(sunlight);
    }

    public boolean isValidTemp(float temp)
    {
        return minTemp <= temp && maxTemp >= temp;
    }

    public boolean isValidAvgTemp(float temp)
    {
        return Math.abs(temp - ((minTemp + maxTemp) / 2)) < 10;
    }

    public boolean isValidRain(float rain)
    {
        return minRain <= rain && maxRain >= rain;
    }

    public boolean isValidSunlight(int sunlight)
    {
        return minSun <= sunlight && maxSun >= sunlight;
    }

    public boolean isValidFloatingWaterDepth(World world, BlockPos pos, IBlockState water)
    {
        int depthCounter = getMinWaterDepth();
        int maxDepth = getMaxWaterDepth();

        for (int i = 1; i <= depthCounter; ++i)
        {
            if (world.getBlockState(pos.down(i)) != water) return false;
        }

        while (world.getBlockState(pos.down(depthCounter)) == water)
        {
            depthCounter++;
        }
        return (maxDepth > 0) && depthCounter <= maxDepth + 1;
    }

    public int getMinWaterDepth()
    {
        return minWaterDepth;
    }

    public int getMaxWaterDepth()
    {
        return maxWaterDepth;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    @Nonnull
    public PlantType getPlantType()
    {
        return plantType;
    }

    @Nonnull
    public Material getMaterial()
    {
        return material;
    }

    public IBlockState getWaterType()
    {
        if (plantType == PlantType.FLOATING_SEA)
        {
            return SALT_WATER;
        }
        else
        {
            return FRESH_WATER;
        }
    }

    public enum PlantType
    {
        STANDARD(BlockPlantTFC::new),
        DOUBLE(BlockDoublePlantTFC::new),
        CREEPING(BlockCreepingPlantTFC::new),
        FLOATING(BlockFloatingWaterTFC::new),
        FLOATING_SEA(BlockFloatingWaterTFC::new),
        DESERT(BlockPlantTFC::new),
        CACTUS(BlockCactusTFC::new),
        SHORT_GRASS(BlockShortGrassTFC::new),
        TALL_GRASS(BlockTallGrassTFC::new),
        EPIPHYTE(BlockEpiphyteTFC::new),
        REED(BlockPlantTFC::new),
        DOUBLE_REED(BlockDoublePlantTFC::new);

        private final Function<Plant, BlockPlantTFC> supplier;

        PlantType(@Nonnull Function<Plant, BlockPlantTFC> supplier)
        {
            this.supplier = supplier;
        }

        public BlockPlantTFC create(Plant plant)
        {
            return supplier.apply(plant);
        }

        public final Material getPlantMaterial()
        {
            switch (this)
            {
                case CACTUS:
                    return Material.CACTUS;
                case SHORT_GRASS:
                case TALL_GRASS:
                    return Material.VINE;
                default:
                    return Material.PLANTS;
            }
        }
    }

    public enum EnumPlantTypeTFC
    {
        Clay,
        Dry,
        None;

        public String toString()
        {
            return name();
        }
    }
}
