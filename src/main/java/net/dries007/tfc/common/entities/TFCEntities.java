/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.Locale;
import java.util.Map;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.aquatic.*;
import net.dries007.tfc.common.items.TFCItems;
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
    public static final RegistryObject<EntityType<AquaticCritter>> HORSESHOE_CRAB = register("horseshoe_crab", EntityType.Builder.of(AquaticCritter::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));

    // Water Creatures

    public static final RegistryObject<EntityType<TFCDolphin>> DOLPHIN = register("dolphin", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<TFCDolphin>> ORCA = register("orca", EntityType.Builder.of(TFCDolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<Manatee>> MANATEE = register("manatee", EntityType.Builder.of(Manatee::new, MobCategory.WATER_CREATURE).sized(1.0F, 1.0F));

    // Creatures

    public static final RegistryObject<EntityType<AmphibiousAnimal>> TURTLE = register("turtle", EntityType.Builder.of(AmphibiousAnimal::new, MobCategory.CREATURE).sized(0.8F, 0.5F).clientTrackingRange(10));

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
        event.put(ISOPOD.get(), AbstractFish.createAttributes().build());
        event.put(HORSESHOE_CRAB.get(), AbstractFish.createAttributes().build());
        event.put(DOLPHIN.get(), Dolphin.createAttributes().build());
        event.put(ORCA.get(), Dolphin.createAttributes().build());
        event.put(MANATEE.get(), Manatee.createAttributes().build());
        event.put(TURTLE.get(), AmphibiousAnimal.createAttributes().build());
    }
}