/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.VisibleForTesting;

public final class ItemStackProvider
{
    private static final ItemStackModifier[] NONE = new ItemStackModifier[0];
    private static final ItemStackProvider EMPTY = new ItemStackProvider(ItemStack.EMPTY, NONE);
    private static final ItemStackProvider COPY_INPUT = new ItemStackProvider(ItemStack.EMPTY, new ItemStackModifier[] { CopyInputModifier.INSTANCE });

    public static ItemStackProvider empty()
    {
        return EMPTY;
    }

    public static ItemStackProvider copyInput()
    {
        return COPY_INPUT;
    }

    public static ItemStackProvider of(ItemStack stack, ItemStackModifier... modifiers)
    {
        return new ItemStackProvider(stack, modifiers);
    }

    public static ItemStackProvider fromJson(JsonObject json)
    {
        final ItemStack stack;
        final ItemStackModifier[] modifiers;
        final boolean hasStack = json.has("stack");
        final boolean hasMods = json.has("modifiers");
        if (hasStack || hasMods)
        {
            // Provider
            stack = hasStack ? ShapedRecipe.itemStackFromJson(JsonHelpers.getAsJsonObject(json, "stack")) : ItemStack.EMPTY;
            if (hasMods)
            {
                final JsonArray modifiersJson = JsonHelpers.getAsJsonArray(json, "modifiers");
                modifiers = new ItemStackModifier[modifiersJson.size()];
                for (int i = 0; i < modifiers.length; i++)
                {
                    modifiers[i] = ItemStackModifiers.fromJson(modifiersJson.get(i));
                }
            }
            else
            {
                modifiers = NONE;
            }
        }
        else
        {
            stack = ShapedRecipe.itemStackFromJson(json);
            modifiers = NONE;
        }
        return new ItemStackProvider(stack, modifiers);
    }

    public static ItemStackProvider fromNetwork(FriendlyByteBuf buffer)
    {
        final ItemStack stack = buffer.readItem();
        final int count = buffer.readVarInt();
        if (count == 0)
        {
            return new ItemStackProvider(stack, NONE);
        }
        final ItemStackModifier[] modifiers = new ItemStackModifier[count];
        for (int i = 0; i < count; i++)
        {
            modifiers[i] = ItemStackModifiers.fromNetwork(buffer);
        }
        return new ItemStackProvider(stack, modifiers);
    }

    private final ItemStack stack;
    private final ItemStackModifier[] modifiers;

    private ItemStackProvider(ItemStack stack, ItemStackModifier[] modifiers)
    {
        this.stack = stack;
        this.modifiers = modifiers;
    }

    public ItemStack getStack(ItemStack input)
    {
        ItemStack output = stack.copy();
        for (ItemStackModifier modifier : modifiers)
        {
            output = modifier.apply(output, input);
        }
        return output;
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeItem(stack);
        buffer.writeVarInt(modifiers.length);
        for (ItemStackModifier modifier : modifiers)
        {
            modifier.toNetwork(buffer);
        }
    }

    @VisibleForTesting
    public ItemStack stack()
    {
        return stack;
    }

    @VisibleForTesting
    public ItemStackModifier[] modifiers()
    {
        return modifiers;
    }
}
