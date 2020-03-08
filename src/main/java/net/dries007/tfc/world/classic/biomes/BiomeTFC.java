/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.IHuntable;
import net.dries007.tfc.api.types.ILivestock;
import net.dries007.tfc.api.types.IPredator;
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
        spawnBiome = false;
        if (ConfigTFC.WORLD.predatorRespawnWeight > 0)
        {
            //noinspection unchecked
            ForgeRegistries.ENTITIES.getValuesCollection().stream()
                .filter(x -> IPredator.class.isAssignableFrom(x.getEntityClass()))
                .forEach(x -> spawnableCreatureList.add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) x.getEntityClass(), ConfigTFC.WORLD.predatorRespawnWeight, 1, 2)));
        }
        if (ConfigTFC.WORLD.huntableRespawnWeight > 0)
        {
            //noinspection unchecked
            ForgeRegistries.ENTITIES.getValuesCollection().stream()
                .filter(x -> IHuntable.class.isAssignableFrom(x.getEntityClass()))
                .forEach(x -> spawnableCreatureList.add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) x.getEntityClass(), ConfigTFC.WORLD.huntableRespawnWeight, 1, 2)));
        }
        if (ConfigTFC.WORLD.livestockRespawnWeight > 0)
        {
            //noinspection unchecked
            ForgeRegistries.ENTITIES.getValuesCollection().stream()
                .filter(x -> ILivestock.class.isAssignableFrom(x.getEntityClass()))
                .forEach(x -> spawnableCreatureList.add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) x.getEntityClass(), ConfigTFC.WORLD.livestockRespawnWeight, 1, 2)));
        }
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
