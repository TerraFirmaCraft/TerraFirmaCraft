/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.common.entities.ai.prey.TFCOcelot;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.entities.aquatic.TFCSquid;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.Gender;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.common.entities.livestock.horse.TFCHorse;
import net.dries007.tfc.common.entities.misc.TFCFishingHook;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.entities.prey.TFCFrog;
import net.dries007.tfc.common.entities.prey.TFCRabbit;
import net.dries007.tfc.common.entities.prey.WildAnimal;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Common tooltips that can be displayed for various entities via external sources.
 */
public final class EntityTooltips
{
    public static void register(RegisterCallback<EntityTooltip, Entity> registry)
    {
        registry.register("animal", ANIMAL, TFCAnimal.class);
        registry.register("horse", ANIMAL, TFCHorse.class);
        registry.register("chested_horse", ANIMAL, TFCChestedHorse.class);
        registry.register("rabbit", ANIMAL, TFCRabbit.class);
        registry.register("wild_animal", ANIMAL, WildAnimal.class);
        registry.register("frog", FROG, TFCFrog.class);
        registry.register("squid", SQUID, TFCSquid.class);
        registry.register("fish", FISH, WaterAnimal.class);
        registry.register("predator", PREDATOR, Predator.class);
        registry.register("pack_predator", PACK_PREDATOR, PackPredator.class);
        registry.register("ocelot", OCELOT, TFCOcelot.class);
        registry.register("rabbit", RABBIT, Rabbit.class);
        registry.register("fishing_hook", HOOK, TFCFishingHook.class);
    }

    public static final EntityTooltip FROG = (level, entity, tooltip) -> {
        if (entity instanceof TFCFrog frog)
        {
            tooltip.accept(Helpers.translateEnum(frog.isMale() ? Gender.MALE : Gender.FEMALE));
            final float familiarity = Math.max(0.0F, Math.min(1.0F, frog.getFamiliarity()));
            final String familiarityPercent = String.format("%.2f", familiarity * 100);
            tooltip.accept(Component.translatable("tfc.jade.familiarity", familiarityPercent));

        }
    };

