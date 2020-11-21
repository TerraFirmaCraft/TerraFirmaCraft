package net.dries007.tfc.common.capabilities.heat;

import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public class HeatManager extends DataManager<HeatDefinition>
{
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = new IndirectHashCollection<>(HeatDefinition::getValidItems);
    public static final HeatManager INSTANCE = new HeatManager();

    @Nullable
    public static HeatDefinition get(ItemStack stack)
    {
        for (HeatDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    private HeatManager()
    {
        super(new GsonBuilder().create(), "item_heats", "item heat");
    }

    @Override
    protected HeatDefinition read(ResourceLocation id, JsonObject obj)
    {
        return new HeatDefinition(id, obj);
    }
}
