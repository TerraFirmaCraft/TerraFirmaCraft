/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.common.entities.ai.prey.TFCOcelot;
import net.dries007.tfc.common.entities.aquatic.AquaticCritter;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.common.entities.aquatic.FreshwaterFish;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.entities.aquatic.Manatee;
import net.dries007.tfc.common.entities.aquatic.Octopoteuthis;
import net.dries007.tfc.common.entities.aquatic.Penguin;
import net.dries007.tfc.common.entities.aquatic.TFCCod;
import net.dries007.tfc.common.entities.aquatic.TFCDolphin;
import net.dries007.tfc.common.entities.aquatic.TFCPufferfish;
import net.dries007.tfc.common.entities.aquatic.TFCSquid;
import net.dries007.tfc.common.entities.aquatic.TFCTropicalFish;
import net.dries007.tfc.common.entities.aquatic.TFCTurtle;
import net.dries007.tfc.common.entities.livestock.DairyAnimal;
import net.dries007.tfc.common.entities.livestock.Mammal;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.common.entities.livestock.WoolyAnimal;
import net.dries007.tfc.common.entities.livestock.horse.TFCDonkey;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCMule;
import net.dries007.tfc.common.entities.predator.AmphibiousPredator;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.entities.prey.Prey;
import net.dries007.tfc.common.entities.prey.RammingPrey;
import net.dries007.tfc.common.entities.prey.TFCFox;
import net.dries007.tfc.common.entities.prey.TFCFrog;
import net.dries007.tfc.common.entities.prey.TFCPanda;
import net.dries007.tfc.common.entities.prey.TFCRabbit;
import net.dries007.tfc.common.entities.prey.WingedPrey;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class Faunas
{
    public static final FaunaType<TFCCod> COD = registerFish(TFCEntities.COD);
    public static final FaunaType<Jellyfish> JELLYFISH = registerFish(TFCEntities.JELLYFISH);
    public static final FaunaType<TFCTropicalFish> TROPICAL_FISH = registerFish(TFCEntities.TROPICAL_FISH);
    public static final FaunaType<TFCPufferfish> PUFFERFISH = registerFish(TFCEntities.PUFFERFISH);
    public static final Map<Fish, FaunaType<FreshwaterFish>> FRESHWATER_FISH = Helpers.mapOfKeys(Fish.class, fish -> registerFish(TFCEntities.FRESHWATER_FISH.get(fish)));
    public static final FaunaType<AquaticCritter> LOBSTER = registerFish(TFCEntities.LOBSTER);
    public static final FaunaType<AquaticCritter> CRAYFISH = registerFish(TFCEntities.CRAYFISH);
    public static final FaunaType<AquaticCritter> ISOPOD = registerFish(TFCEntities.ISOPOD);
    public static final FaunaType<AquaticCritter> HORSESHOE_CRAB = registerFish(TFCEntities.HORSESHOE_CRAB);
    public static final FaunaType<TFCDolphin> DOLPHIN = registerFish(TFCEntities.DOLPHIN);
    public static final FaunaType<TFCDolphin> ORCA = registerFish(TFCEntities.ORCA);
    public static final FaunaType<Manatee> MANATEE = registerFish(TFCEntities.MANATEE);
    public static final FaunaType<TFCTurtle> TURTLE = registerAnimal(TFCEntities.TURTLE);
    public static final FaunaType<Penguin> PENGUIN = registerAnimal(TFCEntities.PENGUIN);
    public static final FaunaType<TFCFrog> FROG = registerAnimal(TFCEntities.FROG);
    public static final FaunaType<Predator> POLAR_BEAR = registerAnimal(TFCEntities.POLAR_BEAR);
    public static final FaunaType<Predator> GRIZZLY_BEAR = registerAnimal(TFCEntities.GRIZZLY_BEAR);
    public static final FaunaType<Predator> BLACK_BEAR = registerAnimal(TFCEntities.BLACK_BEAR);
    public static final FaunaType<FelinePredator> COUGAR = registerAnimal(TFCEntities.COUGAR);
    public static final FaunaType<FelinePredator> PANTHER = registerAnimal(TFCEntities.PANTHER);
    public static final FaunaType<FelinePredator> LION = registerAnimal(TFCEntities.LION);
    public static final FaunaType<FelinePredator> SABERTOOTH = registerAnimal(TFCEntities.SABERTOOTH);
    public static final FaunaType<FelinePredator> TIGER = registerAnimal(TFCEntities.TIGER);
    public static final FaunaType<AmphibiousPredator> CROCODILE = registerAmphibiousPredator(TFCEntities.CROCODILE);
    public static final FaunaType<PackPredator> WOLF = registerAnimal(TFCEntities.WOLF);
    public static final FaunaType<PackPredator> HYENA = registerAnimal(TFCEntities.HYENA);
    public static final FaunaType<PackPredator> DIREWOLF = registerAnimal(TFCEntities.DIREWOLF);
    public static final FaunaType<TFCSquid> SQUID = registerFish(TFCEntities.SQUID);
    public static final FaunaType<Octopoteuthis> OCTOPOTEUTHIS = registerFish(TFCEntities.OCTOPOTEUTHIS);
    public static final FaunaType<Mammal> PIG = registerAnimal(TFCEntities.PIG);
    public static final FaunaType<DairyAnimal> COW = registerAnimal(TFCEntities.COW);
    public static final FaunaType<DairyAnimal> GOAT = registerAnimal(TFCEntities.GOAT);
    public static final FaunaType<DairyAnimal> YAK = registerAnimal(TFCEntities.YAK);
    public static final FaunaType<WoolyAnimal> ALPACA = registerAnimal(TFCEntities.ALPACA);
    public static final FaunaType<WoolyAnimal> SHEEP = registerAnimal(TFCEntities.SHEEP);
    public static final FaunaType<WoolyAnimal> MUSK_OX = registerAnimal(TFCEntities.MUSK_OX);
    public static final FaunaType<OviparousAnimal> CHICKEN = registerAnimal(TFCEntities.CHICKEN);
    public static final FaunaType<OviparousAnimal> DUCK = registerAnimal(TFCEntities.DUCK);
    public static final FaunaType<OviparousAnimal> QUAIL = registerAnimal(TFCEntities.QUAIL);
    public static final FaunaType<TFCRabbit> RABBIT = registerAnimal(TFCEntities.RABBIT);
    public static final FaunaType<TFCFox> FOX = registerAnimal(TFCEntities.FOX);
    public static final FaunaType<TFCPanda> PANDA = registerAnimal(TFCEntities.PANDA);
    public static final FaunaType<TFCOcelot> OCELOT = registerAnimal(TFCEntities.OCELOT);
    public static final FaunaType<RammingPrey> BOAR = registerAnimal(TFCEntities.BOAR);
    public static final FaunaType<RammingPrey> WILDEBEEST = registerAnimal(TFCEntities.WILDEBEEST);
    public static final FaunaType<Prey> BONGO = registerAnimal(TFCEntities.BONGO);
    public static final FaunaType<Prey> CARIBOU = registerAnimal(TFCEntities.CARIBOU);
    public static final FaunaType<Prey> DEER = registerAnimal(TFCEntities.DEER);
    public static final FaunaType<Prey> GAZELLE = registerAnimal(TFCEntities.GAZELLE);
    public static final FaunaType<RammingPrey> MOOSE = registerAnimal(TFCEntities.MOOSE);
    public static final FaunaType<WingedPrey> GROUSE = registerAnimal(TFCEntities.GROUSE);
    public static final FaunaType<WingedPrey> PHEASANT = registerAnimal(TFCEntities.PHEASANT);
    public static final FaunaType<WingedPrey> TURKEY = registerAnimal(TFCEntities.TURKEY);
    public static final FaunaType<WingedPrey> PEAFOWL = registerAnimal(TFCEntities.PEAFOWL);
    public static final FaunaType<TFCDonkey> DONKEY = registerAnimal(TFCEntities.DONKEY);
    public static final FaunaType<TFCMule> MULE = registerAnimal(TFCEntities.MULE);
    public static final FaunaType<TFCHorse> HORSE = registerAnimal(TFCEntities.HORSE);

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event)
    {
        FRESHWATER_FISH.values().forEach(fish -> registerSpawnPlacement(event, fish));
        registerSpawnPlacement(event, COD);
        registerSpawnPlacement(event, JELLYFISH);
        registerSpawnPlacement(event, TROPICAL_FISH);
        registerSpawnPlacement(event, PUFFERFISH);
        registerSpawnPlacement(event, LOBSTER);
        registerSpawnPlacement(event, CRAYFISH);
        registerSpawnPlacement(event, ISOPOD);
        registerSpawnPlacement(event, HORSESHOE_CRAB);
        registerSpawnPlacement(event, DOLPHIN);
        registerSpawnPlacement(event, ORCA);
        registerSpawnPlacement(event, MANATEE);
        registerSpawnPlacement(event, TURTLE);
        registerSpawnPlacement(event, PENGUIN);
        registerSpawnPlacement(event, FROG);
        registerSpawnPlacement(event, POLAR_BEAR);
        registerSpawnPlacement(event, GRIZZLY_BEAR);
        registerSpawnPlacement(event, BLACK_BEAR);
        registerSpawnPlacement(event, COUGAR);
        registerSpawnPlacement(event, PANTHER);
        registerSpawnPlacement(event, LION);
        registerSpawnPlacement(event, SABERTOOTH);
        registerSpawnPlacement(event, TIGER);
        registerSpawnPlacement(event, CROCODILE);
        registerSpawnPlacement(event, SQUID);
        registerSpawnPlacement(event, OCTOPOTEUTHIS);
        registerSpawnPlacement(event, PIG);
        registerSpawnPlacement(event, COW);
        registerSpawnPlacement(event, GOAT);
        registerSpawnPlacement(event, YAK);
        registerSpawnPlacement(event, ALPACA);
        registerSpawnPlacement(event, SHEEP);
        registerSpawnPlacement(event, MUSK_OX);
        registerSpawnPlacement(event, CHICKEN);
        registerSpawnPlacement(event, DUCK);
        registerSpawnPlacement(event, QUAIL);
        registerSpawnPlacement(event, RABBIT);
        registerSpawnPlacement(event, FOX);
        registerSpawnPlacement(event, PANDA);
        registerSpawnPlacement(event, OCELOT);
        registerSpawnPlacement(event, BOAR);
        registerSpawnPlacement(event, WILDEBEEST);
        registerSpawnPlacement(event, MOOSE);
        registerSpawnPlacement(event, BONGO);
        registerSpawnPlacement(event, CARIBOU);
        registerSpawnPlacement(event, DEER);
        registerSpawnPlacement(event, GAZELLE);
        registerSpawnPlacement(event, GROUSE);
        registerSpawnPlacement(event, PHEASANT);
        registerSpawnPlacement(event, TURKEY);
        registerSpawnPlacement(event, PEAFOWL);
        registerSpawnPlacement(event, DONKEY);
        registerSpawnPlacement(event, MULE);
        registerSpawnPlacement(event, HORSE);
        registerSpawnPlacement(event, WOLF);
        registerSpawnPlacement(event, HYENA);
        registerSpawnPlacement(event, DIREWOLF);
    }

    private static <E extends Mob> FaunaType<E> registerAnimal(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> FaunaType<E> registerFish(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> FaunaType<E> registerAmphibiousPredator(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> FaunaType<E> register(IdHolder<EntityType<E>> entity, SpawnPlacementType spawnPlacement, Heightmap.Types heightmapType)
    {
        final Supplier<Fauna> fauna = Fauna.MANAGER.getReference(entity.getId());
        return new FaunaType<>(entity, fauna, spawnPlacement, heightmapType);
    }

    private static <E extends Mob> void registerSpawnPlacement(RegisterSpawnPlacementsEvent event, FaunaType<E> type)
    {
        event.register(type.entity().get(), type.spawnPlacementType(), type.heightmapType(), (mob, level, heightmap, pos, rand) -> {
            final Fauna fauna = type.fauna().get();
            final ChunkGenerator generator = level.getLevel().getChunkSource().getGenerator();
            if (rand.nextInt(fauna.chance()) != 0)
            {
                return false;
            }

            if (mob instanceof AquaticMob aquaticMob && !aquaticMob.canSpawnIn(level.getFluidState(pos).getType()))
            {
                return false;
            }

            final int seaLevel = generator.getSeaLevel();
            if (fauna.distanceBelowSeaLevel() != -1 && pos.getY() > (seaLevel - fauna.distanceBelowSeaLevel()))
            {
                return false;
            }

            final ChunkData data = ChunkData.get(level, pos);
            if (!fauna.climate().isValid(data, pos, rand))
            {
                return false;
            }

            final BlockPos below = pos.below();
            if (fauna.solidGround() && !Helpers.isBlock(level.getBlockState(below), BlockTags.VALID_SPAWN))
            {
                return false;
            }
            return fauna.maxBrightness() == -1 || level.getRawBrightness(pos, 0) <= fauna.maxBrightness();
        }, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    record FaunaType<E extends Mob>(
        Supplier<EntityType<E>> entity,
        Supplier<Fauna> fauna,
        SpawnPlacementType spawnPlacementType,
        Heightmap.Types heightmapType
    ) {}
}
