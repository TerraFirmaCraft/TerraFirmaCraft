/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.types;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.MetalItem;
import net.dries007.tfc.util.data.DataManager;

public class MetalItemManager extends DataManager<MetalItem>
{
    public static final MetalItemManager INSTANCE = new MetalItemManager();

    private MetalItemManager()
    {
        super(new GsonBuilder().create(), "metal_items", "metal item");
    }

    @Override
    protected MetalItem read(ResourceLocation id, JsonObject obj)
    {
        return new MetalItem(id, obj);
    }
}
