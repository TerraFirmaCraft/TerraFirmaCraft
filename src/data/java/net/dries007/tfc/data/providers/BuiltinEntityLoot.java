/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.HideItemType.Size;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.loot.AnimalYieldProvider;
import net.dries007.tfc.util.loot.IsMaleCondition;
import net.dries007.tfc.util.loot.NotPredatedCondition;

import static net.minecraft.world.level.storage.loot.LootPool.*;
import static net.minecraft.world.level.storage.loot.LootTable.*;
import static net.minecraft.world.level.storage.loot.entries.LootItem.*;

public class BuiltinEntityLoot extends EntityLootSubProvider
{
    public BuiltinEntityLoot(HolderLookup.Provider registries)
    {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes()
    {
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> {
            return BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(TerraFirmaCraft.MOD_ID) || type == EntityType.ZOMBIE || type == EntityType.DROWNED;
        });
    }

    @Override
    public void generate()
    {
        // Vanilla overrides
        add(EntityType.ZOMBIE, lootTable()
            .withPool(lootPool().add(lootTableItem(Items.ROTTEN_FLESH).apply(setCount(0, 2))))
        );
        add(EntityType.DROWNED, lootTable()
            .withPool(lootPool().add(lootTableItem(Items.ROTTEN_FLESH).apply(setCount(0, 2))))
        );

        // Fish - killed by player
        add(TFCEntities.COD.get(), killedByPlayerFood(Food.COD));
        add(TFCEntities.TROPICAL_FISH.get(), killedByPlayerFood(Food.TROPICAL_FISH));
        add(TFCEntities.PUFFERFISH.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.PUFFERFISH)).when(LootItemKilledByPlayerCondition.killedByPlayer()))
        );

        // Freshwater fish
        for (Fish fish : Fish.values())
        {
            Food food = switch (fish)
            {
                case BLUEGILL -> Food.BLUEGILL;
                case CRAPPIE -> Food.CRAPPIE;
                case LAKE_TROUT -> Food.LAKE_TROUT;
                case LARGEMOUTH_BASS -> Food.LARGEMOUTH_BASS;
                case RAINBOW_TROUT -> Food.RAINBOW_TROUT;
                case SALMON -> Food.SALMON;
                case SMALLMOUTH_BASS -> Food.SMALLMOUTH_BASS;
                case NORTHERN_PIKE -> Food.NORTHERN_PIKE;
                case BURBOT -> Food.BURBOT;
                case ARCTIC_CHAR -> Food.ARCTIC_CHAR;
                case MUKSUN -> Food.MUKSUN;
                case TILAPIA -> Food.TILAPIA;
                case SPOTTED_GUDGEON -> Food.SPOTTED_GUDGEON;
                case PEACOCK_BASS -> Food.PEACOCK_BASS;
                case PACU -> Food.PACU;
                case RED_PIRANHA -> Food.RED_PIRANHA;
            };
            add(TFCEntities.FRESHWATER_FISH.get(fish).get(), killedByPlayerFood(food));
        }

        // Squid - ink sac + calamari
        add(TFCEntities.SQUID.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.INK_SAC).apply(setCount(1, 10))).when(LootItemKilledByPlayerCondition.killedByPlayer()))
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.CALAMARI))).when(LootItemKilledByPlayerCondition.killedByPlayer()))
        );
        add(TFCEntities.OCTOPOTEUTHIS.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.GLOW_INK_SAC).apply(setCount(1, 10))).when(LootItemKilledByPlayerCondition.killedByPlayer()))
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.CALAMARI))).when(LootItemKilledByPlayerCondition.killedByPlayer()))
        );

        // Shellfish critters - killed by player
        add(TFCEntities.ISOPOD.get(), killedByPlayerFood(Food.SHELLFISH));
        add(TFCEntities.LOBSTER.get(), killedByPlayerFood(Food.SHELLFISH));
        add(TFCEntities.HORSESHOE_CRAB.get(), killedByPlayerFood(Food.SHELLFISH));
        add(TFCEntities.CRAYFISH.get(), killedByPlayerFood(Food.SHELLFISH));

        // Marine mammals - blubber + bones
        add(TFCEntities.ORCA.get(), blubberMammal(2, 7, 5));
        add(TFCEntities.DOLPHIN.get(), blubberMammal(2, 7, 5));
        add(TFCEntities.MANATEE.get(), blubberMammal(2, 7, 5));

        // Penguin - feather, small hide (50% chance), bones
        add(TFCEntities.PENGUIN.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.FEATHER).apply(setCount(1, 3))))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(Size.SMALL))).when(LootItemRandomChanceCondition.randomChance(0.5f)))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, 2))))
        );

        // Leopard seal - blubber, medium hide, bones
        add(TFCEntities.LEOPARD_SEAL.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.BLUBBER).apply(setCount(3, 6))))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(Size.MEDIUM))))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, 4))))
        );

        // Turtle - scute + turtle meat
        add(TFCEntities.TURTLE.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.TURTLE_SCUTE)))
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.TURTLE))))
        );

        // Armadillo - scute + armadillo meat
        add(TFCEntities.ARMADILLO.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(Items.ARMADILLO_SCUTE)))
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.ARMADILLO))).when(NotPredatedCondition.notPredated())));

        // Predators - hide + bones
        add(TFCEntities.POLAR_BEAR.get(), predator(Size.LARGE, 6));
        add(TFCEntities.GRIZZLY_BEAR.get(), predator(Size.LARGE, 6));
        add(TFCEntities.BLACK_BEAR.get(), predator(Size.LARGE, 6));
        add(TFCEntities.COUGAR.get(), predator(Size.LARGE, 6));
        add(TFCEntities.PANTHER.get(), predator(Size.LARGE, 6));
        add(TFCEntities.LION.get(), predator(Size.LARGE, 6));
        add(TFCEntities.SABERTOOTH.get(), predator(Size.LARGE, 8));
        add(TFCEntities.TIGER.get(), predator(Size.LARGE, 7));
        add(TFCEntities.CROCODILE.get(), predator(Size.LARGE, 7));
        add(TFCEntities.WOLF.get(), predator(Size.SMALL, 3));
        add(TFCEntities.HYENA.get(), predator(Size.SMALL, 3));
        add(TFCEntities.DIREWOLF.get(), predator(Size.MEDIUM, 4));
        add(TFCEntities.DOG.get(), predator(Size.SMALL, 3));
        add(TFCEntities.CAT.get(), predator(Size.SMALL, 3));

        // Livestock with hide
        add(TFCEntities.PIG.get(), livestock(Food.PORK, 4, 9, 15, Size.MEDIUM, 3));
        add(TFCEntities.COW.get(), livestock(Food.BEEF, 6, 17, 23, Size.LARGE, 4));
        add(TFCEntities.YAK.get(), livestock(Food.CHEVON, 8, 13, 19, Size.LARGE, 4));
        add(TFCEntities.DROMEDARY_CAMEL.get(), livestock(Food.CAMELIDAE, 8, 13, 19, Size.LARGE, 4));

        // Goat - special case with horn
        add(TFCEntities.GOAT.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.CHEVON)).apply(animalYield(4, 7, 13))))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(Size.MEDIUM))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, 4))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(TFCItems.GOAT_HORN)).when(IsMaleCondition.isMale()))
        );

        // Livestock with sheepskin
        add(TFCEntities.ALPACA.get(), livestockWool(Food.CAMELIDAE, 6, 10, 16, Size.MEDIUM, 4));
        add(TFCEntities.SHEEP.get(), livestockWool(Food.MUTTON, 4, 12, 18, Size.SMALL, 4));
        add(TFCEntities.MUSK_OX.get(), livestockWool(Food.MUTTON, 6, 13, 19, Size.LARGE, 4));
        add(TFCEntities.BACTRIAN_CAMEL.get(), livestockWool(Food.CAMELIDAE, 6, 13, 19, Size.LARGE, 4));

        // Poultry
        add(TFCEntities.CHICKEN.get(), poultry(Food.CHICKEN, 2, 1, 6, 4, 12));
        add(TFCEntities.DUCK.get(), poultry(Food.DUCK, 2, 1, 6, 4, 10));
        add(TFCEntities.QUAIL.get(), poultry(Food.QUAIL, 1, 1, 6, 4, 12));

        // Rabbit - special case
        add(TFCEntities.RABBIT.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.RABBIT))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(Size.SMALL))).when(LootItemRandomChanceCondition.randomChance(0.5f)))
            .withPool(lootPool().add(lootTableItem(Items.BONE)).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.RABBIT_FOOT)).when(LootItemRandomChanceCondition.randomChance(0.1f)))
        );

        // Fox
        add(TFCEntities.FOX.get(), predator(Size.SMALL, 1));

        // Wild prey with hide chance
        add(TFCEntities.BOAR.get(), wildPreyChance(Food.PORK, 5, 10, Size.SMALL, 0.8f, 3));
        add(TFCEntities.WILDEBEEST.get(), wildPreyChance(Food.VENISON, 8, 14, Size.SMALL, 0.8f, 3));

        // Wild prey with guaranteed hide
        add(TFCEntities.BISON.get(), wildPrey(Food.BISON, 12, 20, Size.LARGE, 10));
        add(TFCEntities.BONGO.get(), wildPrey(Food.VENISON, 6, 10, Size.MEDIUM, 6));
        add(TFCEntities.GAZELLE.get(), wildPrey(Food.VENISON, 3, 8, Size.MEDIUM, 6));
        add(TFCEntities.DEER.get(), wildPrey(Food.VENISON, 4, 10, Size.MEDIUM, 6));
        add(TFCEntities.CARIBOU.get(), wildPrey(Food.VENISON, 6, 11, Size.MEDIUM, 6));
        add(TFCEntities.MOOSE.get(), wildPrey(Food.VENISON, 10, 20, Size.LARGE, 10));

        // Wild birds
        add(TFCEntities.GROUSE.get(), wildBird(Food.GROUSE, 2, 3, 2, 4, 10));
        add(TFCEntities.PHEASANT.get(), wildBird(Food.PHEASANT, 2, 3, 2, 4, 10));
        add(TFCEntities.TURKEY.get(), wildBird(Food.TURKEY, 2, 4, 2, 6, 10));
        add(TFCEntities.PEAFOWL.get(), wildBird(Food.PEAFOWL, 2, 4, 2, 8, 14));

        // Equines
        add(TFCEntities.DONKEY.get(), livestock(Food.HORSE_MEAT, 4, 15, 21, Size.MEDIUM, 6));
        add(TFCEntities.MULE.get(), livestock(Food.HORSE_MEAT, 4, 15, 21, Size.MEDIUM, 6));
        add(TFCEntities.HORSE.get(), livestock(Food.HORSE_MEAT, 4, 15, 21, Size.MEDIUM, 6));

        // Frog
        add(TFCEntities.FROG.get(), lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(Food.FROG_LEGS)).apply(setCount(2, 2))))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, 2))))
        );

        // Empty loot tables
        add(TFCEntities.JELLYFISH.get(), lootTable());
        add(TFCEntities.PANDA.get(), lootTable());
        add(TFCEntities.OCELOT.get(), lootTable());
        add(TFCEntities.RAT.get(), lootTable());
        add(TFCEntities.LEMMING.get(), lootTable());
        add(TFCEntities.MONGOOSE.get(), lootTable());
        add(TFCEntities.JERBOA.get(), lootTable());
    }

    // ===== Helper Methods =====

    private static LootTable.Builder killedByPlayerFood(Food food)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food))).when(LootItemKilledByPlayerCondition.killedByPlayer()));
    }

    private static LootTable.Builder blubberMammal(int minBlubber, int maxBlubber, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.BLUBBER).apply(setCount(minBlubber, maxBlubber))))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))));
    }

    private static LootTable.Builder predator(Size size, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(size))))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))));
    }

    private static LootTable.Builder livestock(Food food, int min, int maxLo, int maxHi, Size size, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(animalYield(min, maxLo, maxHi))))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(size))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))).when(NotPredatedCondition.notPredated()));
    }

    private static LootTable.Builder livestockWool(Food food, int min, int maxLo, int maxHi, Size size, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(animalYield(min, maxLo, maxHi))))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.SHEEPSKIN).get(size))).when(NotPredatedCondition.notPredated()));
    }

    private static LootTable.Builder poultry(Food food, int min, int maxLo, int maxHi, int minFeathers, int maxFeathers)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(animalYield(min, maxLo, maxHi))))
            .withPool(lootPool().add(lootTableItem(Items.FEATHER).apply(setCount(minFeathers, maxFeathers))).when(NotPredatedCondition.notPredated()));
    }

    private static LootTable.Builder wildPrey(Food food, int minFood, int maxFood, Size size, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(setCount(minFood, maxFood))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(size))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))).when(NotPredatedCondition.notPredated()));
    }

    private static LootTable.Builder wildPreyChance(Food food, int minFood, int maxFood, Size size, float hideChance, int bones)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(setCount(minFood, maxFood))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(TFCItems.HIDES.get(HideItemType.RAW).get(size))).when(LootItemRandomChanceCondition.randomChance(hideChance)))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))).when(NotPredatedCondition.notPredated()));
    }

    private static LootTable.Builder wildBird(Food food, int minFood, int maxFood, int bones, int minFeathers, int maxFeathers)
    {
        return lootTable()
            .withPool(lootPool().add(lootTableItem(TFCItems.FOOD.get(food)).apply(setCount(minFood, maxFood))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.BONE).apply(setCount(1, bones))).when(NotPredatedCondition.notPredated()))
            .withPool(lootPool().add(lootTableItem(Items.FEATHER).apply(setCount(minFeathers, maxFeathers))).when(NotPredatedCondition.notPredated()));
    }

    private static SetItemCountFunction.Builder<?> setCount(int min, int max)
    {
        return SetItemCountFunction.setCount(UniformGenerator.between(min, max));
    }

    private static SetItemCountFunction.Builder<?> animalYield(int min, int maxLo, int maxHi)
    {
        return SetItemCountFunction.setCount(new AnimalYieldProvider(
            ConstantValue.exactly(min),
            UniformGenerator.between(maxLo, maxHi)
        ));
    }
}
