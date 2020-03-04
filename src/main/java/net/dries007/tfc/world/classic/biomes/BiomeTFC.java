/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.util.ArrayList;
import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.api.types.IHuntable;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.WorldEntitySpawnerTFC;

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
        spawnBiome = false;
        modSpawnableLists.put(WorldEntitySpawnerTFC.PREDATOR, new ArrayList<>());
        modSpawnableLists.put(WorldEntitySpawnerTFC.HUNTABLE, new ArrayList<>());
        //noinspection unchecked
        ForgeRegistries.ENTITIES.getValuesCollection().stream()
            .filter(x -> IHuntable.class.isAssignableFrom(x.getEntityClass()))
            .forEach(x -> modSpawnableLists.get(WorldEntitySpawnerTFC.HUNTABLE).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) x.getEntityClass(), 12, 2, 4)));

        //noinspection unchecked
        ForgeRegistries.ENTITIES.getValuesCollection().stream()
            .filter(x -> IPredator.class.isAssignableFrom(x.getEntityClass()))
            .forEach(x -> modSpawnableLists.get(WorldEntitySpawnerTFC.PREDATOR).add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) x.getEntityClass(), 30, 2, 4)));

        modSpawnableLists.put(WorldEntitySpawnerTFC.MOB, spawnableMonsterList);
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
