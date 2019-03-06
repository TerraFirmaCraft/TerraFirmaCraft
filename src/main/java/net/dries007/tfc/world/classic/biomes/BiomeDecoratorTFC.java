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
import net.dries007.tfc.world.classic.worldgen.*;

@ParametersAreNonnullByDefault
public class BiomeDecoratorTFC extends BiomeDecorator
{
    private final int lilyPadPerChunk;
    private final int waterPlantsPerChunk;
    private final WorldGenPumpkinTFC pumpkinGen;
    private final WorldGenWaterPlants waterplantGen;

    private final WorldGenPlantTFC plantGen;

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
        this.reedGen = null;

        plantGen = new WorldGenPlantTFC();

        reedGen = new WorldGenTallPlant(Blocks.REEDS); // todo: replace block?
        sandGen = new WorldGenSandTFC(7);
        pumpkinGen = new WorldGenPumpkinTFC(Blocks.PUMPKIN); // todo: replace block?
        waterplantGen = new WorldGenWaterPlants(); // todo: replace block
    }

    @Override
    public void decorate(final World world, final Random rng, final Biome biome, final BlockPos chunkPos)
    {
        ChunkDataTFC data = ChunkDataTFC.get(world, chunkPos);
        if (data == null || !data.isInitialized()) return;

        final float temperature = ClimateTFC.getHeightAdjustedBiomeTemp(world, chunkPos);
        final float rainfall = ChunkDataTFC.getRainfall(world, chunkPos);
        final float floraDensity = data.getFloraDensity(); // Use for various plant based decoration (tall grass, those vanilla jungle shrub things, etc.)

        this.chunkPos = chunkPos;
        // todo: settings for all the rarities?

//        final Random rng = new Random(world.getSeed() + ((this.chunkPos.getX() >> 7) - (this.chunkPos.getZ() >> 7)) * (this.chunkPos.getZ() >> 7));

//        TerraFirmaCraft.getLog().info("decorate {} ({}) {} {}", chunkPos, biome.getBiomeName(), lilyPadPerChunk, waterPlantsPerChunk);
        // todo: crops

        for (int i = 0; i < 10; i++)
        {
            if (rng.nextInt(100) >= 10) continue;

            final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));

            if (temperature >= 15f + rng.nextFloat() * 5f && rainfall > 75f)
                reedGen.generate(world, rng, p2);
        }

        if (rng.nextInt(300) == 0)
        {
            pumpkinGen.generate(world, rng, world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8)));
        }

        for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
        {
            if (plant.isValidLocation(temperature, rainfall))
            {
                plantGen.setGeneratedPlant(plant);

                if (plant.getPlantType() == Plant.PlantType.LILYPAD)
                {
                    for (int i = 0; i < lilyPadPerChunk * floraDensity; i++)
                    {
                        final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        if (ClimateTFC.getHeightAdjustedBiomeTemp(world, p2) >= 7)
                            plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.CACTUS)
                {
                    if (rng.nextFloat() < floraDensity)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS)
                {
                    // todo: will need to lower how many times this is run if more grasses are added
                    for (int i = 0; i < 1 + floraDensity * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else if (plant.getPlantType() == Plant.PlantType.TALL_GRASS)
                {
                    for (int i = rng.nextInt(16); i < 1 + floraDensity * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
                else
                {
                    for (float i = rng.nextInt(64); i < 1 + floraDensity * 5; i++)
                    {
                        final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                        plantGen.generate(world, rng, p2);
                    }
                }
            }
        }
    }
}
