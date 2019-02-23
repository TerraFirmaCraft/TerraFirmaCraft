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
import net.minecraftforge.common.BiomeDictionary;

import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
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

    private final WorldGenTallGrassTFC sparseGrassGen;
    private final WorldGenTallGrassTFC standardGrassGen;
    private final WorldGenTallGrassTFC lushGrassGen;
    private final WorldGenTallGrassTFC desertGrassGen;

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
        this.waterlilyGen = null;

        sparseGrassGen = new WorldGenTallGrassTFC(BlockTallGrassTFC.EnumGrassType.SPARSE);
        standardGrassGen = new WorldGenTallGrassTFC(BlockTallGrassTFC.EnumGrassType.STANDARD);
        lushGrassGen = new WorldGenTallGrassTFC(BlockTallGrassTFC.EnumGrassType.LUSH);
        desertGrassGen = new WorldGenTallGrassTFC(BlockTallGrassTFC.EnumGrassType.DESERT);

        reedGen = new WorldGenTallPlant(Blocks.REEDS); // todo: replace block?
        sandGen = new WorldGenSandTFC(7);
        waterlilyGen = new WorldGenWaterlilyTFC(); // todo: replace block?
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

        for (int i = 0; i < lilyPadPerChunk; i++)
        {
            waterlilyGen.generate(world, rng, world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8)));
        }

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

        if (temperature > 15f && rainfall < 75f && rng.nextBoolean())
        {
            final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
            cactusGen.generate(world, rng, p2);
        }

        for (int i = 0; i < waterPlantsPerChunk; i++)
        {
            final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
            if (ClimateTFC.getHeightAdjustedBiomeTemp(world, p2) >= 7)
                waterplantGen.generate(world, rng, p2);
        }
/*
          if (rainfall < 75f || BlocksTFC.isSand(world.getBlockState(pos.down()))) return EnumGrassType.DESERT;
        else if (temperature > 20f && rainfall > 300f) return EnumGrassType.LUSH;
        else if (temperature > 15f && rainfall > 150f) return EnumGrassType.STANDARD;
        else return EnumGrassType.SPARSE;
*/
        if (rainfall < 75f)
        {
            for (int i = 0; i < floraDensity * 5; i++)
            {
                final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                if (!BiomeDictionary.hasType(world.getBiome(p2), BiomeDictionary.Type.BEACH))
                    desertGrassGen.generate(world, rng, p2);
            }
        }
        else if (temperature > 20f && rainfall > 300f)
        {
            for (int i = 0; i < 3 + floraDensity * 5; i++)
            {
                final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                if (!BiomeDictionary.hasType(world.getBiome(p2), BiomeDictionary.Type.BEACH))
                    lushGrassGen.generate(world, rng, p2);
            }
        }
        else if (temperature > 15f && rainfall > 150f)
        {
            for (int i = 0; i < 1 + floraDensity * 5; i++)
            {
                final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                if (!BiomeDictionary.hasType(world.getBiome(p2), BiomeDictionary.Type.BEACH))
                    standardGrassGen.generate(world, rng, p2);
            }
        }
        else
        {
            for (int i = 0; i < floraDensity * 5; i++)
            {
                final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
                if (!BiomeDictionary.hasType(world.getBiome(p2), BiomeDictionary.Type.BEACH))
                    sparseGrassGen.generate(world, rng, p2);
            }
        }
    }
}