    public static final EntityTooltip ANIMAL = (level, entity, tooltip) -> {
        if (entity instanceof WildAnimal animal)
        {
            if (animal.displayMaleCharacteristics())
            {
                tooltip.accept(Helpers.translateEnum(Gender.MALE));
            }
            else if (animal.displayFemaleCharacteristics())
            {
                tooltip.accept(Helpers.translateEnum(Gender.FEMALE));
            }
            if (animal.isBaby())
            {
                tooltip.accept(Component.translatable("tfc.jade.juvenile"));
            }
        }
        if (entity instanceof TFCAnimalProperties animal)
        {
            final MutableComponent line1 = Helpers.translateEnum(animal.getGender());

            if (animal.isFertilized())
            {
                line1.append(", ").append(Component.translatable("tfc.tooltip.fertilized"));
            }
            final float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
            final String familiarityPercent = String.format("%.2f", familiarity * 100);

            final Age age = animal.getAgeType();
            ChatFormatting familiarityStyle = ChatFormatting.GRAY;
            if (familiarity >= animal.getAdultFamiliarityCap() && age != Age.CHILD)
            {
                familiarityStyle = ChatFormatting.RED;
            }
            else if (familiarity >= TFCConfig.SERVER.familiarityDecayLimit.get())
            {
                familiarityStyle = ChatFormatting.WHITE;
            }
            line1.append(", ").append(Component.translatable("tfc.jade.familiarity", familiarityPercent).withStyle(familiarityStyle));
            tooltip.accept(line1);
            tooltip.accept(Component.translatable("tfc.jade.animal_size", animal.getGeneticSize()));
            if (animal.isReadyForAnimalProduct())
            {
                tooltip.accept(animal.getProductReadyName().withStyle(ChatFormatting.GREEN));
            }
            if (animal.isReadyToMate())
            {
                tooltip.accept(Component.translatable("tfc.jade.can_mate"));
            }

            // when the animal is 'used up' but hasn't hit its asynchronous old day yet
            final double usageRatio = animal.getUses() >= animal.getUsesToElderly() ? 0.99 : (float) animal.getUses() / animal.getUsesToElderly();
            switch (age)
            {
                case CHILD -> tooltip.accept(Component.translatable("tfc.jade.adulthood_progress", Calendars.get(level).getTimeDelta((long) ICalendar.TICKS_IN_DAY * animal.getDaysToAdulthood() + animal.getBirthTick() - Calendars.get(level).getTicks())));
                case ADULT -> tooltip.accept(Component.translatable("tfc.jade.animal_wear", String.format("%d%%", Math.min(100, Math.round(100f * usageRatio)))));
                case OLD -> tooltip.accept(Component.translatable("tfc.jade.old_animal"));
            }

        }
        if (entity instanceof MammalProperties mammal)
        {
            if (mammal.getPregnantTime() > 0)
            {
                tooltip.accept(Component.translatable("tfc.tooltip.animal.pregnant", entity.getName().getString()));

                final ICalendar calendar = Calendars.get(level);
                tooltip.accept(Component.translatable("tfc.jade.gestation_time_left", calendar.getTimeDelta(ICalendar.TICKS_IN_DAY * (mammal.getGestationDays() + mammal.getPregnantTime() - Calendars.get(level).getTotalDays()))));
            }
        }
        if (entity instanceof HorseProperties horse)
        {
            if (horse.getFamiliarity() >= HorseProperties.TAMED_FAMILIARITY)
            {
                tooltip.accept(Component.translatable("tfc.jade.may_ride_horse"));
            }
            if (entity instanceof TFCHorse tfcHorse)
            {
                tooltip.accept(Component.translatable("tfc.jade.variant_and_markings", Helpers.translateEnum(tfcHorse.getVariant(), "horse_variant"), Helpers.translateEnum(tfcHorse.getMarkings())));
            }
            if (entity instanceof TFCChestedHorse chested && !chested.getChestItem().isEmpty())
            {
                final MutableComponent component = chested.getChestItem().getHoverName().copy();
                final IFluidHandlerItem fluidHandler = chested.getChestItem().getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandler != null && !fluidHandler.getFluidInTank(0).isEmpty())
                {
                    component.append(", ").append(Tooltips.fluidUnitsOf(fluidHandler.getFluidInTank(0)));
                }
                tooltip.accept(component);
            }
        }
    };

    public static final EntityTooltip PACK_PREDATOR = (level, entity, tooltip) -> {
        if (entity instanceof PackPredator predator)
        {
            tooltip.accept(Component.translatable("tfc.jade.pack_respect", predator.getRespect()));
            if (predator.isTamable())
            {
                final String familiarityPercent = String.format("%.2f", predator.getFamiliarity() * 100);
                tooltip.accept(Component.translatable("tfc.jade.familiarity", familiarityPercent));
            }
        }
    };

    public static final EntityTooltip OCELOT = (level, entity, tooltip) -> {
        if (entity instanceof TFCOcelot ocelot)
        {
            final String familiarityPercent = String.format("%.2f", ocelot.getFamiliarity() * 100);
            tooltip.accept(Component.translatable("tfc.jade.familiarity", familiarityPercent));
        }
    };

    public static final EntityTooltip SQUID = (level, entity, tooltip) -> {
        if (entity instanceof TFCSquid squid)
        {
            tooltip.accept(Component.translatable("tfc.jade.squid_size", squid.getSize()));
        }
    };

    public static final EntityTooltip FISH = (level, entity, tooltip) -> {
        if (entity instanceof AquaticMob aquatic)
        {
            if (aquatic.canSpawnIn(TFCFluids.SALT_WATER.getSource()))
            {
                tooltip.accept(Component.translatable("tfc.jade.saltwater"));
            }
            if (aquatic.canSpawnIn(Fluids.WATER))
            {
                tooltip.accept(Component.translatable("tfc.jade.freshwater"));
            }
            if (Helpers.isEntity(entity, TFCTags.Entities.NEEDS_LARGE_FISHING_BAIT))
            {
                tooltip.accept(Component.translatable("tfc.jade.large_bait"));
            }
        }
    };

    public static final EntityTooltip PREDATOR = (level, entity, tooltip) -> {
        if (entity instanceof Predator predator)
        {
            tooltip.accept(predator.isDiurnal() ? Component.translatable("tfc.jade.diurnal") : Component.translatable("tfc.jade.nocturnal"));
        }
    };

    public static final EntityTooltip RABBIT = (level, entity, tooltip) -> {
        if (entity instanceof Rabbit rabbit)
        {
            tooltip.accept(Helpers.translateEnum(rabbit.getVariant(), "rabbit_variant"));
        }
    };

    public static final EntityTooltip HOOK = (level, entity, tooltip) -> {
        if (entity instanceof TFCFishingHook hook)
        {
            if (hook.getHookedIn() != null)
            {
                tooltip.accept(Component.translatable("tfc.jade.hooked", hook.getHookedIn().getName()));
            }
            if (!hook.getBait().isEmpty())
            {
                tooltip.accept(Component.translatable("tfc.jade.bait", hook.getBait().getHoverName()));
            }
        }
    };

}
