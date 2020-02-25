/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.minecraft.util.JsonUtils;

import net.dries007.tfc.api.capability.damage.DamageResistance;

public class DamageResistanceJson implements JsonDeserializer<DamageResistance>
{
    @Override
    public DamageResistance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = JsonUtils.getJsonObject(json, "entity");

        final float crushingModifier = JsonUtils.getFloat(jsonObject, "crushing_resistance");
        final float piercingModifier = JsonUtils.getFloat(jsonObject, "piercing_resistance");
        final float slashingModifier = JsonUtils.getFloat(jsonObject, "slashing_resistance");

        return new DamageResistance(crushingModifier, piercingModifier, slashingModifier);
    }
}
