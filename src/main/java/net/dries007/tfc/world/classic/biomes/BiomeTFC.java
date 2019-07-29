/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import javax.annotation.Nonnull;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;

import net.dries007.tfc.objects.entity.animal.*;

public class BiomeTFC extends Biome
{
    public final int waterPlantsPerChunk;
    public final int lilyPadPerChunk;
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
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntitySheepTFC.class, 12, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityPigTFC.class, 10, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityChickenTFC.class, 10, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityCowTFC.class, 8, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityDeerTFC.class, 14, 2, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityPheasantTFC.class, 14, 2, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityBearTFC.class, 4, 1, 2));
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
        // todo: Forge event wrap this
        return new BiomeDecoratorTFC(lilyPadPerChunk, waterPlantsPerChunk);
    }

    public Biome setSpawnBiome()
    {
        spawnBiome = true;
        return this;
    }

    @Override
    public boolean ignorePlayerSpawnSuitability()
    {
        return spawnBiome;
    }
}
