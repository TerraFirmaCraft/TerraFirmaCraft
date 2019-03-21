/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenPlantTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenPumpkinTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenSandTFC;

@ParametersAreNonnullByDefault
public class BiomeDecoratorTFC extends BiomeDecorator
{
    private final int lilyPadPerChunk;
    private final int waterPlantsPerChunk;
    private final WorldGenPumpkinTFC pumpkinGen;

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
        pumpkinGen = new WorldGenPumpkinTFC(Blocks.PUMPKIN); // todo: replace block?

        for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
        {
            switch (plant.getPlantType().toString())
            {
                case "STANDARD":
                    standardCount++;
                    break;
                case "TALL_PLANT":
                    tallCount++;
                    break;
                case "CREEPING":
                    creepingCount++;
                    break;
                case "HANGING":
                    hangingCount++;
                    break;
                case "FLOATING":
                    floatingCount++;
                    break;
                case "FLOATING_SEA":
                    floatingSeaCount++;
                    break;
                case "DESERT":
                case "DESERT_TALL_PLANT":
                    desertCount++;
                    break;
                case "DRY":
                case "DRY_TALL_PLANT":
                    dryCount++;
                    break;
                case "CACTUS":
                    cactusCount++;
                    break;
                case "SHORT_GRASS":
                    grassCount++;
                    break;
                case "TALL_GRASS":
                    tallGrassCount++;
                    break;
                case "EPIPHYTE":
                    epiphyteCount++;
                    break;
                case "REED":
                case "TALL_REED":
                    reedCount++;
                    break;
                case "REED_SEA":
                case "TALL_REED_SEA":
                    reedSeaCount++;
                    break;
                case "WATER":
                case "TALL_WATER":
                case "EMERGENT_TALL_WATER":
                    waterCount++;
                    break;
                case "WATER_SEA":
                case "TALL_WATER_SEA":
                case "EMERGENT_TALL_WATER_SEA":
                    waterSeaCount++;
                case "MUSHROOM":
                    mushroomCount++;
                    break;
            }
        }
    }

    @Override
    public void decorate(final World world, final Random rng, final Biome biome, final BlockPos chunkPos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, chunkPos);
        if (data == null || !data.isInitialized()) return;

        final float avgTemperature = ClimateTFC.getAverageBiomeTemp(world, chunkPos);
        final float rainfall = ChunkDataTFC.getRainfall(world, chunkPos);
        final float floraDensity = data.getFloraDensity(); // Use for various plant based decoration (tall grass, those vanilla jungle shrub things, etc.)
        final float floraDiversity = data.getFloraDiversity();

        this.chunkPos = chunkPos;
        // todo: settings for all the rarities?

//        final Random rng = new Random(world.getSeed() + ((this.chunkPos.getX() >> 7) - (this.chunkPos.getZ() >> 7)) * (this.chunkPos.getZ() >> 7));

//        TerraFirmaCraft.getLog().info("decorate {} ({}) {} {}", chunkPos, biome.getBiomeName(), lilyPadPerChunk, waterPlantsPerChunk);
        // todo: crops

        if (rng.nextInt(300) == 0)
        {
            pumpkinGen.generate(world, rng, world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8)));
        }

        for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
        {
            if (plant.isValidTempForWorldGen(avgTemperature) && plant.isValidRain(rainfall))
            {
                plantGen.setGeneratedPlant(plant);

                if (plant.getPlantType() == Plant.PlantType.WATER || plant.getPlantType() == Plant.PlantType.TALL_WATER || plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER)
                {
                    for (int i = rng.nextInt(waterCount); i < waterPlantsPerChunk * (floraDensity + floraDiversity); i++)
                    {
                        final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                if (plant.getPlantType() == Plant.PlantType.WATER_SEA || plant.getPlantType() == Plant.PlantType.TALL_WATER_SEA || plant.getPlantType() == Plant.PlantType.EMERGENT_TALL_WATER_SEA)
                {
                    for (int i = rng.nextInt(waterSeaCount); i < waterPlantsPerChunk * (floraDensity + floraDiversity); i++)
                    {
                        final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.FLOATING)
                {
                    for (int i = rng.nextInt(floatingCount); i < lilyPadPerChunk * (floraDensity + floraDiversity); i++)
                    {
                        final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.FLOATING_SEA)
                {
                    for (int i = rng.nextInt(floatingSeaCount * 64); i < lilyPadPerChunk * (floraDensity + floraDiversity); i++)
                    {
                        final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.REED || plant.getPlantType() == Plant.PlantType.TALL_REED)
                {
                    for (int i = rng.nextInt(reedCount); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));

                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.REED_SEA || plant.getPlantType() == Plant.PlantType.TALL_REED_SEA)
                {
                    for (int i = rng.nextInt(reedSeaCount); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));

                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.CACTUS)
                {
                    for (int i = rng.nextInt(cactusCount * 16); i < (floraDensity + floraDiversity) * 3; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.EPIPHYTE)
                {
                    for (float i = rng.nextInt(epiphyteCount); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS)
                {
                    for (int i = rng.nextInt(grassCount); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.TALL_GRASS)
                {
                    for (int i = rng.nextInt(tallGrassCount * 4); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.CREEPING)
                {
                    for (float i = rng.nextInt(creepingCount * 32); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.HANGING)
                {
                    for (float i = rng.nextInt(hangingCount); i < (3 + floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.DESERT || plant.getPlantType() == Plant.PlantType.DESERT_TALL_PLANT)
                {
                    for (float i = rng.nextInt(desertCount * 16); i < (floraDensity + floraDiversity) * 3; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.DRY || plant.getPlantType() == Plant.PlantType.DRY_TALL_PLANT)
                {
                    for (float i = rng.nextInt(dryCount * 16); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.TALL_PLANT)
                {
                    for (float i = rng.nextInt(tallCount * 5); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.MUSHROOM)
                {
                    for (float i = rng.nextInt(mushroomCount * 5); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else
                {
                    for (float i = rng.nextInt(standardCount * 5); i < (floraDensity + floraDiversity) * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
            }
        }
    }
}
