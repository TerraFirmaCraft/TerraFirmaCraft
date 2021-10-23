/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import net.dries007.tfc.mixin.accessor.SpawnPlacementsAccessor;
import net.dries007.tfc.util.DataManager;

public class FaunaManager extends DataManager<Fauna>
{
    public static final FaunaManager MANAGER = new FaunaManager();

    protected FaunaManager()
    {
        super("fauna", "fauna");
    }

    @Override
    protected Fauna read(ResourceLocation id, JsonObject obj)
    {
        return new Fauna(id, obj);
    }

    @Override
    protected void postProcess()
    {
        MANAGER.getValues().forEach(fauna -> {
            // MC will throw up if we have duplicate keys, so we have to remove first.
            SpawnPlacementsAccessor.accessor$getSpawnData().remove(fauna.getEntity());
            SpawnPlacements.register(fauna.getEntity(), fauna.getType(), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, fauna.makeRules());
        });
        super.postProcess();
    }
}
