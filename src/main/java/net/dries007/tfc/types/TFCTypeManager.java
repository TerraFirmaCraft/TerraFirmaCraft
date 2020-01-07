package net.dries007.tfc.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.IReloadableResourceManager;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.json.RockDeserializer;

/**
 * This is the central manager class for all dynamic TFC types (Rocks, Metals, etc.)
 */
public final class TFCTypeManager
{
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Rock.class, new RockDeserializer())
        .create();

    public static final TFCTypeReloadListener<Rock> ROCKS = new TFCTypeReloadListener<>(GSON, "rocks", Rock.class);

    public static void init(IReloadableResourceManager resourceManager)
    {
        resourceManager.addReloadListener(ROCKS);
    }

}
