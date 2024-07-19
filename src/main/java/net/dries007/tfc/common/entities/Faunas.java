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
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class Faunas
{
    public static final Id<TFCCod> COD = registerFish(TFCEntities.COD);
    public static final Id<Jellyfish> JELLYFISH = registerFish(TFCEntities.JELLYFISH);
    public static final Id<TFCTropicalFish> TROPICAL_FISH = registerFish(TFCEntities.TROPICAL_FISH);
    public static final Id<TFCPufferfish> PUFFERFISH = registerFish(TFCEntities.PUFFERFISH);
    public static final Map<Fish, Id<FreshwaterFish>> FISH = Helpers.mapOfKeys(Fish.class, fish -> registerFish(TFCEntities.FRESHWATER_FISH.get(fish)));
    public static final Id<AquaticCritter> LOBSTER = registerFish(TFCEntities.LOBSTER);
    public static final Id<AquaticCritter> CRAYFISH = registerFish(TFCEntities.CRAYFISH);
    public static final Id<AquaticCritter> ISOPOD = registerFish(TFCEntities.ISOPOD);
    public static final Id<AquaticCritter> HORSESHOE_CRAB = registerFish(TFCEntities.HORSESHOE_CRAB);
    public static final Id<TFCDolphin> DOLPHIN = registerFish(TFCEntities.DOLPHIN);
    public static final Id<TFCDolphin> ORCA = registerFish(TFCEntities.ORCA);
    public static final Id<Manatee> MANATEE = registerFish(TFCEntities.MANATEE);
    public static final Id<TFCTurtle> TURTLE = registerAnimal(TFCEntities.TURTLE);
    public static final Id<Penguin> PENGUIN = registerAnimal(TFCEntities.PENGUIN);
    public static final Id<TFCFrog> FROG = registerAnimal(TFCEntities.FROG);
    public static final Id<Predator> POLAR_BEAR = registerAnimal(TFCEntities.POLAR_BEAR);
    public static final Id<Predator> GRIZZLY_BEAR = registerAnimal(TFCEntities.GRIZZLY_BEAR);
    public static final Id<Predator> BLACK_BEAR = registerAnimal(TFCEntities.BLACK_BEAR);
    public static final Id<FelinePredator> COUGAR = registerAnimal(TFCEntities.COUGAR);
    public static final Id<FelinePredator> PANTHER = registerAnimal(TFCEntities.PANTHER);
    public static final Id<FelinePredator> LION = registerAnimal(TFCEntities.LION);
    public static final Id<FelinePredator> SABERTOOTH = registerAnimal(TFCEntities.SABERTOOTH);
    public static final Id<FelinePredator> TIGER = registerAnimal(TFCEntities.TIGER);
    public static final Id<AmphibiousPredator> CROCODILE = registerAmphibiousPredator(TFCEntities.CROCODILE);
    public static final Id<PackPredator> WOLF = registerAnimal(TFCEntities.WOLF);
    public static final Id<PackPredator> HYENA = registerAnimal(TFCEntities.HYENA);
    public static final Id<PackPredator> DIREWOLF = registerAnimal(TFCEntities.DIREWOLF);
    public static final Id<TFCSquid> SQUID = registerFish(TFCEntities.SQUID);
    public static final Id<Octopoteuthis> OCTOPOTEUTHIS = registerFish(TFCEntities.OCTOPOTEUTHIS);
    public static final Id<Mammal> PIG = registerAnimal(TFCEntities.PIG);
    public static final Id<DairyAnimal> COW = registerAnimal(TFCEntities.COW);
    public static final Id<DairyAnimal> GOAT = registerAnimal(TFCEntities.GOAT);
    public static final Id<DairyAnimal> YAK = registerAnimal(TFCEntities.YAK);
    public static final Id<WoolyAnimal> ALPACA = registerAnimal(TFCEntities.ALPACA);
    public static final Id<WoolyAnimal> SHEEP = registerAnimal(TFCEntities.SHEEP);
    public static final Id<WoolyAnimal> MUSK_OX = registerAnimal(TFCEntities.MUSK_OX);
    public static final Id<OviparousAnimal> CHICKEN = registerAnimal(TFCEntities.CHICKEN);
    public static final Id<OviparousAnimal> DUCK = registerAnimal(TFCEntities.DUCK);
    public static final Id<OviparousAnimal> QUAIL = registerAnimal(TFCEntities.QUAIL);
    public static final Id<TFCRabbit> RABBIT = registerAnimal(TFCEntities.RABBIT);
    public static final Id<TFCFox> FOX = registerAnimal(TFCEntities.FOX);
    public static final Id<TFCPanda> PANDA = registerAnimal(TFCEntities.PANDA);
    public static final Id<TFCOcelot> OCELOT = registerAnimal(TFCEntities.OCELOT);
    public static final Id<RammingPrey> BOAR = registerAnimal(TFCEntities.BOAR);
    public static final Id<RammingPrey> WILDEBEEST = registerAnimal(TFCEntities.WILDEBEEST);
    public static final Id<Prey> BONGO = registerAnimal(TFCEntities.BONGO);
    public static final Id<Prey> CARIBOU = registerAnimal(TFCEntities.CARIBOU);
    public static final Id<Prey> DEER = registerAnimal(TFCEntities.DEER);
    public static final Id<Prey> GAZELLE = registerAnimal(TFCEntities.GAZELLE);
    public static final Id<RammingPrey> MOOSE = registerAnimal(TFCEntities.MOOSE);
    public static final Id<WingedPrey> GROUSE = registerAnimal(TFCEntities.GROUSE);
    public static final Id<WingedPrey> PHEASANT = registerAnimal(TFCEntities.PHEASANT);
    public static final Id<WingedPrey> TURKEY = registerAnimal(TFCEntities.TURKEY);
    public static final Id<WingedPrey> PEAFOWL = registerAnimal(TFCEntities.PEAFOWL);
    public static final Id<TFCDonkey> DONKEY = registerAnimal(TFCEntities.DONKEY);
    public static final Id<TFCMule> MULE = registerAnimal(TFCEntities.MULE);
    public static final Id<TFCHorse> HORSE = registerAnimal(TFCEntities.HORSE);

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event)
    {
        FISH.values().forEach(fish -> registerSpawnPlacement(event, fish));
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

    private static <E extends Mob> Id<E> registerAnimal(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> Id<E> registerFish(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> Id<E> registerAmphibiousPredator(IdHolder<EntityType<E>> entity)
    {
        return register(entity, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static <E extends Mob> Id<E> register(IdHolder<EntityType<E>> entity, SpawnPlacementType spawnPlacement, Heightmap.Types heightmapType)
    {
        return new Id<>(entity, Fauna.MANAGER.getReference(entity.getId()), spawnPlacement, heightmapType);
    }

    private static <E extends Mob> void registerSpawnPlacement(RegisterSpawnPlacementsEvent event, Id<E> type)
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

    public record Id<E extends Mob>(
        Supplier<EntityType<E>> entity,
        DataManager.Reference<Fauna> fauna,
        SpawnPlacementType spawnPlacementType,
        Heightmap.Types heightmapType
    ) {}
}
