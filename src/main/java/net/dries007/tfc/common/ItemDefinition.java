package net.dries007.tfc.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

/**
 * Top level class for item-based 'definition' objects that are defined in JSON
 *
 * @see net.dries007.tfc.util.data.DataManager
 */
public class ItemDefinition
{
    protected final ResourceLocation id;
    protected final Ingredient ingredient;

    protected ItemDefinition(ResourceLocation id, JsonObject json)
    {
        this(id, CraftingHelper.getIngredient(Objects.requireNonNull(json.get("ingredient"), "Missing required field 'ingredient'")));
    }

    protected ItemDefinition(ResourceLocation id, Ingredient ingredient)
    {
        this.id = id;
        this.ingredient = ingredient;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean matches(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
