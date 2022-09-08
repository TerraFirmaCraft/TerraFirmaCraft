/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.common;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCFishingHook;
import net.dries007.tfc.common.entities.WildAnimal;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.entities.aquatic.TFCSquid;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

/**
 * Common tooltips that can be displayed for various entities via external sources.
 */
public final class EntityTooltips
{
    public static final EntityTooltip ANIMAL = (level, entity, tooltip) -> {
        if (entity instanceof WildAnimal animal)
        {
            if (animal.displayMaleCharacteristics())
            {
                tooltip.accept(Helpers.translateEnum(TFCAnimalProperties.Gender.MALE));
            }
            else if (animal.displayFemaleCharacteristics())
            {
                tooltip.accept(Helpers.translateEnum(TFCAnimalProperties.Gender.FEMALE));
            }
        }
        if (entity instanceof TFCAnimalProperties animal)
        {
            tooltip.accept(Helpers.translateEnum(animal.getGender()));
            if (animal.isFertilized())
            {
                tooltip.accept(Helpers.translatable("tfc.tooltip.fertilized"));
            }
            final float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
            final String familiarityPercent = String.format("%.2f", familiarity * 100);

            final TFCAnimalProperties.Age age = animal.getAgeType();
            ChatFormatting familiarityStyle = ChatFormatting.GRAY;
            if (familiarity >= animal.getAdultFamiliarityCap() && age != TFCAnimalProperties.Age.CHILD)
            {
                familiarityStyle = ChatFormatting.RED;
            }
            else if (familiarity > 0.3f)
            {
                familiarityStyle = ChatFormatting.WHITE;
            }
            tooltip.accept(Helpers.translatable("tfc.jade.familiarity", familiarityPercent).withStyle(familiarityStyle));
            if (animal.isReadyForAnimalProduct())
            {
                tooltip.accept(animal.getProductReadyName().withStyle(ChatFormatting.GREEN));
            }
            if (animal.isReadyToMate())
            {
                tooltip.accept(Helpers.translatable("tfc.jade.can_mate"));
            }

            switch (age)
            {
                case CHILD -> tooltip.accept(Helpers.translatable("tfc.jade.adulthood_days", animal.getDaysToAdulthood()));
                case ADULT -> tooltip.accept(Helpers.translatable("tfc.jade.animal_wear", String.format("%d%%", Math.round(100f * animal.getUses() / animal.getUsesToElderly()))));
                case OLD -> tooltip.accept(Helpers.translatable("tfc.jade.old_animal"));
            }

        }
        if (entity instanceof MammalProperties mammal)
        {
            if (mammal.getPregnantTime() > 0)
            {
                tooltip.accept(Helpers.translatable("tfc.tooltip.animal.pregnant", entity.getName().getString()));

                final long totalDays = Calendars.get(level).getTotalDays();
                tooltip.accept(Helpers.translatable("tfc.jade.gestation_progress", String.format("%d%%", Math.round(100f * (mammal.getPregnantTime() - totalDays) / mammal.getGestationDays()))));
            }
        }
        if (entity instanceof HorseProperties horse)
        {
            if (horse.getFamiliarity() >= HorseProperties.TAMED_FAMILIARITY)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.may_ride_horse"));
            }
            if (entity instanceof TFCHorse tfcHorse)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.variant_and_markings", Helpers.translateEnum(tfcHorse.getVariant()), Helpers.translateEnum(tfcHorse.getMarkings())));
            }
            if (entity instanceof TFCChestedHorse chested && !chested.getChestItem().isEmpty())
            {
                tooltip.accept(chested.getChestItem().getHoverName());
            }
        }
    };

    public static final EntityTooltip SQUID = (level, entity, tooltip) -> {
        if (entity instanceof TFCSquid squid)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.squid_size", squid.getSize()));
        }
    };

    public static final EntityTooltip FISH = (level, entity, tooltip) -> {
        if (entity instanceof AquaticMob aquatic)
        {
            if (aquatic.canSpawnIn(TFCFluids.SALT_WATER.getSource()))
            {
                tooltip.accept(Helpers.translatable("tfc.jade.saltwater"));
            }
            if (aquatic.canSpawnIn(Fluids.WATER))
            {
                tooltip.accept(Helpers.translatable("tfc.jade.freshwater"));
            }
            if (Helpers.isEntity(entity, TFCTags.Entities.NEEDS_LARGE_FISHING_BAIT))
            {
                tooltip.accept(Helpers.translatable("tfc.jade.large_bait"));
            }
        }
    };

    public static final EntityTooltip PREDATOR = (level, entity, tooltip) -> {
        if (entity instanceof Predator predator)
        {
            tooltip.accept(predator.isDiurnal() ? Helpers.translatable("tfc.jade.diurnal") : Helpers.translatable("tfc.jade.nocturnal"));
        }
    };

    public static final EntityTooltip RABBIT = (level, entity, tooltip) -> {
        if (entity instanceof Rabbit rabbit)
        {
            final int type = rabbit.getRabbitType();
            if ((type >= 0 && type <= 5) || type == 99)
            {
                tooltip.accept(Helpers.translatable("tfc.rabbit_" + type));
            }
        }
    };

    public static final EntityTooltip HOOK = (level, entity, tooltip) -> {
        if (entity instanceof TFCFishingHook hook)
        {
            if (hook.getHookedIn() != null)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.hooked", hook.getHookedIn().getName()));
            }
            if (!hook.getBait().isEmpty())
            {
                tooltip.accept(Helpers.translatable("tfc.jade.bait", hook.getBait().getHoverName()));
            }
        }
    };

}
