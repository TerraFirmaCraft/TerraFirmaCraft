/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.network.PlayerDataUpdatePacket;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class PlayerData
{
    /**
     * Returns the {@link PlayerData} for a given {@code player}. This will always be present.
     * @param player The player to query. Can be either a server or client player, server data will be synced on modification to client.
     * @return The player data instance.
     */
    public static PlayerData get(Player player)
    {
        throw new IllegalStateException("no player data"); // todo: 1.21 porting
    }

    public static final long MAX_INTOXICATED_TICKS = 36 * ICalendar.TICKS_IN_HOUR; // A day and a half. Each drink gives you 4 hours of time

    private final Player player;
    @Nullable private CompoundTag delayedFoodNbt;

    private long lastDrinkTick;
    private long intoxicationTick;
    private ChiselRecipe.Mode chiselMode = ChiselRecipe.Mode.SMOOTH;

    public PlayerData(Player player)
    {
        this.player = player;
    }

    /**
     * @deprecated Use {@link #getIntoxicatedTicks()} instead.
     * @return The number of remaining ticks the player is intoxicated for
     */
    @Deprecated
    public long getIntoxicatedTicks(boolean isClientSide)
    {
        return getIntoxicatedTicks();
    }

    public long getIntoxicatedTicks()
    {
        return Math.max(0, intoxicationTick - Calendars.get(player.level().isClientSide).getTicks());
    }

    /**
     * Intoxicates the player for at least the next {@code ticks}.
     */
    public void addIntoxicatedTicks(long ticks)
    {
        long currentTick = Calendars.SERVER.getTicks();
        if (intoxicationTick < currentTick)
        {
            intoxicationTick = currentTick;
        }
        intoxicationTick += ticks;
        if (intoxicationTick > currentTick + MAX_INTOXICATED_TICKS)
        {
            intoxicationTick = currentTick + MAX_INTOXICATED_TICKS;
        }
        sync();
    }

    public long getLastDrinkTick()
    {
        return lastDrinkTick;
    }

    public void setLastDrinkTick(long lastDrinkTick)
    {
        this.lastDrinkTick = lastDrinkTick;
        sync();
    }

    public ChiselRecipe.Mode getChiselMode()
    {
        return chiselMode;
    }

    public void cycleChiselMode()
    {
        chiselMode = chiselMode.next();
        sync();
    }

    public void setChiselMode(ChiselRecipe.Mode mode)
    {
        chiselMode = mode;
        sync();
    }

    public void sync()
    {
        if (player instanceof final ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerDataUpdatePacket(lastDrinkTick, intoxicationTick, chiselMode));
        }
    }

    public void updateFromPacket(long lastDrinkTick, long intoxicationTick, ChiselRecipe.Mode mode)
    {
        this.lastDrinkTick = lastDrinkTick;
        this.intoxicationTick = intoxicationTick;
        this.chiselMode = mode;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        final CompoundTag nbt = new CompoundTag();
        if (player.getFoodData() instanceof TFCFoodData data)
        {
            nbt.put("food", data.serializeToPlayerData());
        }
        nbt.putLong("lastDrinkTick", lastDrinkTick);
        nbt.putLong("intoxicationTick", intoxicationTick);
        nbt.putInt("chiselMode", chiselMode.ordinal());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        delayedFoodNbt = nbt.contains("food", Tag.TAG_COMPOUND) ? nbt.getCompound("food") : null;
        if (player.getFoodData() instanceof TFCFoodData data)
        {
            writeTo(data);
        }
        lastDrinkTick = nbt.getLong("lastDrinkTick");
        intoxicationTick = nbt.getLong("intoxicationTick");
        chiselMode = ChiselRecipe.Mode.valueOf(nbt.getInt("chiselMode"));
    }

    public void writeTo(TFCFoodData stats)
    {
        if (delayedFoodNbt != null)
        {
            stats.deserializeFromPlayerData(delayedFoodNbt);
            delayedFoodNbt = null;
        }
    }
}
