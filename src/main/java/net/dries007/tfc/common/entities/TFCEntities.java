/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Locale;
import java.util.Map;

import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.land.DairyAnimal;
import net.dries007.tfc.common.entities.land.Mammal;
import net.dries007.tfc.common.entities.aquatic.*;
import net.dries007.tfc.common.entities.land.OviparousAnimal;
import net.dries007.tfc.common.entities.land.WoolyAnimal;
import net.dries007.tfc.common.entities.predator.Predator;
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
 */
@SuppressWarnings("unused")
public class TFCEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    // Misc

    public static final RegistryObject<EntityType<TFCFallingBlockEntity>> FALLING_BLOCK = register("falling_block", EntityType.Builder.<TFCFallingBlockEntity>of(TFCFallingBlockEntity::new, MobCategory.MISC).sized(0.98f, 0.98f));
    public static final RegistryObject<EntityType<TFCFishingHook>> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<TFCFishingHook>of(TFCFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final RegistryObject<EntityType<GlowArrow>> GLOW_ARROW = register("glow_arrow", EntityType.Builder.<GlowArrow>of(GlowArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    public static final RegistryObject<EntityType<Seat>> SEAT = register("seat", EntityType.Builder.of(Seat::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(20));

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

    public static final RegistryObject<EntityType<AquaticCritter>> ISOPOD = register("isopod", EntityType.Builder.of(AquaticCritter::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<AquaticCritter>> LOBSTER = register("lobster", EntityType.Builder.of(AquaticCritter::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<FreshWaterCritter>> CRAYFISH = register("crayfish", EntityType.Builder.of(FreshWaterCritter::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<AquaticCritter>> HORSESHOE_CRAB = register("horseshoe_crab", EntityType.Builder.of(AquaticCritter::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));

    // Water Creatures

    public static final RegistryObject<EntityType<TFCDolphin>> DOLPHIN = register("dolphin", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<TFCDolphin>> ORCA = register("orca", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<Manatee>> MANATEE = register("manatee", EntityType.Builder.of(Manatee::new, MobCategory.WATER_CREATURE).sized(1.0F, 1.0F));
    public static final RegistryObject<EntityType<TFCSquid>> SQUID = register("squid", EntityType.Builder.of(TFCSquid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<Octopoteuthis>> OCTOPOTEUTHIS = register("octopoteuthis", EntityType.Builder.of(Octopoteuthis::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));

    // Creatures
    public static final RegistryObject<EntityType<AmphibiousAnimal>> TURTLE = register("turtle", EntityType.Builder.of(AmphibiousAnimal::new, MobCategory.CREATURE).sized(0.8F, 0.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AmphibiousAnimal>> PENGUIN = register("penguin", EntityType.Builder.of(AmphibiousAnimal::new, MobCategory.CREATURE).sized(0.3F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Predator>> POLAR_BEAR = register("polar_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Predator>> GRIZZLY_BEAR = register("grizzly_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.3F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Predator>> BLACK_BEAR = register("black_bear", EntityType.Builder.of(Predator::createBear, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.2F, 1.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> COUGAR = register("cougar", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> PANTHER = register("panther", EntityType.Builder.of(FelinePredator::createCougar, MobCategory.CREATURE).sized(1.0F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> LION = register("lion", EntityType.Builder.of(FelinePredator::createLion, MobCategory.CREATURE).sized(1.0F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FelinePredator>> SABERTOOTH = register("sabertooth", EntityType.Builder.of(FelinePredator::createSabertooth, MobCategory.CREATURE).sized(1.1F, 1.3F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<Mammal>> PIG = register("pig", EntityType.Builder.of(TFCEntities::makePig, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DairyAnimal>> COW = register("cow", EntityType.Builder.of(TFCEntities::makeCow, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoolyAnimal>> ALPACA = register("alpaca", EntityType.Builder.of(TFCEntities::makeAlpaca, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<OviparousAnimal>> CHICKEN = register("chicken", EntityType.Builder.of(TFCEntities::makeChicken, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITIES.register(id, () -> builder.build(MOD_ID + ":" + id));
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
        event.put(SQUID.get(), Squid.createAttributes().build());
        event.put(OCTOPOTEUTHIS.get(), GlowSquid.createAttributes().build());
        event.put(PIG.get(), Pig.createAttributes().build());
        event.put(COW.get(), Cow.createAttributes().build());
        event.put(ALPACA.get(), Cow.createAttributes().build());
        event.put(CHICKEN.get(), Chicken.createAttributes().build());
    }

    public static Mammal makePig(EntityType<? extends Mammal> animal, Level level)
    {
        return new Mammal(animal, level, () -> SoundEvents.PIG_AMBIENT, () -> SoundEvents.PIG_HURT, () -> SoundEvents.PIG_DEATH, () -> SoundEvents.PIG_STEP, TFCConfig.SERVER.pigFamiliarityCap, TFCConfig.SERVER.pigAdulthoodDays, TFCConfig.SERVER.pigUses, TFCConfig.SERVER.pigEatsRottenFood, TFCConfig.SERVER.pigChildCount, TFCConfig.SERVER.pigGestationDays)
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
        return new DairyAnimal(animal, level, () -> SoundEvents.COW_AMBIENT, () -> SoundEvents.COW_HURT, () -> SoundEvents.COW_DEATH, () -> SoundEvents.COW_STEP, TFCConfig.SERVER.cowFamiliarityCap, TFCConfig.SERVER.cowAdulthoodDays, TFCConfig.SERVER.cowUses, TFCConfig.SERVER.cowEatsRottenFood, TFCConfig.SERVER.cowChildCount, TFCConfig.SERVER.cowGestationDays, TFCConfig.SERVER.cowMilkTicks, TFCConfig.SERVER.cowMinMilkFamiliarity)
        {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.COW_FOOD;
            }
        };
    }

    public static WoolyAnimal makeAlpaca(EntityType<? extends WoolyAnimal> animal, Level level)
    {
        return new WoolyAnimal(animal, level, TFCSounds.ALPACA_AMBIENT, TFCSounds.ALPACA_HURT, TFCSounds.ALPACA_DEATH, TFCSounds.ALPACA_STEP, TFCConfig.SERVER.alpacaFamiliarityCap, TFCConfig.SERVER.alpacaAdulthoodDays, TFCConfig.SERVER.alpacaUses, TFCConfig.SERVER.alpacaEatsRottenFood, TFCConfig.SERVER.alpacaChildCount, TFCConfig.SERVER.alpacaGestationDays, TFCConfig.SERVER.alpacaWoolTicks, TFCConfig.SERVER.alpacaMinWoolFamiliarity) {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.ALPACA_FOOD;
            }
        };
    }

    public static OviparousAnimal makeChicken(EntityType<? extends OviparousAnimal> animal, Level level)
    {
        return new OviparousAnimal(animal, level, () -> SoundEvents.CHICKEN_AMBIENT, () -> SoundEvents.CHICKEN_HURT, () -> SoundEvents.CHICKEN_DEATH, () -> SoundEvents.CHICKEN_STEP, TFCConfig.SERVER.chickenFamiliarityCap, TFCConfig.SERVER.chickenAdulthoodDays, TFCConfig.SERVER.chickenUses, TFCConfig.SERVER.chickenEatsRottenFood, TFCConfig.SERVER.chickenEggTicks, TFCConfig.SERVER.chickenMinEggFamiliarity, TFCConfig.SERVER.chickenHatchDays) {
            @Override
            public TagKey<Item> getFoodTag()
            {
                return TFCTags.Items.CHICKEN_FOOD;
            }
        };
    }

}