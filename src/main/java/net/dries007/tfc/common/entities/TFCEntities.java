/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Locale;
import java.util.Map;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.common.entities.ai.prey.TFCOcelot;
import net.dries007.tfc.common.entities.aquatic.*;
import net.dries007.tfc.common.entities.livestock.DairyAnimal;
import net.dries007.tfc.common.entities.livestock.Mammal;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.common.entities.livestock.WoolyAnimal;
import net.dries007.tfc.common.entities.livestock.horse.TFCDonkey;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCMule;
import net.dries007.tfc.common.entities.livestock.pet.Dog;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.entities.prey.Pest;
import net.dries007.tfc.common.entities.prey.Prey;
import net.dries007.tfc.common.entities.prey.TFCFox;
import net.dries007.tfc.common.entities.prey.TFCPanda;
import net.dries007.tfc.common.entities.prey.TFCRabbit;
import net.dries007.tfc.common.entities.prey.WingedPrey;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * For reference, each living entity needs:
 * - A registered entity renderer
 * - A registered FaunaType for spawn placement
 * - A spawn egg item (and a bucket item if it's bucketable)
 * - Entity attributes, set in this class below
 * - In datagen, a json entry for fauna
 * - In datagen, an entry in biome spawners
 * - In datagen, a loot table
 *
 * When making an entity, some rules:
 * - Each synced data parameter and any variable that needs to persist should be serialized
 * - Use Brain or Goals when appropriate, and do not mix the two
 * - Avoid creating unnecessary classes. See the anonymous constructors at the bottom of this class.
 */
@SuppressWarnings("unused")
public class TFCEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    // Misc

    public static final RegistryObject<EntityType<TFCFallingBlockEntity>> FALLING_BLOCK = register("falling_block", EntityType.Builder.<TFCFallingBlockEntity>of(TFCFallingBlockEntity::new, MobCategory.MISC).sized(0.98f, 0.98f));
    public static final RegistryObject<EntityType<TFCFishingHook>> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<TFCFishingHook>of(TFCFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final RegistryObject<EntityType<GlowArrow>> GLOW_ARROW = register("glow_arrow", EntityType.Builder.<GlowArrow>of(GlowArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    public static final RegistryObject<EntityType<ThrownJavelin>> THROWN_JAVELIN = register("thrown_javelin", EntityType.Builder.<ThrownJavelin>of(ThrownJavelin::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    public static final RegistryObject<EntityType<Seat>> SEAT = register("seat", EntityType.Builder.of(Seat::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(20));
    public static final RegistryObject<EntityType<TFCMinecartChest>> CHEST_MINECART = register("chest_minecart", EntityType.Builder.of(TFCMinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<HoldingMinecart>> HOLDING_MINECART = register("holding_minecart", EntityType.Builder.<HoldingMinecart>of(HoldingMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));

    public static final Map<Wood, RegistryObject<EntityType<TFCBoat>>> BOATS = Helpers.mapOfKeys(Wood.class, wood ->
        register("boat/" + wood.name(), EntityType.Builder.<TFCBoat>of((type, level) -> new TFCBoat(type, level, TFCItems.BOATS.get(wood)), MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10))
    );

    // Water Ambient

    public static final RegistryObject<EntityType<TFCCod>> COD = register("cod", EntityType.Builder.of(TFCCod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCSalmon>> SALMON = register("salmon", EntityType.Builder.of(TFCSalmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCTropicalFish>> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.of(TFCTropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCPufferfish>> PUFFERFISH = register("pufferfish", EntityType.Builder.of(TFCPufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<Bluegill>> BLUEGILL = register("bluegill", EntityType.Builder.of(Bluegill::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<Jellyfish>> JELLYFISH = register("jellyfish", EntityType.Builder.of(Jellyfish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).clientTrackingRange(4));

    public static final RegistryObject<EntityType<AquaticCritter>> ISOPOD = register("isopod", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<AquaticCritter>> LOBSTER = register("lobster", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<AquaticCritter>> CRAYFISH = register("crayfish", EntityType.Builder.of(AquaticCritter::fresh, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<AquaticCritter>> HORSESHOE_CRAB = register("horseshoe_crab", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));

    // Water Creatures

    public static final RegistryObject<EntityType<TFCDolphin>> DOLPHIN = register("dolphin", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<TFCDolphin>> ORCA = register("orca", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(1.1F, 1.0F));
    public static final RegistryObject<EntityType<Manatee>> MANATEE = register("manatee", EntityType.Builder.of(Manatee::new, MobCategory.WATER_CREATURE).sized(1.5F, 1.2F));
    public static final RegistryObject<EntityType<TFCSquid>> SQUID = register("squid", EntityType.Builder.of(TFCSquid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<Octopoteuthis>> OCTOPOTEUTHIS = register("octopoteuthis", EntityType.Builder.of(Octopoteuthis::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));

    // Creatures
    public static final RegistryObject<EntityType<TFCTurtle>> TURTLE = register("turtle", EntityType.Builder.of(TFCTurtle::new, MobCategory.CREATURE).sized(0.8F, 0.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Penguin>> PENGUIN = register("penguin", EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.3F, 0.6F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<Predator>> POLAR_BEAR = register("polar_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Predator>> GRIZZLY_BEAR = register("grizzly_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.3F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Predator>> BLACK_BEAR = register("black_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.2F, 1.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> COUGAR = register("cougar", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> PANTHER = register("panther", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> LION = register("lion", EntityType.Builder.of(FelinePredator::createLion, MobCategory.CREATURE).sized(1.0F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> SABERTOOTH = register("sabertooth", EntityType.Builder.of(FelinePredator::createSabertooth, MobCategory.CREATURE).sized(1.1F, 1.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<PackPredator>> WOLF = register("wolf", EntityType.Builder.of(PackPredator::createWolf, MobCategory.CREATURE).sized(0.65F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<PackPredator>> DIREWOLF = register("direwolf", EntityType.Builder.of(PackPredator::createDirewolf, MobCategory.CREATURE).sized(1.0F, 1.2F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<Mammal>> PIG = register("pig", EntityType.Builder.of(TFCEntities::makePig, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DairyAnimal>> COW = register("cow", EntityType.Builder.of(TFCEntities::makeCow, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DairyAnimal>> GOAT = register("goat", EntityType.Builder.of(TFCEntities::makeGoat, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DairyAnimal>> YAK = register("yak", EntityType.Builder.of(TFCEntities::makeYak, MobCategory.CREATURE).sized(1.3F, 1.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoolyAnimal>> ALPACA = register("alpaca", EntityType.Builder.of(TFCEntities::makeAlpaca, MobCategory.CREATURE).sized(0.9F, 1.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoolyAnimal>> SHEEP = register("sheep", EntityType.Builder.of(TFCEntities::makeSheep, MobCategory.CREATURE).sized(0.9F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoolyAnimal>> MUSK_OX = register("musk_ox", EntityType.Builder.of(TFCEntities::makeMuskOx, MobCategory.CREATURE).sized(1.3F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<OviparousAnimal>> CHICKEN = register("chicken", EntityType.Builder.of(TFCEntities::makeChicken, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<OviparousAnimal>> DUCK = register("duck", EntityType.Builder.of(TFCEntities::makeDuck, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<OviparousAnimal>> QUAIL = register("quail", EntityType.Builder.of(TFCEntities::makeQuail, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<TFCRabbit>> RABBIT = register("rabbit", EntityType.Builder.of(TFCRabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<TFCFox>> FOX = register("fox", EntityType.Builder.of(TFCFox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<TFCPanda>> PANDA = register("panda", EntityType.Builder.of(TFCPanda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<TFCOcelot>> OCELOT = register("ocelot", EntityType.Builder.of(TFCOcelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Prey>> BOAR = register("boar", EntityType.Builder.of(TFCEntities::makeBoar, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Prey>> DEER = register("deer", EntityType.Builder.of(TFCEntities::makeDeer, MobCategory.CREATURE).sized(1.0F, 1.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Prey>> MOOSE = register("moose", EntityType.Builder.of(TFCEntities::makeMoose, MobCategory.CREATURE).sized(2.2F, 2.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WingedPrey>> GROUSE = register("grouse", EntityType.Builder.of(TFCEntities::makeGrouse, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WingedPrey>> PHEASANT = register("pheasant", EntityType.Builder.of(TFCEntities::makePheasant, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WingedPrey>> TURKEY = register("turkey", EntityType.Builder.of(TFCEntities::makeTurkey, MobCategory.CREATURE).sized(0.5F, 0.8F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<Pest>> RAT = register("rat", EntityType.Builder.of(TFCEntities::makeRat, MobCategory.CREATURE).sized(0.4f, 0.3f).clientTrackingRange(8));

    public static final RegistryObject<EntityType<TFCDonkey>> DONKEY = register("donkey", EntityType.Builder.of(TFCDonkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<TFCMule>> MULE = register("mule", EntityType.Builder.of(TFCMule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<TFCHorse>> HORSE = register("horse", EntityType.Builder.<TFCHorse>of(TFCHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<TFCCat>> CAT = register("cat", EntityType.Builder.of(TFCCat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<Dog>> DOG = register("dog", EntityType.Builder.of(Dog::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITIES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        });
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        event.put(COD.get(), AbstractFish.createAttributes().build());
        event.put(SALMON.get(), AbstractFish.createAttributes().build());
        event.put(TROPICAL_FISH.get(), AbstractFish.createAttributes().build());
        event.put(PUFFERFISH.get(), AbstractFish.createAttributes().build());
        event.put(BLUEGILL.get(), AbstractFish.createAttributes().build());
        event.put(JELLYFISH.get(), AbstractFish.createAttributes().build());
        event.put(LOBSTER.get(), AbstractFish.createAttributes().build());
        event.put(CRAYFISH.get(), AbstractFish.createAttributes().build());
        event.put(ISOPOD.get(), AbstractFish.createAttributes().build());
        event.put(HORSESHOE_CRAB.get(), AbstractFish.createAttributes().build());
        event.put(DOLPHIN.get(), Dolphin.createAttributes().build());
        event.put(ORCA.get(), Dolphin.createAttributes().build());
        event.put(MANATEE.get(), Manatee.createAttributes().build());
        event.put(TURTLE.get(), AmphibiousAnimal.createAttributes().build());
        event.put(PENGUIN.get(), AmphibiousAnimal.createAttributes().build());
        event.put(POLAR_BEAR.get(), Predator.createAttributes().build());
        event.put(GRIZZLY_BEAR.get(), Predator.createAttributes().build());
        event.put(BLACK_BEAR.get(), Predator.createAttributes().build());
        event.put(COUGAR.get(), FelinePredator.createAttributes().build());
        event.put(PANTHER.get(), FelinePredator.createAttributes().build());
        event.put(LION.get(), FelinePredator.createAttributes().build());
        event.put(SABERTOOTH.get(), FelinePredator.createAttributes().build());
        event.put(WOLF.get(), Predator.createAttributes().build());
        event.put(DIREWOLF.get(), Predator.createAttributes().build());
        event.put(SQUID.get(), Squid.createAttributes().build());
        event.put(OCTOPOTEUTHIS.get(), GlowSquid.createAttributes().build());
        event.put(PIG.get(), Pig.createAttributes().build());
        event.put(COW.get(), Cow.createAttributes().build());
        event.put(GOAT.get(), Pig.createAttributes().build());
        event.put(YAK.get(), Cow.createAttributes().build());
        event.put(ALPACA.get(), Cow.createAttributes().build());
        event.put(SHEEP.get(), Cow.createAttributes().build());
        event.put(MUSK_OX.get(), Cow.createAttributes().build());
        event.put(CHICKEN.get(), OviparousAnimal.createAttributes().build());
        event.put(DUCK.get(), OviparousAnimal.createAttributes().build());
        event.put(QUAIL.get(), OviparousAnimal.createAttributes().build());
        event.put(RABBIT.get(), TFCRabbit.createAttributes().build());
        event.put(FOX.get(), TFCFox.createAttributes().build());
        event.put(BOAR.get(), Prey.createAttributes().build());
        event.put(DEER.get(), Prey.createAttributes().build());
        event.put(MOOSE.get(), Prey.createLargeAttributes().build());
        event.put(GROUSE.get(), OviparousAnimal.createAttributes().build());
        event.put(PHEASANT.get(), OviparousAnimal.createAttributes().build());
        event.put(TURKEY.get(), OviparousAnimal.createAttributes().build());
        event.put(RAT.get(), Pest.createAttributes().build());
        event.put(MULE.get(), AbstractChestedHorse.createBaseChestedHorseAttributes().build());
        event.put(DONKEY.get(), AbstractChestedHorse.createBaseChestedHorseAttributes().build());
        event.put(HORSE.get(), AbstractHorse.createBaseHorseAttributes().build());
        event.put(CAT.get(), TFCCat.createAttributes().build());
        event.put(DOG.get(), Dog.createAttributes().build());
        event.put(PANDA.get(), TFCPanda.createAttributes().build());
        event.put(OCELOT.get(), TFCOcelot.createAttributes().build());
    }

    public static Mammal makePig(EntityType<? extends Mammal> animal, Level level)
    {
        return new Mammal(animal, level, TFCSounds.PIG, TFCConfig.SERVER.pigConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.PIG_FOOD;
            }
        };
    }

    public static DairyAnimal makeCow(EntityType<? extends DairyAnimal> animal, Level level)
    {
        return new DairyAnimal(animal, level, TFCSounds.COW, TFCConfig.SERVER.cowConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.COW_FOOD;
            }
        };
    }

    public static DairyAnimal makeGoat(EntityType<? extends DairyAnimal> animal, Level level)
    {
        return new DairyAnimal(animal, level, TFCSounds.GOAT, TFCConfig.SERVER.goatConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.GOAT_FOOD;
            }
        };
    }

    public static DairyAnimal makeYak(EntityType<? extends DairyAnimal> animal, Level level)
    {
        return new DairyAnimal(animal, level, TFCSounds.YAK, TFCConfig.SERVER.yakConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.YAK_FOOD;
            }
        };
    }

    // TODO: alpacas probably should have attack sounds, and be able to attack (spit)
    public static WoolyAnimal makeAlpaca(EntityType<? extends WoolyAnimal> animal, Level level)
    {
        return new WoolyAnimal(animal, level, TFCSounds.ALPACA, TFCConfig.SERVER.alpacaConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.ALPACA_FOOD;
            }
        };
    }

    public static WoolyAnimal makeSheep(EntityType<? extends WoolyAnimal> animal, Level level)
    {
        return new WoolyAnimal(animal, level, TFCSounds.SHEEP, TFCConfig.SERVER.sheepConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.SHEEP_FOOD;
            }
        };
    }

    public static WoolyAnimal makeMuskOx(EntityType<? extends WoolyAnimal> animal, Level level)
    {
        return new WoolyAnimal(animal, level, TFCSounds.MUSK_OX, TFCConfig.SERVER.muskOxConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.MUSK_OX_FOOD;
            }
        };
    }

    public static OviparousAnimal makeChicken(EntityType<? extends OviparousAnimal> animal, Level level)
    {
        return new OviparousAnimal(animal, level, TFCSounds.CHICKEN, TFCConfig.SERVER.chickenConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.CHICKEN_FOOD;
            }
        };
    }

    public static OviparousAnimal makeDuck(EntityType<? extends OviparousAnimal> animal, Level level)
    {
        return new OviparousAnimal(animal, level, TFCSounds.DUCK, TFCConfig.SERVER.duckConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.DUCK_FOOD;
            }
        };
    }

    public static OviparousAnimal makeQuail(EntityType<? extends OviparousAnimal> animal, Level level)
    {
        return new OviparousAnimal(animal, level, TFCSounds.QUAIL, TFCConfig.SERVER.quailConfig)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.QUAIL_FOOD;
            }
        };
    }

    public static Prey makeBoar(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.PIG);
    }

    public static Prey makeDeer(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.DEER);
    }

    public static Prey makeMoose(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.MOOSE);
    }

    public static WingedPrey makePheasant(EntityType<? extends WingedPrey> animal, Level level)
    {
        return new WingedPrey(animal, level, TFCSounds.PHEASANT);
    }

    public static WingedPrey makeGrouse(EntityType<? extends WingedPrey> animal, Level level)
    {
        return new WingedPrey(animal, level, TFCSounds.GROUSE);
    }

    public static WingedPrey makeTurkey(EntityType<? extends WingedPrey> animal, Level level)
    {
        return new WingedPrey(animal, level, TFCSounds.TURKEY);
    }

    public static Pest makeRat(EntityType<? extends Pest> animal, Level level)
    {
        return new Pest(animal, level, TFCSounds.RAT);
    }

}