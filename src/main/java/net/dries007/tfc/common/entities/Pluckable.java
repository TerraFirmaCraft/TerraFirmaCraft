/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCDamageTypes;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.AnimalProductEvent;

public interface Pluckable
{
    int PLUCKING_COOLDOWN = ICalendar.TICKS_IN_HOUR;

    long getLastPluckedTick();

    void setLastPluckedTick(long tick);

    default boolean pluck(Player player, InteractionHand hand, LivingEntity entity)
    {
        final Level level = entity.level();
        if (level.isClientSide || hand == InteractionHand.OFF_HAND)
            return false;
        if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown() && (entity.getHealth() / entity.getMaxHealth() > 0.15001f))
        {
            if (Calendars.SERVER.getTicks() < getLastPluckedTick() + PLUCKING_COOLDOWN)
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.animal.cannot_pluck", ICalendar.getTimeDelta(PLUCKING_COOLDOWN - (Calendars.SERVER.getTicks() -  getLastPluckedTick()), Calendars.SERVER.getCalendarDaysInMonth())), true);
                return false;
            }
            if (entity.getHealth() / entity.getMaxHealth() <= 0.15f)
            {
                player.displayClientMessage(Component.translatable("tfc.tooltip.animal.cannot_pluck_old_or_sick"), true);
                return false;
            }
            ItemStack feather = new ItemStack(Items.FEATHER, Mth.nextInt(entity.getRandom(), 1, 3));
            if (entity instanceof TFCAnimalProperties properties)
            {
                // since becoming old is asynchronous it is not enough
                if (properties.getAgeType() == TFCAnimalProperties.Age.ADULT && properties.getUses() < properties.getUsesToElderly())
                {
                    AnimalProductEvent event = new AnimalProductEvent(level, entity.blockPosition(), player, properties, feather, ItemStack.EMPTY, 1);
                    if (!NeoForge.EVENT_BUS.post(event).isCanceled())
                    {
                        TFCDamageTypes.pluck(entity, entity.getMaxHealth() * 0.15f, null);
                        properties.addUses(event.getUses());
                        ItemHandlerHelper.giveItemToPlayer(player, event.getProduct());
                    }
                    setLastPluckedTick(Calendars.SERVER.getTicks());
                }
                else
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.animal.cannot_pluck_old_or_sick"), true);
                    return false;
                }
            }
            else
            {
                ItemHandlerHelper.giveItemToPlayer(player, feather);
                TFCDamageTypes.pluck(entity, entity.getMaxHealth() * 0.15f, null);
                setLastPluckedTick(Calendars.SERVER.getTicks());
            }
            return true;
        }
        return false;
    }
}
