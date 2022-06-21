/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.mojang.serialization.JsonOps;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.settings.ClimateSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClimateSettingsTests extends TestHelper
{
    @Test
    public void testEncodeDecodePresets()
    {
        final ClimateSettings preset = ClimateSettings.DEFAULT;
        final JsonElement encoded = ClimateSettings.CODEC.encodeStart(JsonOps.INSTANCE, preset).getOrThrow(false, Assertions::fail);
        final ClimateSettings decoded = ClimateSettings.CODEC.decode(JsonOps.INSTANCE, encoded).getOrThrow(false, Assertions::fail).getFirst();

        assertEquals(preset, decoded);
    }

    @Test
    public void testEncodeDecodeCustom()
    {
        final ClimateSettings custom = new ClimateSettings(100, false);
        final JsonElement encoded = ClimateSettings.CODEC.encodeStart(JsonOps.INSTANCE, custom).getOrThrow(false, Assertions::fail);
        final ClimateSettings decoded = ClimateSettings.CODEC.decode(JsonOps.INSTANCE, encoded).getOrThrow(false, Assertions::fail).getFirst();

        assertEquals(custom, decoded);
    }

    @Test
    public void testDecodePreset()
    {
        final JsonElement json = new JsonPrimitive("tfc:default");
        final ClimateSettings decoded = ClimateSettings.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, Assertions::fail).getFirst();

        assertEquals(decoded, ClimateSettings.DEFAULT);
    }

    @Test
    public void testDecodeCustom()
    {
        final JsonObject json = new JsonObject();

        json.addProperty("scale", 100);
        json.addProperty("endless_poles", false);

        final ClimateSettings decoded = ClimateSettings.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, Assertions::fail).getFirst();
        final ClimateSettings expected = new ClimateSettings(100, false);

        assertEquals(expected, decoded);
    }
}
