/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.common;

import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCFishingHook;
import net.dries007.tfc.common.entities.WildAnimal;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.common.entities.ai.prey.TFCOcelot;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.entities.aquatic.TFCSquid;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Common tooltips that can be displayed for various entities via external sources.
 */
public final class EntityTooltips
{
    public static void register(BiConsumer<EntityTooltip, Class<? extends Entity>> registry)
    {
        registry.accept(ANIMAL, TFCAnimal.class);
        registry.accept(ANIMAL, TFCHorse.class);
        registry.accept(ANIMAL, TFCChestedHorse.class);
        registry.accept(ANIMAL, WildAnimal.class);
        registry.accept(SQUID, TFCSquid.class);
        registry.accept(FISH, WaterAnimal.class);
        registry.accept(PREDATOR, Predator.class);
        registry.accept(PACK_PREDATOR, PackPredator.class);
        registry.accept(OCELOT, TFCOcelot.class);
        registry.accept(RABBIT, Rabbit.class);
        registry.accept(HOOK, TFCFishingHook.class);
    }

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
            if (animal.isBaby())
            {
                tooltip.accept(Helpers.translatable("tfc.jade.juvenile"));
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
            tooltip.accept(Helpers.translatable("tfc.jade.animal_size", animal.getGeneticSize()));
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
                case CHILD -> tooltip.accept(Helpers.translatable("tfc.jade.adulthood_progress", Calendars.get(level).getTimeDelta(ICalendar.TICKS_IN_DAY * (animal.getDaysToAdulthood() + animal.getBirthDay() - Calendars.get(level).getTotalDays()))));
                case ADULT -> tooltip.accept(Helpers.translatable("tfc.jade.animal_wear", String.format("%d%%", Math.min(100, Math.round(100f * animal.getUses() / animal.getUsesToElderly())))));
                case OLD -> tooltip.accept(Helpers.translatable("tfc.jade.old_animal"));
            }

        }
        if (entity instanceof MammalProperties mammal)
        {
            if (mammal.getPregnantTime() > 0)
            {
                tooltip.accept(Helpers.translatable("tfc.tooltip.animal.pregnant", entity.getName().getString()));

                final ICalendar calendar = Calendars.get(level);
                tooltip.accept(Helpers.translatable("tfc.jade.gestation_time_left", calendar.getTimeDelta(ICalendar.TICKS_IN_DAY * (mammal.getGestationDays() + mammal.getPregnantTime() - Calendars.get(level).getTotalDays()))));
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

    public static final EntityTooltip PACK_PREDATOR = (level, entity, tooltip) -> {
        if (entity instanceof PackPredator predator)
        {
            tooltip.accept(Helpers.translatable("tfc.jade.pack_respect", predator.getRespect()));
            if (predator.isTamable())
            {
                final String familiarityPercent = String.format("%.2f", predator.getFamiliarity() * 100);
                tooltip.accept(Helpers.translatable("tfc.jade.familiarity", familiarityPercent));
            }
        }
    };

    public static final EntityTooltip OCELOT = (level, entity, tooltip) -> {
        if (entity instanceof TFCOcelot ocelot)
        {
            final String familiarityPercent = String.format("%.2f", ocelot.getFamiliarity() * 100);
            tooltip.accept(Helpers.translatable("tfc.jade.familiarity", familiarityPercent));
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
