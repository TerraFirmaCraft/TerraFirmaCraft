/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import net.dries007.tfc.util.Helpers;

public class StarsReloadListener extends SimplePreparableReloadListener<JsonElement>
{
    private static final ResourceLocation ID = Helpers.identifier("stars.json");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    @Override
    protected JsonElement prepare(ResourceManager resourceManager, ProfilerFiller profiler)
    {
        try (var reader = resourceManager.openAsReader(ID))
        {
            return GsonHelper.fromJson(GSON, reader, JsonElement.class);
        }
        catch (IOException | JsonParseException e)
        {
            LOGGER.error("Could not load stars", e);
            return JsonNull.INSTANCE;
        }
    }

    @Override
    protected void apply(JsonElement object, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        try
        {
            LevelRendererExtension.INSTANCE.updateStars(Star.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow(JsonParseException::new));
        }
        catch (JsonParseException e)
        {
            LOGGER.error("Could not parse stars.json", e);
        }
    }
}
