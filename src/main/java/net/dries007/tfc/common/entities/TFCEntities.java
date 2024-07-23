/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.common.entities.ai.prey.TFCOcelot;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;
import net.dries007.tfc.common.entities.aquatic.AquaticCritter;
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
import net.dries007.tfc.common.entities.livestock.pet.Dog;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;
import net.dries007.tfc.common.entities.misc.GlowArrow;
import net.dries007.tfc.common.entities.misc.HoldingMinecart;
import net.dries007.tfc.common.entities.misc.Seat;
import net.dries007.tfc.common.entities.misc.TFCBoat;
import net.dries007.tfc.common.entities.misc.TFCChestBoat;
import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.entities.misc.TFCFishingHook;
import net.dries007.tfc.common.entities.misc.TFCMinecartChest;
import net.dries007.tfc.common.entities.misc.ThrownJavelin;
import net.dries007.tfc.common.entities.predator.AmphibiousPredator;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.entities.prey.Pest;
import net.dries007.tfc.common.entities.prey.Prey;
import net.dries007.tfc.common.entities.prey.RammingPrey;
import net.dries007.tfc.common.entities.prey.TFCFox;
import net.dries007.tfc.common.entities.prey.TFCFrog;
import net.dries007.tfc.common.entities.prey.TFCPanda;
import net.dries007.tfc.common.entities.prey.TFCRabbit;
import net.dries007.tfc.common.entities.prey.WingedPrey;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Each living entity needs:
 * <ul>
 *     <li>A registered entity renderer</li>
 *     <li>A registered FaunaType for spawn placement, as well as a JSON entry for the fauna</li>
 *     <li>A spawn egg item (and a bucket item if it's bucketable)</li>
 *     <li>Entity attributes, set in this class below</li>
 *     <li>An entry in the biome JSON's {@code spawners} field</li>
 *     <li>A loot table (JSON)</li>
 * </ul>
 * When making an entity, some rules:
 * <ul>
 *     <li>Each synced data parameter and any variable that needs to persist should be serialized</li>
 *     <li>Use Brain or Goals when appropriate, and do not mix the two</li>
 *     <li>Avoid creating unnecessary classes. See the anonymous constructors at the bottom of this class.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class TFCEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    // Misc

    public static final Id<TFCFallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<TFCFallingBlockEntity>of(TFCFallingBlockEntity::new, MobCategory.MISC).sized(0.98f, 0.98f));
    public static final Id<TFCFishingHook> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<TFCFishingHook>of(TFCFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final Id<GlowArrow> GLOW_ARROW = register("glow_arrow", EntityType.Builder.<GlowArrow>of(GlowArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    public static final Id<ThrownJavelin> THROWN_JAVELIN = register("thrown_javelin", EntityType.Builder.<ThrownJavelin>of(ThrownJavelin::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    public static final Id<Seat> SEAT = register("seat", EntityType.Builder.of(Seat::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(20));
    public static final Id<TFCMinecartChest> CHEST_MINECART = register("chest_minecart", EntityType.Builder.of(TFCMinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
    public static final Id<HoldingMinecart> HOLDING_MINECART = register("holding_minecart", EntityType.Builder.<HoldingMinecart>of(HoldingMinecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));

    public static final Map<Wood, Id<TFCChestBoat>> CHEST_BOATS = Helpers.mapOf(Wood.class, wood ->
        register("chest_boat/" + wood.name(), EntityType.Builder.<TFCChestBoat>of((type, level) -> new TFCChestBoat(type, level, TFCItems.BOATS.get(wood)), MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10))
    );
    public static final Map<Wood, Id<TFCBoat>> BOATS = Helpers.mapOf(Wood.class, wood ->
        register("boat/" + wood.name(), EntityType.Builder.<TFCBoat>of((type, level) -> new TFCBoat(type, level, CHEST_BOATS.get(wood), TFCItems.BOATS.get(wood)), MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10))
    );


    // Water Ambient

    public static final Map<Fish, Id<FreshwaterFish>> FRESHWATER_FISH = Helpers.mapOf(Fish.class, fish -> register(fish.getSerializedName(), EntityType.Builder.<FreshwaterFish>of((type, level) -> new FreshwaterFish(type, level, TFCSounds.FRESHWATER_FISHES.get(fish), TFCItems.FRESHWATER_FISH_BUCKETS.get(fish)), MobCategory.WATER_AMBIENT).sized(fish.getWidth(), fish.getHeight()).clientTrackingRange(4)));

    public static final Id<TFCCod> COD = register("cod", EntityType.Builder.of(TFCCod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.26F).clientTrackingRange(4));
    public static final Id<TFCTropicalFish> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.of(TFCTropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4));
    public static final Id<TFCPufferfish> PUFFERFISH = register("pufferfish", EntityType.Builder.of(TFCPufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).eyeHeight(0.455F).clientTrackingRange(4));
    public static final Id<Jellyfish> JELLYFISH = register("jellyfish", EntityType.Builder.of(Jellyfish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).eyeHeight(0.26F).clientTrackingRange(4));

    public static final Id<AquaticCritter> ISOPOD = register("isopod", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.26F).clientTrackingRange(4));
    public static final Id<AquaticCritter> LOBSTER = register("lobster", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.26F).clientTrackingRange(4));
    public static final Id<AquaticCritter> CRAYFISH = register("crayfish", EntityType.Builder.of(AquaticCritter::fresh, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.26F).clientTrackingRange(4));
    public static final Id<AquaticCritter> HORSESHOE_CRAB = register("horseshoe_crab", EntityType.Builder.of(AquaticCritter::salty, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.26F).clientTrackingRange(4));

    // Water Creatures

    public static final Id<TFCDolphin> DOLPHIN = register("dolphin", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).eyeHeight(0.3F));
    public static final Id<TFCDolphin> ORCA = register("orca", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(1.1F, 1.0F).eyeHeight(0.3F));
    public static final Id<Manatee> MANATEE = register("manatee", EntityType.Builder.of(Manatee::new, MobCategory.WATER_CREATURE).sized(1.5F, 1.2F).eyeHeight(0.3F));
    public static final Id<TFCSquid> SQUID = register("squid", EntityType.Builder.of(TFCSquid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(8));
    public static final Id<Octopoteuthis> OCTOPOTEUTHIS = register("octopoteuthis", EntityType.Builder.of(Octopoteuthis::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(8));

    // Creatures
    public static final Id<TFCTurtle> TURTLE = register("turtle", EntityType.Builder.of(TFCTurtle::new, MobCategory.CREATURE).sized(0.8F, 0.3F).clientTrackingRange(10));
    public static final Id<Penguin> PENGUIN = register("penguin", EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.3F, 0.6F).clientTrackingRange(10));
    public static final Id<TFCFrog> FROG = register("frog", EntityType.Builder.of(TFCFrog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(10));

    public static final Id<Predator> POLAR_BEAR = register("polar_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.6F).clientTrackingRange(10));
    public static final Id<Predator> GRIZZLY_BEAR = register("grizzly_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.3F, 1.4F).clientTrackingRange(10));
    public static final Id<Predator> BLACK_BEAR = register("black_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.2F, 1.3F).clientTrackingRange(10));
    public static final Id<FelinePredator> COUGAR = register("cougar", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final Id<FelinePredator> PANTHER = register("panther", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final Id<FelinePredator> LION = register("lion", EntityType.Builder.of(FelinePredator::createLion, MobCategory.CREATURE).sized(1.0F, 1.2F).clientTrackingRange(10));
    public static final Id<FelinePredator> SABERTOOTH = register("sabertooth", EntityType.Builder.of(FelinePredator::createSabertooth, MobCategory.CREATURE).sized(1.1F, 1.3F).clientTrackingRange(10));
    public static final Id<FelinePredator> TIGER = register("tiger", EntityType.Builder.of(FelinePredator::createTiger, MobCategory.CREATURE).sized(1.1F, 1.3F).clientTrackingRange(10));
    public static final Id<AmphibiousPredator> CROCODILE = register("crocodile", EntityType.Builder.of(AmphibiousPredator::createCrocodile, MobCategory.CREATURE).sized(1.8F, 0.8F).clientTrackingRange(10));
    public static final Id<PackPredator> WOLF = register("wolf", EntityType.Builder.of(PackPredator::createWolf, MobCategory.CREATURE).sized(0.65F, 0.9F).eyeHeight(0.68F).clientTrackingRange(10));
    public static final Id<PackPredator> HYENA = register("hyena", EntityType.Builder.of(PackPredator::createHyena, MobCategory.CREATURE).sized(0.65F, 0.9F).clientTrackingRange(10));
    public static final Id<PackPredator> DIREWOLF = register("direwolf", EntityType.Builder.of(PackPredator::createDirewolf, MobCategory.CREATURE).sized(1.0F, 1.2F).clientTrackingRange(10));

    public static final Id<Mammal> PIG = register("pig", EntityType.Builder.of(TFCEntities::makePig, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
    public static final Id<DairyAnimal> COW = register("cow", EntityType.Builder.of(TFCEntities::makeCow, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).clientTrackingRange(10));
    public static final Id<DairyAnimal> GOAT = register("goat", EntityType.Builder.of(TFCEntities::makeGoat, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
    public static final Id<DairyAnimal> YAK = register("yak", EntityType.Builder.of(TFCEntities::makeYak, MobCategory.CREATURE).sized(1.3F, 1.7F).clientTrackingRange(10));
    public static final Id<WoolyAnimal> ALPACA = register("alpaca", EntityType.Builder.of(TFCEntities::makeAlpaca, MobCategory.CREATURE).sized(0.9F, 1.9F).clientTrackingRange(10));
    public static final Id<WoolyAnimal> SHEEP = register("sheep", EntityType.Builder.of(TFCEntities::makeSheep, MobCategory.CREATURE).sized(0.9F, 1.2F).clientTrackingRange(10));
    public static final Id<WoolyAnimal> MUSK_OX = register("musk_ox", EntityType.Builder.of(TFCEntities::makeMuskOx, MobCategory.CREATURE).sized(1.3F, 1.5F).clientTrackingRange(10));
    public static final Id<OviparousAnimal> CHICKEN = register("chicken", EntityType.Builder.of(TFCEntities::makeChicken, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).clientTrackingRange(10));
    public static final Id<OviparousAnimal> DUCK = register("duck", EntityType.Builder.of(TFCEntities::makeDuck, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).clientTrackingRange(10));
    public static final Id<OviparousAnimal> QUAIL = register("quail", EntityType.Builder.of(TFCEntities::makeQuail, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).clientTrackingRange(10));

    public static final Id<TFCRabbit> RABBIT = register("rabbit", EntityType.Builder.of(TFCEntities::makeRabbit, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
    public static final Id<TFCFox> FOX = register("fox", EntityType.Builder.of(TFCFox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.4F).clientTrackingRange(8));
    public static final Id<TFCPanda> PANDA = register("panda", EntityType.Builder.of(TFCPanda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
    public static final Id<TFCOcelot> OCELOT = register("ocelot", EntityType.Builder.of(TFCOcelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
    public static final Id<Prey> DEER = register("deer", EntityType.Builder.of(TFCEntities::makeDeer, MobCategory.CREATURE).sized(1.0F, 1.3F).clientTrackingRange(10));
    public static final Id<Prey> CARIBOU = register("caribou", EntityType.Builder.of(TFCEntities::makeCaribou, MobCategory.CREATURE).sized(1.0F, 1.3F).clientTrackingRange(10));
    public static final Id<Prey> BONGO = register("bongo", EntityType.Builder.of(TFCEntities::makeBongo, MobCategory.CREATURE).sized(1.0F, 1.3F).clientTrackingRange(10));
    public static final Id<Prey> GAZELLE = register("gazelle", EntityType.Builder.of(TFCEntities::makeGazelle, MobCategory.CREATURE).sized(1.0F, 1.3F).clientTrackingRange(10));
    public static final Id<WingedPrey> GROUSE = register("grouse", EntityType.Builder.of(TFCEntities::makeGrouse, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
    public static final Id<WingedPrey> PHEASANT = register("pheasant", EntityType.Builder.of(TFCEntities::makePheasant, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).clientTrackingRange(10));
    public static final Id<WingedPrey> TURKEY = register("turkey", EntityType.Builder.of(TFCEntities::makeTurkey, MobCategory.CREATURE).sized(0.5F, 0.8F).eyeHeight(0.644F).clientTrackingRange(10));
    public static final Id<WingedPrey> PEAFOWL = register("peafowl", EntityType.Builder.of(TFCEntities::makePeafowl, MobCategory.CREATURE).sized(0.5F, 0.8F).eyeHeight(0.644F).clientTrackingRange(10));

    public static final Id<RammingPrey> BOAR = register("boar", EntityType.Builder.of(TFCEntities::makeBoar, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
    public static final Id<RammingPrey> MOOSE = register("moose", EntityType.Builder.of(TFCEntities::makeMoose, MobCategory.CREATURE).sized(1.8F, 2.2F).clientTrackingRange(10));
    public static final Id<RammingPrey> WILDEBEEST = register("wildebeest", EntityType.Builder.of(TFCEntities::makeWildebeest, MobCategory.CREATURE).sized(1.0F, 1.4F).clientTrackingRange(10));

    public static final Id<Pest> RAT = register("rat", EntityType.Builder.of(TFCEntities::makeRat, MobCategory.CREATURE).sized(0.4f, 0.3f).eyeHeight(0.13F).clientTrackingRange(8));

    public static final Id<TFCDonkey> DONKEY = register("donkey", EntityType.Builder.of(TFCDonkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).eyeHeight(1.425F).passengerAttachments(1.1125F).clientTrackingRange(10));
    public static final Id<TFCMule> MULE = register("mule", EntityType.Builder.of(TFCMule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.2125F).clientTrackingRange(8));
    public static final Id<TFCHorse> HORSE = register("horse", EntityType.Builder.<TFCHorse>of(TFCHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.44375F).clientTrackingRange(10));

    public static final Id<TFCCat> CAT = register("cat", EntityType.Builder.of(TFCCat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.35F).clientTrackingRange(8));
    public static final Id<Dog> DOG = register("dog", EntityType.Builder.of(Dog::new, MobCategory.CREATURE).sized(0.6F, 0.85F).eyeHeight(0.68F).clientTrackingRange(10));

    public static <E extends Entity> Id<E> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> Id<E> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return new Id<>(ENTITIES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        }));
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        FRESHWATER_FISH.values().forEach(reg -> event.put(reg.get(), AbstractFish.createAttributes().build()));
        event.put(COD.get(), AbstractFish.createAttributes().build());
        event.put(TROPICAL_FISH.get(), AbstractFish.createAttributes().build());
        event.put(PUFFERFISH.get(), AbstractFish.createAttributes().build());
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
        event.put(FROG.get(), TFCFrog.createAttributes().build());
        event.put(POLAR_BEAR.get(), Predator.createBearAttributes().build());
        event.put(GRIZZLY_BEAR.get(), Predator.createBearAttributes().build());
        event.put(BLACK_BEAR.get(), Predator.createBearAttributes().build());
        event.put(COUGAR.get(), FelinePredator.createAttributes().build());
        event.put(PANTHER.get(), FelinePredator.createAttributes().build());
        event.put(LION.get(), FelinePredator.createAttributes().build());
        event.put(SABERTOOTH.get(), FelinePredator.createAttributes().build());
        event.put(TIGER.get(), FelinePredator.createAttributes().build());
        event.put(CROCODILE.get(), AmphibiousPredator.createAttributes().build());
        event.put(WOLF.get(), Predator.createAttributes().build());
        event.put(HYENA.get(), Predator.createAttributes().build());
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
        event.put(DEER.get(), Prey.createAttributes().build());
        event.put(BONGO.get(), Prey.createAttributes().build());
        event.put(GAZELLE.get(), Prey.createAttributes().build());
        event.put(CARIBOU.get(), Prey.createAttributes().build());
        event.put(BOAR.get(), RammingPrey.createAttributes().build());
        event.put(WILDEBEEST.get(), RammingPrey.createMediumAttributes().build());
        event.put(MOOSE.get(), RammingPrey.createLargeAttributes().build());
        event.put(GROUSE.get(), OviparousAnimal.createAttributes().build());
        event.put(PHEASANT.get(), OviparousAnimal.createAttributes().build());
        event.put(TURKEY.get(), OviparousAnimal.createAttributes().build());
        event.put(PEAFOWL.get(), OviparousAnimal.createAttributes().build());
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
        return new OviparousAnimal(animal, level, TFCSounds.CHICKEN, TFCConfig.SERVER.chickenConfig, true)
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
        return new OviparousAnimal(animal, level, TFCSounds.DUCK, TFCConfig.SERVER.duckConfig, false)
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
        return new OviparousAnimal(animal, level, TFCSounds.QUAIL, TFCConfig.SERVER.quailConfig, false)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.QUAIL_FOOD;
            }
        };
    }

    public static TFCRabbit makeRabbit(EntityType<? extends Rabbit> animal, Level level)
    {
        return new TFCRabbit(animal, level, TFCConfig.SERVER.rabbitConfig);
    }
    public static RammingPrey makeBoar(EntityType<? extends RammingPrey> animal, Level level)
    {
        return new RammingPrey(animal, level, TFCSounds.BOAR, 0.1d);
    }
    public static RammingPrey makeWildebeest(EntityType<? extends RammingPrey> animal, Level level)
    {
        return new RammingPrey(animal, level, TFCSounds.WILDEBEEST, 0.1d);
    }
    public static RammingPrey makeMoose(EntityType<? extends RammingPrey> animal, Level level)
    {
        return new RammingPrey(animal, level, TFCSounds.MOOSE, 0.75d);
    }

    public static Prey makeBongo(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.BONGO);
    }
    public static Prey makeCaribou(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.CARIBOU);
    }
    public static Prey makeDeer(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.DEER);
    }
    public static Prey makeGazelle(EntityType<? extends Prey> animal, Level level)
    {
        return new Prey(animal, level, TFCSounds.GAZELLE);
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
    public static WingedPrey makePeafowl(EntityType<? extends WingedPrey> animal, Level level)
    {
        return new WingedPrey(animal, level, TFCSounds.PEAFOWL);
    }

    public static Pest makeRat(EntityType<? extends Pest> animal, Level level)
    {
        return new Pest(animal, level, TFCSounds.RAT);
    }
    
    public record Id<T extends Entity>(DeferredHolder<EntityType<?>, EntityType<T>> holder)
        implements RegistryHolder<EntityType<?>, EntityType<T>> {}
}