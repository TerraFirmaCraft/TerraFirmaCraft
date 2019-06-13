/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.json.LowercaseEnumTypeAdapterFactory;
import net.dries007.tfc.util.json.ResourceLocationJson;
import net.dries007.tfc.util.json.VeinTypeJson;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.WEST;

public final class Constants
{
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocationJson())
        .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
        .registerTypeAdapter(VeinType.class, new VeinTypeJson())
        .create();
    public static final String GUI_FACTORY = "net.dries007.tfc.client.TFCModGuiFactory";

    public static final Random RNG = new Random();

    /**
     * Used by horizontal rotatable blocks to search for a valid rotation in a given space,
     * starting from a preferred rotation(like the direction a player is looking upon placing it)
     * usage: facingPriorityLists.get(preferredFacing.getHorizontalIndex())
     */
    public static final List<List<EnumFacing>> facingPriorityLists = new ArrayList<>(4);

    static
    {
        List<EnumFacing> list = new ArrayList<>(4);
        list.add(SOUTH);
        list.add(WEST);
        list.add(EAST);
        list.add(NORTH);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(WEST);
        list.add(NORTH);
        list.add(SOUTH);
        list.add(EAST);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(NORTH);
        list.add(EAST);
        list.add(WEST);
        list.add(SOUTH);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(EAST);
        list.add(SOUTH);
        list.add(NORTH);
        list.add(WEST);
        facingPriorityLists.add(list);
    }
}
