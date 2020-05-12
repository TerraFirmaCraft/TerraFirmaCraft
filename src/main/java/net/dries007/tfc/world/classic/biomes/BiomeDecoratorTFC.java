/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenPlantTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenSandTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;

@ParametersAreNonnullByDefault
public class BiomeDecoratorTFC extends BiomeDecorator
{
    private final int lilyPadPerChunk;
    private final int waterPlantsPerChunk;
    private final WorldGenWildCrops wildCropsGen;

    private final WorldGenPlantTFC plantGen;
    private int standardCount = 0;
    private int tallCount = 0;
    private int creepingCount = 0;
    private int hangingCount = 0;
    private int floatingCount = 0;
    private int floatingSeaCount = 0;
    private int desertCount = 0;
    private int dryCount = 0;
    private int cactusCount = 0;
    private int grassCount = 0;
    private int tallGrassCount = 0;
    private int epiphyteCount = 0;
    private int reedCount = 0;
    private int reedSeaCount = 0;
    private int waterCount = 0;
    private int waterSeaCount = 0;
    private int mushroomCount = 0;


    public BiomeDecoratorTFC(int lilyPadPerChunk, int waterPlantsPerChunk)
    {
        this.lilyPadPerChunk = lilyPadPerChunk;
        this.waterPlantsPerChunk = waterPlantsPerChunk;

        this.clayGen = null;
        this.sandGen = null;
        this.gravelGen = null;
        this.flowerGen = null;
        this.mushroomBrownGen = null;
        this.mushroomRedGen = null;
        this.bigMushroomGen = null;

        plantGen = new WorldGenPlantTFC();

        sandGen = new WorldGenSandTFC(7);
        wildCropsGen = new WorldGenWildCrops();

        for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
        {
            switch (plant.getPlantType())
            {
                case TALL_PLANT:
                    tallCount++;
                    break;
                case CREEPING:
                    creepingCount++;
                    break;
                case HANGING:
                    hangingCount++;
                    break;
                case FLOATING:
                    floatingCount++;
                    break;
                case FLOATING_SEA:
                    floatingSeaCount++;
                    break;
                case DESERT:
                case DESERT_TALL_PLANT:
                    desertCount++;
                    break;
                case DRY:
                case DRY_TALL_PLANT:
                    dryCount++;
                    break;
                case CACTUS:
                    cactusCount++;
                    break;
                case SHORT_GRASS:
                    grassCount++;
                    break;
                case TALL_GRASS:
                    tallGrassCount++;
                    break;
                case EPIPHYTE:
                    epiphyteCount++;
                    break;
                case REED:
                case TALL_REED:
                    reedCount++;
                    break;
                case REED_SEA:
                case TALL_REED_SEA:
                    reedSeaCount++;
                    break;
                case WATER:
                case TALL_WATER:
                case EMERGENT_TALL_WATER:
                    waterCount++;
                    break;
                case WATER_SEA:
                case TALL_WATER_SEA:
                case EMERGENT_TALL_WATER_SEA:
                    waterSeaCount++;
                    break;
                case MUSHROOM:
                    mushroomCount++;
                    break;
                default:
                    standardCount++;
            }
        }
    }

