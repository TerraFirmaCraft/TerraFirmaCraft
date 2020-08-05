/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.biomes;

import java.awt.*;
import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.climate.ClimateTFC;

public class BiomeTFC extends Biome
{
    public final Color debugColor;
    private final int waterPlantsPerChunk;
    private final int lilyPadPerChunk;
    private boolean spawnBiome;

    public BiomeTFC(int debugColor, BiomeProperties properties)
    {
        this(debugColor, properties, 0, 0);
    }

    public BiomeTFC(int debugColor, BiomeProperties properties, int lilyPadPerChunk, int waterPlantsPerChunk)
    {
        super(properties);
        this.debugColor = new Color(debugColor);
        this.lilyPadPerChunk = lilyPadPerChunk;
        this.waterPlantsPerChunk = waterPlantsPerChunk;

        // throw out the first decorator, because it's missing the lilypad & plant settings
        this.decorator = createBiomeDecorator();
        this.spawnableCreatureList.clear();
        spawnBiome = false;

        // Add creatures to respawn list
        for (String input : ConfigTFC.General.WORLD.respawnableCreatures)
        {
            String[] split = input.split(" ");
            if (split.length == 4)
            {
                ResourceLocation key = new ResourceLocation(split[0]);
                int rarity;
                int min;
                int max;
                try
                {
                    rarity = Integer.parseInt(split[1]);
                    min = Integer.parseInt(split[2]);
                    max = Integer.parseInt(split[3]);
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
                EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(key);
                if (entityEntry != null)
                {
                    Class<? extends Entity> entityClass = entityEntry.getEntityClass();
                    if (EntityLiving.class.isAssignableFrom(entityClass))
                    {
                        //noinspection unchecked
                        spawnableCreatureList.add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) entityClass, rarity, min, max));
                    }
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return this.biomeName;
    }

    @Override
    @Nonnull
    public BiomeDecorator createBiomeDecorator()
    {
        return new BiomeDecoratorTFC(lilyPadPerChunk, waterPlantsPerChunk);
    }

    @Override
    public float getTemperature(@Nonnull BlockPos pos)
    {
        // Vanilla spec: 0.15 = snow threshold, range = [-1, 1] for overworld temps.
        return MathHelper.clamp(0.15f + ClimateTFC.getDailyTemp(pos) / 35, -1, 1);
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
