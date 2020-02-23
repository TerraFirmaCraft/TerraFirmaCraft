/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;

import net.dries007.tfc.objects.entity.animal.*;
import net.dries007.tfc.util.climate.ClimateTFC;

public class BiomeTFC extends Biome
{
    private final int waterPlantsPerChunk;
    private final int lilyPadPerChunk;
    private boolean spawnBiome;

    public BiomeTFC(BiomeProperties properties)
    {
        this(properties, 0, 0);
    }

    public BiomeTFC(BiomeProperties properties, int lilyPadPerChunk, int waterPlantsPerChunk)
    {
        super(properties);
        this.lilyPadPerChunk = lilyPadPerChunk;
        this.waterPlantsPerChunk = waterPlantsPerChunk;

        // throw out the first decorator, because it's missing the lilypad & plant settings
        this.decorator = createBiomeDecorator();

        this.spawnableCreatureList.clear();
        // Register creature that respawns in any biome
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityDeerTFC.class, 14, 2, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityPheasantTFC.class, 14, 2, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbitTFC.class, 15, 3, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityWolfTFC.class, 6, 2, 3));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityBearTFC.class, 12, 1, 2));
        spawnBiome = false;
    }

    @Override
    public String toString()
    {
        return getBiomeName();
    }

    @Override
    @Nonnull
    public BiomeDecorator createBiomeDecorator()
    {
        return new BiomeDecoratorTFC(lilyPadPerChunk, waterPlantsPerChunk);
    }

    @Override
    public float getTemperature(BlockPos pos)
    {
        // Vanilla spec: 0.15 = snow threshold, range = [-1, 1] for overworld temps.
        return MathHelper.clamp(0.15f + ClimateTFC.getActualTemp(pos) / 35, -1, 1);
    }

    @Override
    public boolean ignorePlayerSpawnSuitability()
    {
        return spawnBiome;
    }

    public Biome setSpawnBiome()
    {
        spawnBiome = true;
        return this;
    }
}
