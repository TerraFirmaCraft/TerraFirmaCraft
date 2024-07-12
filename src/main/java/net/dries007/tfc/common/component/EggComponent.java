/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import java.util.List;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 *
 * @param fertilized Is this egg fertilized?
 * @param hatchDay The day it will hatch, as per {@link ICalendar#getTotalDays()}
 * @param entity The saved NBT of the entity that it will hatch into
 */
public record EggComponent(
    boolean fertilized,
    long hatchDay,
    Optional<CompoundTag> entity
)
{
    public static final Codec<EggComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.BOOL.optionalFieldOf("fertilized", false).forGetter(c -> c.fertilized),
        Codec.LONG.optionalFieldOf("hatch_day", -1L).forGetter(c -> c.hatchDay),
        CompoundTag.CODEC.optionalFieldOf("entity").forGetter(c -> c.entity)
    ).apply(i, EggComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EggComponent> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, c -> c.fertilized,
        ByteBufCodecs.VAR_LONG, c -> c.hatchDay,
        ByteBufCodecs.OPTIONAL_COMPOUND_TAG, c -> c.entity,
        EggComponent::new
    );

    public static final EggComponent DEFAULT = new EggComponent(false, -1, Optional.empty());

    public static EggComponent of(HolderLookup.Provider provider, OviparousAnimal entity, long hatchDay)
    {
        return new EggComponent(true, hatchDay, Optional.of(entity.serializeNBT(provider)));
    }

    public void addTooltipInfo(List<Component> text)
    {
        if (fertilized())
        {
            final long remainingDays = hatchDay() - Calendars.CLIENT.getTotalDays();
            text.add(Component.translatable("tfc.tooltip.fertilized"));
            if (remainingDays > 0)
            {
                text.add(Component.translatable("tfc.tooltip.egg_hatch", remainingDays));
            }
            else
            {
                text.add(Component.translatable("tfc.tooltip.egg_hatch_today"));
            }
        }
    }

    /**
     * Should only be invoked on logical server
     * @return {@code true} if the egg is fertilized and is able to hatch today.
     */
    public boolean canHatch()
    {
        return fertilized && hatchDay <= Calendars.SERVER.getTotalDays();
    }

    /**
     * @param level The world, must be on server
     * @return An entity to hatch into, or empty if there is no entity for some reason
     */
    public Optional<Entity> hatch(Level level)
    {
        return entity.flatMap(e -> EntityType.create(e, level));
    }
}
