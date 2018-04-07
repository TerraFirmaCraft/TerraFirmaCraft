package net.dries007.tfc.objects.biomes;

import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenSandTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenTallPlant;
import net.dries007.tfc.world.classic.worldgen.WorldGenWaterPlants;
import net.dries007.tfc.world.classic.worldgen.WorldGenWaterlilyTFC;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenPumpkin;

import java.util.Random;

public class BiomeDecoratorTFC extends BiomeDecorator
{
    private final int lilyPadPerChunk;
    private final int waterPlantsPerChunk;
    private final WorldGenPumpkin pumpkinGen;
    private final WorldGenWaterPlants waterplantGen;

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
        this.cactusGen = null;
        this.waterlilyGen = null;

        reedGen = new WorldGenTallPlant(Blocks.REEDS); // todo: replace block
        sandGen = new WorldGenSandTFC(7);
        waterlilyGen = new WorldGenWaterlilyTFC();
        pumpkinGen = new WorldGenPumpkin(); // todo: customize
        cactusGen = new WorldGenTallPlant(Blocks.CACTUS); // todo: replace block
        waterplantGen = new WorldGenWaterPlants(); // todo: customize
    }

    @Override
    public void decorate(final World world, final Random worldRng, final Biome biome, final BlockPos chunkPos)
    {
        this.chunkPos = chunkPos;
        // todo: settings for all the rarities?

        final Random rng = new Random(world.getSeed() + ((this.chunkPos.getX() >> 7) - (this.chunkPos.getZ() >> 7)) * (this.chunkPos.getZ() >> 7));

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

            if (ClimateTFC.getBioTemperatureHeight(world, p2) >= 25) //todo: make less likely as temp goes down?
                reedGen.generate(world, rng, p2);
        }

        if (rng.nextInt(300) == 0)
        {
            pumpkinGen.generate(world, rng, world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8)));
        }

        for (int i = 0; i < cactiPerChunk; i++)
        {
            final BlockPos p2 = world.getHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));

            float temperature = ClimateTFC.getBioTemperatureHeight(world, p2);
            float rainfall = ChunkDataTFC.getRainfall(world, p2);
            if (temperature > 20 && rainfall < 125) cactusGen.generate(world, rng, p2);  //todo: make less likely as water moves out of range?
        }

        for (int i = 0; i < waterPlantsPerChunk; i++)
        {
            final BlockPos p2 = world.getPrecipitationHeight(chunkPos.add(rng.nextInt(16) + 8, 0, rng.nextInt(16) + 8));
            if (ClimateTFC.getBioTemperatureHeight(world, p2) >= 7)
                waterplantGen.generate(world, rng, p2);
        }
    }
}
