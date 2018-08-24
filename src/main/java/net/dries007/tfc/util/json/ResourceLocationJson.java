/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.minecraft.util.ResourceLocation;

public final class ResourceLocationJson implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation>
{
    @Override
    public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return new ResourceLocation(json.getAsString());
    }

    @Override
    public JsonElement serialize(ResourceLocation src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toString());
    }
}
