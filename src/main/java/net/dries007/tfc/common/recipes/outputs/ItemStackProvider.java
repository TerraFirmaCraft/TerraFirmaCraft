/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public record ItemStackProvider(Supplier<ItemStack> stack, ItemStackModifier[] modifiers)
{
    private static final ItemStackModifier[] NONE = new ItemStackModifier[0];
    private static final ItemStackProvider EMPTY = new ItemStackProvider(ItemStack.EMPTY, NONE);
    private static final ItemStackProvider COPY_INPUT = new ItemStackProvider(ItemStack.EMPTY, new ItemStackModifier[] {CopyInputModifier.INSTANCE});

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

    public ItemStackProvider(ItemStack stack, ItemStackModifier[] modifiers)
    {
        this(FoodCapability.createNonDecayingStack(stack), modifiers);
    }

    /**
     * Gets the output stack for this provider, for the given input stack, assuming the input is a single item.
     *
     * @param input The input stack. <strong>Important:</strong> the input stack will be treated as if it has count = 1.
     * @return A new stack, independent of the input stack size.
     */
    public ItemStack getSingleStack(ItemStack input)
    {
        return getStack(Helpers.copyWithSize(input, 1));
    }

    /**
     * Gets the output stack from this provider, without taking into consideration the input
     *
     * @return A new stack, possibly invalid if the provider is dependent on the input stack.
     */
    public ItemStack getEmptyStack()
    {
        return getStack(ItemStack.EMPTY);
    }

    /**
     * Gets the output stack from this provider, for the given input stack.
     *
     * @param input The input stack. <strong>Important:</strong> The input stack will be treated as an entire stack, including count, and the returned stack may be the same count as the input due to the presence of {@link CopyInputModifier}s. If this behavior is not desired, use {@link #getSingleStack(ItemStack)}.
     * @return A new stack, possibly dependent on the input stack size.
     */
    public ItemStack getStack(ItemStack input)
    {
        ItemStack output = stack.get().copy();
        for (ItemStackModifier modifier : modifiers)
        {
            output = modifier.apply(output, input);
        }
        return output;
    }

    /**
     * @return {@code true} if the item stack provider is dependent on it's input in such a way that would render the output meaningless without proper inputs.
     */
    public boolean dependsOnInput()
    {
        for (ItemStackModifier modifier : modifiers)
        {
            if (modifier.dependsOnInput())
            {
                return true;
            }
        }
        return false;
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeItem(stack.get());
        buffer.writeVarInt(modifiers.length);
        for (ItemStackModifier modifier : modifiers)
        {
            modifier.toNetwork(buffer);
        }
    }
}
