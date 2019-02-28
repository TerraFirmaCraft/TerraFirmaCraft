/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import java.util.function.BiFunction;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.blocks.plants.BlockCreepingPlantTFC;
import net.dries007.tfc.objects.blocks.plants.BlockDoublePlantTFC;
import net.dries007.tfc.objects.blocks.plants.BlockLilyPadTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;

public class Plant extends IForgeRegistryEntry.Impl<Plant>
{
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;

    private final PlantType plantType;
    private final Material material;

    /**
     * Addon mods that want to add flowers should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your flowers. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name    the ResourceLocation registry name of this flower
     * @param minTemp min temperature
     * @param maxTemp max temperature
     * @param minRain min rainfall
     * @param maxRain max rainfall
     */
    public Plant(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minRain, float maxRain, PlantType plantType)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        this.plantType = plantType;
        this.material = Material.PLANTS;

        setRegistryName(name);
    }

    public Plant(@Nonnull ResourceLocation name, Material material, float minTemp, float maxTemp, float minRain, float maxRain, PlantType plantType)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;

        this.plantType = plantType;
        this.material = material;

        setRegistryName(name);
    }

    public boolean isValidLocation(float temp, float rain)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain;
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
        PLANT(BlockPlantTFC::new),
        DOUBLEPLANT(BlockDoublePlantTFC::new),
        CREEPINGPLANT(BlockCreepingPlantTFC::new),
        LILYPAD(BlockLilyPadTFC::new),
        DESERTPLANT(BlockPlantTFC::new),
        STACKPLANT(BlockPlantTFC::new),
        CACTUS(BlockPlantTFC::new);

        public static Block create(Plant plant, PlantType type)
        {
            return type.supplier.apply(plant, type);
        }

        private final BiFunction<Plant, PlantType, Block> supplier;

        PlantType(@Nonnull BiFunction<Plant, PlantType, Block> supplier)
        {
            this.supplier = supplier;
        }
    }
}
