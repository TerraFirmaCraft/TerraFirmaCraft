/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.common;

import net.minecraft.ChatFormatting;

import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

/**
 * Common tooltips that can be displayed for various entities via external sources.
 */
public final class EntityTooltips
{
    public static final EntityTooltip ANIMAL = (level, entity, tooltip) -> {
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
}
