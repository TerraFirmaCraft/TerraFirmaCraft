/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.List;
import java.util.function.Function;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

public record ItemStackProvider(
    ItemStack stack,
    List<ItemStackModifier> modifiers
) {
    public static final Codec<ItemStackProvider> CODEC = Codec.either(
        RecordCodecBuilder.<ItemStackProvider>create(i -> i.group(
            ItemStack.CODEC.optionalFieldOf("stack", ItemStack.EMPTY).forGetter(c -> c.stack),
            ItemStackModifier.CODEC.listOf().fieldOf("modifiers").forGetter(c -> c.modifiers)
        ).apply(i, ItemStackProvider::new)),
        ItemStack.CODEC
    ).xmap(
        e -> e.map(Function.identity(), ItemStackProvider::of),
        provider -> provider.modifiers.isEmpty() ? Either.right(provider.stack) : Either.left(provider)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackProvider> STREAM_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC, c -> c.stack,
        ItemStackModifier.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.modifiers,
        ItemStackProvider::of
    );

    private static final ItemStackProvider EMPTY = of(ItemStack.EMPTY);
    private static final ItemStackProvider COPY_INPUT = of(ItemStack.EMPTY, CopyInputModifier.INSTANCE);

    public static ItemStackProvider empty()
    {
        return EMPTY;
    }

    public static ItemStackProvider copyInput()
    {
        return COPY_INPUT;
    }

    public static ItemStackProvider of(ItemLike item)
    {
        return of(new ItemStack(item));
    }

    public static ItemStackProvider of(ItemLike item, int count)
    {
        return of(new ItemStack(item, count));
    }

    public static ItemStackProvider of(ItemStack stack)
    {
        return of(stack, List.of());
    }

    public static ItemStackProvider of(ItemStack stack, ItemStackModifier... modifiers)
    {
        return of(stack, List.of(modifiers));
    }

    public static ItemStackProvider of(ItemStackModifier... modifiers)
    {
        return of(ItemStack.EMPTY, List.of(modifiers));
    }

    public static ItemStackProvider of(ItemStack stack, List<ItemStackModifier> modifiers)
    {
        return new ItemStackProvider(FoodCapability.setNonDecaying(stack), modifiers);
    }

    /**
     * Gets the output stack for this provider, for the given input stack, assuming the input is a single item.
     *
     * @param input The input stack. <strong>Important:</strong> the input stack will be treated as if it has count = 1.
     * @return A new stack, independent of the input stack size.
     */
    public ItemStack getSingleStack(ItemStack input)
    {
        return getStack(input.copyWithCount(1));
    }

    /**
     * Gets the output stack from this provider, without taking into consideration the input
     *
     * @return A new stack, possibly invalid if the provider is dependent on the input stack.
     */
    public ItemStack getEmptyStack()
    {
        return getStack(ItemStack.EMPTY, ItemStackModifier.Context.NO_RANDOM_CHANCE);
    }

    /**
     * Gets the output stack from this provider, for the given input stack.
     *
     * @param input The input stack. <strong>Important:</strong> The input stack will be treated as an entire stack, including count, and the
     *              returned stack may be the same count as the input due to the presence of {@link CopyInputModifier}s. If this behavior is
     *              not desired, use {@link #getSingleStack(ItemStack)}.
     * @return A new stack, possibly dependent on the input stack size.
     */
    public ItemStack getStack(ItemStack input)
    {
        return getStack(input, ItemStackModifier.Context.DEFAULT);
    }

    /**
     * Gets the output stack from this provider, for the given input stack, and allows providing a non-typical
     * context to the provider.
     * @return A new stack, possibly dependent on the input stack
     */
    public ItemStack getStack(ItemStack input, ItemStackModifier.Context context)
    {
        ItemStack output = stack.copy();
        for (ItemStackModifier modifier : modifiers)
        {
            output = modifier.apply(output, input, context);
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
}
