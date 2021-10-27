/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.RegisteredDataManager;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class Faunas
{
    public static final Supplier<Fauna> COD = register("cod");
    public static final Supplier<Fauna> JELLYFISH = register("jellyfish");
    public static final Supplier<Fauna> TROPICAL_FISH = register("tropical_fish");
    public static final Supplier<Fauna> BLUEGILL = register("bluegill");
    public static final Supplier<Fauna> PUFFERFISH = register("pufferfish");
    public static final Supplier<Fauna> SALMON = register("salmon");

    public static void registerSpawnPlacements()
    {
        SpawnPlacements.register(TFCEntities.COD.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(COD));
        SpawnPlacements.register(TFCEntities.JELLYFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(JELLYFISH));
        SpawnPlacements.register(TFCEntities.BLUEGILL.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(BLUEGILL));
        SpawnPlacements.register(TFCEntities.TROPICAL_FISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(TROPICAL_FISH));
        SpawnPlacements.register(TFCEntities.PUFFERFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(PUFFERFISH));
        SpawnPlacements.register(TFCEntities.SALMON.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new FaunaSpawnPredicate<>(SALMON));
    }

    private static RegisteredDataManager.Entry<Fauna> register(String name)
    {
        return Fauna.MANAGER.register(Helpers.identifier(name.toLowerCase(Locale.ROOT)));
    }

    record FaunaSpawnPredicate<T extends Mob>(Supplier<Fauna> faunaSupplier) implements SpawnPlacements.SpawnPredicate<T>
    {
        @Override
        public boolean test(EntityType<T> mob, ServerLevelAccessor level, MobSpawnType type, BlockPos pos, Random rand)
        {
            Fauna fauna = faunaSupplier.get();
            if (rand.nextInt(fauna.getChance()) != 0) return false;

            if (mob instanceof AquaticMob aquaticMob)
            {
                if (!aquaticMob.canSpawnIn(level.getFluidState(pos).getType()))
                {
                    return false;
                }
            }

            final int seaLevel = level.getLevel().getChunkSource().generator.getSeaLevel();
            if (fauna.getDistanceBelowSeaLevel() != -1 && pos.getY() > seaLevel - fauna.getDistanceBelowSeaLevel())
            {
                return false;
            }

            final ChunkData data = ChunkData.get(level, pos);
            if (!fauna.getClimateConfig().isValid(data, pos, rand))
            {
                return false;
            }

            final BlockPos below = pos.below();
            if (fauna.isSolidGround() && !level.getBlockState(below).is(BlockTags.VALID_SPAWN))
            {
                return false;
            }
            return true;
        }
    }
}
