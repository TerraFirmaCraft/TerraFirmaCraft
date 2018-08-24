/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.json.LowercaseEnumTypeAdapterFactory;
import net.dries007.tfc.util.json.ResourceLocationJson;

public final class Constants
{
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocationJson())
        .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
        .create();
    public static final Gson GSON_PP = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocationJson())
        .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
        .create();

    public static final String GUI_FACTORY = "net.dries007.tfc.client.TFCModGuiFactory";
}
