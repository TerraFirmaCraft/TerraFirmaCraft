/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.entities.aquatic.*;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.mixin.entity.EntityTypeAccessor;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    // Misc
    public static final RegistryObject<EntityType<TFCFallingBlockEntity>> FALLING_BLOCK = register("falling_block", EntityType.Builder.<TFCFallingBlockEntity>of(TFCFallingBlockEntity::new, EntityClassification.MISC).sized(0.98f, 0.98f));

    // Sea Creatures
    public static final RegistryObject<EntityType<TFCCodEntity>> COD = register("cod", EntityType.Builder.of(TFCCodEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCSalmonEntity>> SALMON = register("salmon", EntityType.Builder.of(TFCSalmonEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCPufferfishEntity>> PUFFERFISH = register("pufferfish", EntityType.Builder.of(TFCPufferfishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<TFCTropicalFishEntity>> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.of(TFCTropicalFishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<JellyfishEntity>> JELLYFISH = register("jellyfish", EntityType.Builder.of(JellyfishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.3F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<ManateeEntity>> MANATEE = register("manatee", EntityType.Builder.of(ManateeEntity::new, EntityClassification.WATER_CREATURE).sized(1.0F, 1.0F).clientTrackingRange(16));
    public static final RegistryObject<EntityType<OrcaEntity>> ORCA = register("orca", EntityType.Builder.of(OrcaEntity::new, EntityClassification.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<TFCDolphinEntity>> DOLPHIN = register("dolphin", EntityType.Builder.of(TFCDolphinEntity::new, EntityClassification.WATER_CREATURE).sized(0.9F, 0.6F));
    public static final RegistryObject<EntityType<SeafloorCritterEntity>> ISOPOD = register("isopod", EntityType.Builder.of(SeafloorCritterEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<SeafloorCritterEntity>> LOBSTER = register("lobster", EntityType.Builder.of(SeafloorCritterEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));

    // Predators
    public static final RegistryObject<EntityType<VultureEntity>> VULTURE = register("vulture", EntityType.Builder.of(VultureEntity::new, EntityClassification.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8));

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        return ENTITIES.register(name, () -> {
            // This is a hack to avoid the data fixer lookup and error message when it can't find one
            // This could be resolved by MinecraftForge#7636 which would put it behind a config option - hopefully, defaulting to true.
            final String id = MOD_ID + ":" + name;
            final EntityType<E> type = builder.noSave().build(id);
            ((EntityTypeAccessor) type).accessor$setSerialize(serialize);
            return type;
        });
    }

    public static void setup()
    {
        GlobalEntityTypeAttributes.put(COD.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(SALMON.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(PUFFERFISH.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(TROPICAL_FISH.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(JELLYFISH.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(MANATEE.get(), AbstractFishEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).build());
        GlobalEntityTypeAttributes.put(ORCA.get(), DolphinEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(DOLPHIN.get(), DolphinEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(ISOPOD.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(LOBSTER.get(), AbstractFishEntity.createAttributes().build());
        GlobalEntityTypeAttributes.put(VULTURE.get(), MonsterEntity.createMonsterAttributes().build());

        EntitySpawnPlacementRegistry.register(ISOPOD.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SeafloorCritterEntity.createSpawnRules(-50F, 13F, 50F, 450F));
        EntitySpawnPlacementRegistry.register(LOBSTER.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SeafloorCritterEntity.createSpawnRules(-30F, 17F, 20F, 500F));

        ForgeFlowingFluid saltWater = TFCFluids.SALT_WATER.getSource();
        EntitySpawnPlacementRegistry.register(COD.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TFCAbstractGroupFishEntity.createSpawnRules(-50F, 18F, 20F, 500F, saltWater));
        EntitySpawnPlacementRegistry.register(PUFFERFISH.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TFCAbstractGroupFishEntity.createSpawnRules(-10F, 50F, 20F, 500F, saltWater));
        EntitySpawnPlacementRegistry.register(TROPICAL_FISH.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TFCAbstractGroupFishEntity.createSpawnRules(18F, 50F, 20F, 500F, saltWater));
        EntitySpawnPlacementRegistry.register(JELLYFISH.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TFCAbstractGroupFishEntity.createSpawnRules(18F, 50F, 20F, 500F, saltWater));
        EntitySpawnPlacementRegistry.register(ORCA.get(), PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcaEntity.createSpawnRules());
    }
}