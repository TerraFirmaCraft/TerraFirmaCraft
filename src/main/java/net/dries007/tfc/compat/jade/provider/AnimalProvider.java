/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public enum AnimalProvider implements IEntityComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getEntity() instanceof TFCAnimalProperties animal)
        {
            tooltip.add(Helpers.translateEnum(animal.getGender()));
            if (animal.isFertilized())
            {
                tooltip.add(Helpers.translatable("tfc.tooltip.fertilized"));
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
            tooltip.add(Helpers.translatable("tfc.jade.familiarity").append(Helpers.literal(familiarityPercent).withStyle(familiarityStyle)));
            if (animal.isReadyForAnimalProduct())
            {
                tooltip.add(animal.getProductReadyName().withStyle(ChatFormatting.GREEN));
            }
            if (animal.isReadyToMate())
            {
                tooltip.add(Helpers.translatable("tfc.jade.can_mate"));
            }

            switch (age)
            {
                case CHILD -> {
                    tooltip.add(Helpers.translatable("tfc.jade.adulthood_days", animal.getDaysToAdulthood()));
                }
                case ADULT -> {
                    tooltip.add(Helpers.translatable("tfc.jade.animal_wear").append(Helpers.formatPercentage(100f * animal.getUses() / animal.getUsesToElderly())));
                }
                case OLD -> {
                    tooltip.add(Helpers.translatable("tfc.jade.old_animal"));
                }
            }

        }
        if (access.getEntity() instanceof MammalProperties mammal)
        {
            if (mammal.getPregnantTime() > 0)
            {
                tooltip.add(Helpers.translatable("tfc.tooltip.animal.pregnant", access.getEntity().getName().getString()));

                final long totalDays = Calendars.get(access.getLevel()).getTotalDays();
                tooltip.add(Helpers.translatable("tfc.jade.gestation_progress", Helpers.formatPercentage(100f * (mammal.getPregnantTime() - totalDays) / mammal.getGestationDays())));
            }
        }
        if (access.getEntity() instanceof HorseProperties horse)
        {
            if (horse.getFamiliarity() >= HorseProperties.TAMED_FAMILIARITY)
            {
                tooltip.add(Helpers.translatable("tfc.jade.may_ride_horse"));
            }
            if (access.getEntity() instanceof TFCHorse tfcHorse)
            {
                tooltip.add(Helpers.translateEnum(tfcHorse.getVariant()).append(Helpers.literal(", ").append(Helpers.translateEnum(tfcHorse.getMarkings()))));
            }
        }
    }
}
