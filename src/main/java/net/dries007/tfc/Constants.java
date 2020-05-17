/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.damage.DamageResistance;
import net.dries007.tfc.objects.entity.animal.AnimalFood;
import net.dries007.tfc.util.json.*;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public final class Constants
{
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocationJson())
        .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
        .registerTypeAdapter(VeinType.class, new VeinTypeJson())
        .registerTypeAdapter(DamageResistance.class, new DamageResistanceJson())
        .registerTypeAdapter(AnimalFood.class, new AnimalFoodJson())
        .create();
    public static final String GUI_FACTORY = "net.dries007.tfc.client.TFCModGuiFactory";

    public static final Random RNG = new Random();
}