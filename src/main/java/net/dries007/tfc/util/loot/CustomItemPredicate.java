/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.Metal;
import org.jetbrains.annotations.Nullable;

public class CustomItemPredicate extends ItemPredicate
{
    public static void registerCustomItemPredicates()
    {
        create("metal_tool", CustomItemPredicate::metalTool);
        create("metal_item", CustomItemPredicate::metalItem);
    }

    public static void create(String name, Function<JsonObject, Predicate<ItemStack>> deserializer)
    {
        ItemPredicate.register(Helpers.identifier(name), json -> new CustomItemPredicate(deserializer.apply(json)));
    }

    private static Predicate<ItemStack> metalTool(JsonObject json)
    {
        return stack -> {
            final Metal metal = metalFromItem(stack);
            return metal != null && metal == metalFromJson(json) && stack.getItem() instanceof TieredItem;
        };
    }

    private static Predicate<ItemStack> metalItem(JsonObject json)
    {
        return stack -> {
            final Metal metal = metalFromItem(stack);
            final int tier = JsonHelpers.getAsInt(json, "tier", -1);
            if (tier != -1)
            {
                return metal != null && metal.getTier() == tier;
            }
            return metal != null && metal == metalFromJson(json);
        };
    }

    @Nullable
    public static Metal metalFromItem(ItemStack stack)
    {
        ItemStackInventory wrapper = new ItemStackInventory(stack);
        HeatingRecipe recipe = HeatingRecipe.getRecipe(wrapper);
        if (recipe != null)
        {
            final FluidStack fluid = recipe.getOutputFluid();
            if (!fluid.isEmpty())
            {
                return Metal.get(fluid.getFluid());
            }
        }
        return null;
    }

    @Nullable
    public static Metal metalFromJson(JsonObject json)
    {
        if (json.has("metal"))
        {
            ResourceLocation metalLocation = new ResourceLocation(JsonHelpers.getAsString(json, "metal"));
            Metal metal = Metal.MANAGER.get(metalLocation);
            if (metal != null)
            {
                return metal;
            }
            TerraFirmaCraft.LOGGER.error("Not a metal: " + metalLocation);
            return null;
        }
        TerraFirmaCraft.LOGGER.error("Expected a key 'metal', found: " + json);
        return null;
    }

    private final Predicate<ItemStack> predicate;

    public CustomItemPredicate(Predicate<ItemStack> predicate)
    {
        this.predicate = predicate;
    }

    @Override
    public boolean matches(ItemStack stack)
    {
        return predicate.test(stack);
    }
}
