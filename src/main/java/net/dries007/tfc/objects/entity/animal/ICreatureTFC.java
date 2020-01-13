package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.biome.Biome;

/**
 * Interface for creature spawning mechanics
 * See {@link net.dries007.tfc.world.classic.WorldEntitySpawnerTFC}
 */
public interface ICreatureTFC
{
    /**
     * Gets the random weight (1 in N chunks) to spawn this creature
     *
     * @param biome the biome in chunk that is trying to spawn this creature
     * @return 0 if can't spawn, 1 or more for how ofter this creature spawn in said biome
     */
    int getSpawnWeight(Biome biome);

    /**
     * Returns the grouping rules (one or more) for spawn
     * Override this if you want your groups to have some form of rules applied to them
     * (ie for animals: Mother and children, one male and all female)
     *
     * @return Consumer method to apply rules to all individuals at once
     */
    default BiConsumer<List<EntityLiving>, Random> getGroupingRules()
    {
        return (creatures, random) -> {}; // Default, no special rules
    }

    /**
     * Returns the minimum group size (if not solo) this creature spawns in
     *
     * @return minimum number of individuals in one group spawn
     */
    default int getMinGroupSize()
    {
        return 1;
    }

    /**
     * Returns the maximum group size this creature spawns in
     *
     * @return maximum number of individuals in one group spawn
     */
    default int getMaxGroupSize()
    {
        return 1;
    }
}
