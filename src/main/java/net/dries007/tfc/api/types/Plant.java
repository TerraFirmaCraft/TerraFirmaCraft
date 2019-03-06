/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.blocks.plants.*;

public class Plant extends IForgeRegistryEntry.Impl<Plant>
{
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final int minSun;
    private final int maxSun;

    private final PlantType plantType;
    private final Material material;

    /**
     * Addon mods that want to add flowers should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your flowers. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name     the ResourceLocation registry name of this flower
     * @param material the Material of this flower, defaults to Material.PLANTS if not present
     * @param minTemp  min temperature
     * @param maxTemp  max temperature
     * @param minRain  min rainfall
     * @param maxRain  max rainfall
     * @param minSun   min light level
     * @param maxSun   max light level
     */
    public Plant(@Nonnull ResourceLocation name, Material material, float minTemp, float maxTemp, float minRain, float maxRain, PlantType plantType, int minSun, int maxSun)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.minSun = minSun;
        this.maxSun = maxSun;

        this.plantType = plantType;
        this.material = material;

        setRegistryName(name);
    }

    public Plant(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minRain, float maxRain, PlantType plantType, int minSun, int maxSun)
    {
        this(name, Material.PLANTS, minTemp, maxTemp, minRain, maxRain, plantType, minSun, maxSun);
    }

    public boolean isValidLocation(float temp, float rain, int sunlight)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain && minSun <= sunlight && maxSun >= sunlight;
    }

    public boolean isValidLocation(float temp, float rain)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain;
    }

    public boolean isValidSunlight(int sunlight)
    {
        return minSun <= sunlight && maxSun >= sunlight;
    }

    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public PlantType getPlantType()
    {
        return plantType;
    }

    public Material getMaterial()
    {
        return material;
    }

    public enum PlantType
    {
        STANDARD(BlockPlantTFC::new),
        DOUBLE(BlockDoublePlantTFC::new),
        CREEPING(BlockCreepingPlantTFC::new),
        LILYPAD(BlockLilyPadTFC::new),
        DESERT(BlockPlantTFC::new),
        CACTUS(BlockCactusTFC::new),
        SHORT_GRASS(BlockShortGrassTFC::new),
        TALL_GRASS(BlockTallGrassTFC::new);

        private final Function<Plant, BlockPlantTFC> supplier;

        PlantType(@Nonnull Function<Plant, BlockPlantTFC> supplier)
        {
            this.supplier = supplier;
        }

        public BlockPlantTFC create(Plant plant)
        {
            return supplier.apply(plant);
        }
    }
}