    @Override
    public void decorate(final World world, final Random rng, final Biome biome, final BlockPos chunkPos)
    {
        ChunkPos forgeChunkPos = new ChunkPos(chunkPos); // actual ChunkPos instead of BlockPos, used for events
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, rng, forgeChunkPos));

        ChunkDataTFC data = ChunkDataTFC.get(world, chunkPos);
        if (!data.isInitialized()) return;

        final float avgTemperature = ClimateTFC.getAvgTemp(world, chunkPos);
        final float rainfall = ChunkDataTFC.getRainfall(world, chunkPos);
        final float floraDensity = data.getFloraDensity(); // Use for various plant based decoration (tall grass, those vanilla jungle shrub things, etc.)
        final float floraDiversity = data.getFloraDiversity();

        this.chunkPos = chunkPos;
        // todo: settings for all the rarities?

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.MUSHROOM && plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);

                    for (float i = rng.nextInt(Math.round(mushroomCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                    {
                        BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, blockPos);
                    }
                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.CACTUS))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.CACTUS && plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);

                    for (int i = rng.nextInt(Math.round((cactusCount + 32) / floraDiversity)); i < (1 + floraDensity) * 3; i++)
                    {
                        BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, blockPos);
                    }

                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.LILYPAD))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);
                    switch (plant.getPlantType())
                    {
                        case FLOATING:
                        {
                            for (int i = rng.nextInt(Math.round(floatingCount / floraDiversity)); i < floraDensity * lilyPadPerChunk; i++)
                            {
                                BlockPos blockPos = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case FLOATING_SEA:
                        {
                            for (int i = rng.nextInt(Math.round((floatingSeaCount + 64) / floraDiversity)); i < floraDensity * lilyPadPerChunk; i++)
                            {
                                BlockPos blockPos = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                        }
                    }
                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.REED))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);
                    switch (plant.getPlantType())
                    {
                        case REED:
                        case TALL_REED:
                        {
                            for (int i = rng.nextInt(Math.round(reedCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case REED_SEA:
                        case TALL_REED_SEA:
                        {
                            for (int i = rng.nextInt(Math.round(reedSeaCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                        }
                    }
                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);
                    switch (plant.getPlantType())
                    {
                        case WATER:
                        case TALL_WATER:
                        case EMERGENT_TALL_WATER:
                        {
                            for (int i = rng.nextInt(Math.round(waterCount / floraDiversity)); i < floraDensity * waterPlantsPerChunk; i++)
                            {
                                BlockPos blockPos = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case WATER_SEA:
                        case TALL_WATER_SEA:
                        case EMERGENT_TALL_WATER_SEA:
                        {
                            for (int i = rng.nextInt(Math.round(waterSeaCount / floraDiversity)); i < floraDensity * waterPlantsPerChunk; i++)
                            {
                                BlockPos blockPos = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case EPIPHYTE:
                        {
                            for (float i = rng.nextInt(Math.round(epiphyteCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case CREEPING:
                        {
                            for (float i = rng.nextInt(Math.round((creepingCount + 32) / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case HANGING:
                        {
                            for (float i = rng.nextInt(Math.round(hangingCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case TALL_PLANT:
                        {
                            for (float i = rng.nextInt(Math.round((tallCount + 8) / floraDiversity)); i < (1 + floraDensity) * 3; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case STANDARD:
                        {
                            for (float i = rng.nextInt(Math.round((standardCount + 32) / floraDiversity)); i < (1 + floraDensity) * 3; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                        }
                    }
                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);
                    switch (plant.getPlantType())
                    {
                        case DESERT:
                        case DESERT_TALL_PLANT:
                        {
                            for (float i = rng.nextInt(Math.round((desertCount + 16) / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case DRY:
                        case DRY_TALL_PLANT:
                        {
                            for (float i = rng.nextInt(Math.round((dryCount + 16) / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                        }
                    }
                }
            }
        }

        if (TerrainGen.decorate(world, rng, forgeChunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS))
        {
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
                {
                    plantGen.setGeneratedPlant(plant);
                    switch (plant.getPlantType())
                    {
                        case SHORT_GRASS:
                        {
                            for (int i = rng.nextInt(Math.round(grassCount / floraDiversity)); i < (3 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                            break;
                        }
                        case TALL_GRASS:
                        {
                            for (int i = rng.nextInt(Math.round((tallGrassCount + 8) / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                            {
                                BlockPos blockPos = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                                plantGen.generate(world, rng, blockPos);
                            }
                        }
                    }
                }
            }
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, rng, forgeChunkPos));
    }
}
