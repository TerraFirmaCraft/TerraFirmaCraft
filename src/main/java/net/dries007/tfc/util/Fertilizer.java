package net.dries007.tfc.util;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Fertilizer extends ItemDefinition
{
    public static final DataManager<Fertilizer> MANAGER = new DataManager.Instance<>(Fertilizer::new, "fertilizers", "fertilizer");
    public static final IndirectHashCollection<Item, Fertilizer> CACHE = new IndirectHashCollection<>(Fertilizer::getValidItems);

    @Nullable
    public static Fertilizer get(ItemStack stack)
    {
        for (Fertilizer def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }

    private final float nitrogen, phosphorus, potassium;

    private Fertilizer(ResourceLocation id, JsonObject json)
    {
        super(id, Ingredient.fromJson(JsonHelpers.get(json, "ingredient")));

        nitrogen = JsonHelpers.getAsFloat(json, "nitrogen", 0);
        phosphorus = JsonHelpers.getAsFloat(json, "phosphorus", 0);
        potassium = JsonHelpers.getAsFloat(json, "potassium", 0);
    }

    public float getNitrogen()
    {
        return nitrogen;
    }

    public float getPhosphorus()
    {
        return phosphorus;
    }

    public float getPotassium()
    {
        return potassium;
    }
}
