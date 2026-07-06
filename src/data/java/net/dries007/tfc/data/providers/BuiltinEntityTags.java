/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Fish;

import static net.dries007.tfc.common.TFCTags.Entities.*;

public class BuiltinEntityTags extends EntityTypeTagsProvider
{
    public BuiltinEntityTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(event.getGenerator().getPackOutput(), provider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Vanilla Tags ===== //
        tag(EntityTypeTags.IMPACT_PROJECTILES)
            .add(TFCEntities.THROWN_JAVELIN.get());
        tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
            .add(TFCEntities.POLAR_BEAR.get())
            .add(TFCEntities.PENGUIN.get());
        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .addTag(WATER_AMBIENT)
            .add(
                TFCEntities.MANATEE.get(),
                TFCEntities.SQUID.get(),
                TFCEntities.OCTOPOTEUTHIS.get(),
                TFCEntities.TURTLE.get()
            );
        tag(EntityTypeTags.AQUATIC)
            .addTags(WATER_AMBIENT, WATER_CREATURES)
            .add(TFCEntities.TURTLE.get());
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
            .addTag(OVIPAROUS_ANIMALS)
            .add(
                TFCEntities.GROUSE.get(),
                TFCEntities.PHEASANT.get(),
                TFCEntities.TURKEY.get(),
                TFCEntities.PEAFOWL.get(),
                TFCEntities.CAT.get(),
                TFCEntities.OCELOT.get()
            );

        // ===== Common Tags ===== //
        final var boatsTag = tag(Tags.EntityTypes.BOATS);
        for (Wood wood : Wood.values())
        {
            boatsTag.add(TFCEntities.BOATS.get(wood).key());
            boatsTag.add(TFCEntities.CHEST_BOATS.get(wood).key());
        }

        tag(Tags.EntityTypes.MINECARTS)
            .add(TFCEntities.CHEST_MINECART.key())
            .add(TFCEntities.HOLDING_MINECART.key());

        // ===== TFC Tags ===== //

        tag(MONSTERS)
            .addTag(EntityTypeTags.UNDEAD)
            .add(
                EntityType.CREEPER,
                EntityType.SPIDER,
                EntityType.WITCH,
                EntityType.SLIME
            );

        tag(SPAWNS_ON_COLD_BLOCKS)
            .add(
                TFCEntities.LEOPARD_SEAL.get(),
                TFCEntities.PENGUIN.get(),
                TFCEntities.POLAR_BEAR.get()
            );

        tag(TURTLE_FRIENDS)
            .add(
                EntityType.PLAYER,
                TFCEntities.DOLPHIN.get()
            );

        tag(SMALL_FISH)
            .add(
                TFCEntities.COD.get(),
                TFCEntities.PUFFERFISH.get(),
                TFCEntities.TROPICAL_FISH.get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.SALMON).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.RAINBOW_TROUT).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.LAKE_TROUT).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.BLUEGILL).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.LARGEMOUTH_BASS).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.SMALLMOUTH_BASS).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.CRAPPIE).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.ARCTIC_CHAR).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.BURBOT).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.PEACOCK_BASS).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.PACU).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.RED_PIRANHA).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.NORTHERN_PIKE).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.MUKSUN).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.TILAPIA).get(),
                TFCEntities.FRESHWATER_FISH.get(Fish.SPOTTED_GUDGEON).get()
            );

        // noinspection unchecked
        tag(BUBBLE_COLUMN_IMMUNE)
            .addTags(SMALL_FISH)
            .add(
                TFCEntities.ORCA.get(),
                TFCEntities.DOLPHIN.get(),
                TFCEntities.SQUID.get(),
                TFCEntities.OCTOPOTEUTHIS.get(),
                TFCEntities.ISOPOD.get(),
                TFCEntities.LOBSTER.get(),
                TFCEntities.HORSESHOE_CRAB.get(),
                TFCEntities.JELLYFISH.get()
            );

        tag(NEEDS_LARGE_FISHING_BAIT)
            .add(
                TFCEntities.ORCA.get(),
                TFCEntities.DOLPHIN.get()
            );

        tag(WATER_AMBIENT)
            .addTag(SMALL_FISH)
            .add(TFCEntities.JELLYFISH.get())
            .add(TFCEntities.ISOPOD.get())
            .add(TFCEntities.LOBSTER.get())
            .add(TFCEntities.CRAYFISH.get())
            .add(TFCEntities.HORSESHOE_CRAB.get());

        tag(WATER_CREATURES)
            .add(TFCEntities.DOLPHIN.get())
            .add(TFCEntities.ORCA.get())
            .add(TFCEntities.MANATEE.get())
            .add(TFCEntities.SQUID.get())
            .add(TFCEntities.OCTOPOTEUTHIS.get());

        tag(AMPHIBIOUS_CREATURES)
            .add(TFCEntities.TURTLE.get())
            .add(TFCEntities.PENGUIN.get())
            .add(TFCEntities.LEOPARD_SEAL.get())
            .add(TFCEntities.FROG.get());

        tag(BEARS)
            .add(TFCEntities.POLAR_BEAR.get())
            .add(TFCEntities.GRIZZLY_BEAR.get())
            .add(TFCEntities.BLACK_BEAR.get());

        tag(BIG_CATS)
            .add(TFCEntities.COUGAR.get())
            .add(TFCEntities.PANTHER.get())
            .add(TFCEntities.LION.get())
            .add(TFCEntities.SABERTOOTH.get())
            .add(TFCEntities.TIGER.get());

        tag(OCEAN_PREDATORS)
            .add(TFCEntities.ORCA.get())
            .add(TFCEntities.DOLPHIN.get());

        tag(HUNTED_BY_OCEAN_PREDATORS)
            .addTag(SMALL_FISH)
            .add(TFCEntities.PENGUIN.get())
            .add(TFCEntities.LEOPARD_SEAL.get());

        tag(CANINE_PREDATORS)
            .add(TFCEntities.WOLF.get())
            .add(TFCEntities.HYENA.get())
            .add(TFCEntities.DIREWOLF.get());

        tag(LAND_PREDATORS)
            .addTag(BEARS)
            .addTag(BIG_CATS)
            .addTag(CANINE_PREDATORS)
            .add(TFCEntities.CROCODILE.get());

        tag(DAIRY_ANIMALS)
            .add(TFCEntities.COW.get())
            .add(TFCEntities.YAK.get())
            .add(TFCEntities.GOAT.get());

        tag(OVIPAROUS_ANIMALS)
            .add(TFCEntities.CHICKEN.get())
            .add(TFCEntities.DUCK.get())
            .add(TFCEntities.QUAIL.get());

        tag(SHEARABLE_ANIMALS)
            .add(TFCEntities.SHEEP.get())
            .add(TFCEntities.ALPACA.get())
            .add(TFCEntities.MUSK_OX.get());

        tag(RIDEABLE_ANIMALS)
            .add(TFCEntities.HORSE.get())
            .add(TFCEntities.DONKEY.get())
            .add(TFCEntities.MULE.get());

        tag(FARM_ANIMALS)
            .addTag(DAIRY_ANIMALS)
            .addTag(OVIPAROUS_ANIMALS)
            .addTag(SHEARABLE_ANIMALS)
            .addTag(RIDEABLE_ANIMALS)
            .add(TFCEntities.PIG.get())
            .add(TFCEntities.RABBIT.get())
            .add(TFCEntities.FROG.get());

        tag(PETS)
            .add(TFCEntities.CAT.get())
            .add(TFCEntities.DOG.get());

        tag(WILD_PREY_ANIMALS)
            .add(TFCEntities.FOX.get())
            .add(TFCEntities.PANDA.get())
            .add(TFCEntities.OCELOT.get())
            .add(TFCEntities.DEER.get())
            .add(TFCEntities.CARIBOU.get())
            .add(TFCEntities.BONGO.get())
            .add(TFCEntities.GAZELLE.get())
            .add(TFCEntities.GROUSE.get())
            .add(TFCEntities.PHEASANT.get())
            .add(TFCEntities.TURKEY.get())
            .add(TFCEntities.PEAFOWL.get());

        tag(ANIMALS)
            .addTag(LAND_PREDATORS)
            .addTag(WATER_AMBIENT)
            .addTag(WATER_CREATURES)
            .addTag(PESTS)
            .addTag(PETS)
            .addTag(RAMMING_ANIMALS)
            .addTag(AMPHIBIOUS_CREATURES)
            .addTag(FARM_ANIMALS)
            .addTag(WILD_PREY_ANIMALS)
            .add(TFCEntities.RABBIT.get());

        tag(RAMMING_ANIMALS)
            .add(
                TFCEntities.BOAR.get(),
                TFCEntities.BISON.get(),
                TFCEntities.MOOSE.get(),
                TFCEntities.WILDEBEEST.get()
            );

        tag(PESTS)
            .addTag(UNIVERSAL_PESTS)
            .addTag(COLD_PESTS)
            .addTag(DESERT_PESTS)
            .addTag(TROPICAL_PESTS);

        tag(COLD_PESTS).add(TFCEntities.LEMMING.get());
        tag(UNIVERSAL_PESTS).add(TFCEntities.RAT.get());
        tag(DESERT_PESTS).add(TFCEntities.JERBOA.get());
        tag(TROPICAL_PESTS).add(TFCEntities.MONGOOSE.get());

        tag(BIRD_PREY)
            .add(
                TFCEntities.TURKEY.get(),
                TFCEntities.PHEASANT.get(),
                TFCEntities.GROUSE.get(),
                TFCEntities.CHICKEN.get(),
                TFCEntities.PENGUIN.get(),
                TFCEntities.QUAIL.get(),
                TFCEntities.DUCK.get()
            );

        tag(LARGE_PREY)
            .add(
                TFCEntities.DEER.get(),
                TFCEntities.CARIBOU.get(),
                TFCEntities.BONGO.get(),
                TFCEntities.PIG.get(),
                TFCEntities.COW.get(),
                TFCEntities.SHEEP.get(),
                TFCEntities.YAK.get(),
                TFCEntities.MUSK_OX.get(),
                TFCEntities.HORSE.get(),
                TFCEntities.MULE.get(),
                TFCEntities.DONKEY.get(),
                TFCEntities.GOAT.get()
            );

        tag(HUNTED_BY_LAND_PREDATORS)
            .addTags(
                BIRD_PREY,
                LARGE_PREY
            )
            .add(
                TFCEntities.RAT.get(),
                TFCEntities.JERBOA.get(),
                TFCEntities.LEMMING.get(),
                TFCEntities.MONGOOSE.get()
            );

        tag(HUNTED_BY_CATS)
            .addTags(
                SMALL_FISH,
                BIRD_PREY
            )
            .add(
                TFCEntities.RAT.get(),
                TFCEntities.JERBOA.get(),
                TFCEntities.LEMMING.get()
            );

        tag(HUNTED_BY_DOGS)
            .add(
                TFCEntities.RAT.get(),
                TFCEntities.JERBOA.get(),
                TFCEntities.LEMMING.get(),
                TFCEntities.MONGOOSE.get()
            );

        tag(NOT_RAMMED_BY_RAMMERS)
            .addTags(BUBBLE_COLUMN_IMMUNE, PESTS, BIRD_PREY)
            .add(TFCEntities.FROG.get());

    }
}
