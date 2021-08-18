package net.dries007.tfc.util.events;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import net.dries007.tfc.world.settings.RockSettings;

/**
 * Fired during the creation of default rock layers for a given dimension.
 * These are only used if the defaults are used - if a pack maker overrides the overworld dimension, the results from this event will be discarded.
 *
 * This event is not managed in any meaningful way. You are free to add, remove, or mutate the underlying map however you'd like.
 * If you break shit with this event (such as leave the map empty), that's on you!
 */
public class RockLoadingEvent extends Event
{
    private final Map<ResourceLocation, RockSettings> settings;

    public RockLoadingEvent(Map<ResourceLocation, RockSettings> settings)
    {
        this.settings = settings;
    }

    public Map<ResourceLocation, RockSettings> getSettings()
    {
        return settings;
    }
}
