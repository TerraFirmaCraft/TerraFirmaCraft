/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

import net.dries007.tfc.util.climate.BiomeBasedClimateModel;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.ClimateModels;

/**
 * An event which is posted while a world is loading or selecting its climate model. This provides access to the level, and
 * is fired during world load. It is only fired on server, and the climate model will to synced to client automatically.
 */
public final class SelectClimateModelEvent extends Event
{
    private final ServerLevel level;
    private ClimateModel model;

    public SelectClimateModelEvent(ServerLevel level)
    {
        this.level = level;
        this.model = BiomeBasedClimateModel.INSTANCE;
    }

    /**
     * @return The server level that we are selecting for.
     */
    public ServerLevel level()
    {
        return level;
    }

    /**
     * @return the currently selected climate model, or {@link BiomeBasedClimateModel} by default.
     */
    public ClimateModel getModel()
    {
        return model;
    }

    public void setModel(ClimateModel model)
    {
        this.model = model;
    }
}
