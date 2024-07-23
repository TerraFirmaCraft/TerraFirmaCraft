/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class WeldingRecipe implements INoopInputRecipe, IRecipePredicate<WeldingRecipe.Inventory>
{
    public static final MapCodec<WeldingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("first_input").forGetter(c -> c.firstInput),
        Ingredient.CODEC.fieldOf("second_input").forGetter(c -> c.secondInput),
        Codec.INT.optionalFieldOf("tier", -1).forGetter(c -> c.tier),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.output),
        Codec.BOOL.optionalFieldOf("apply_bonus", false).forGetter(c -> c.combineForgingBonus)
    ).apply(i, WeldingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeldingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.firstInput,
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.secondInput,
        ByteBufCodecs.VAR_INT, c -> c.tier,
        ItemStackProvider.STREAM_CODEC, c -> c.output,
        ByteBufCodecs.BOOL, c -> c.combineForgingBonus,
        WeldingRecipe::new
    );

    private final Ingredient firstInput, secondInput;
    private final int tier;
    private final ItemStackProvider output;
    private final boolean combineForgingBonus;

    public WeldingRecipe(Ingredient firstInput, Ingredient secondInput, int tier, ItemStackProvider output, boolean combineForgingBonus)
    {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.tier = tier;
        this.output = output;
        this.combineForgingBonus = combineForgingBonus;
    }

    /**
     * @return {@code true} if an anvil of {@code anvilTier} can perform this recipe.
     */
    public boolean isCorrectTier(int anvilTier)
    {
        return anvilTier >= tier;
    }

    public int getTier()
    {
        return tier;
    }

    @Override
    public boolean matches(Inventory input)
    {
        final ItemStack left = input.getLeft(), right = input.getRight();
        return (firstInput.test(left) && secondInput.test(right))
            || (firstInput.test(right) && secondInput.test(left));
    }

    public ItemStack assemble(Inventory input)
    {
        final ItemStack stack = output.getSingleStack(input.getLeft());
        if (combineForgingBonus)
        {
            final ForgingBonus left = ForgingBonus.get(input.getLeft());
            final ForgingBonus right = ForgingBonus.get(input.getRight());
            if (left.ordinal() < right.ordinal())
            {
                ForgingBonus.set(stack, left);
            }
            else
            {
                ForgingBonus.set(stack, right);
            }
        }
        return stack;
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries)
    {
        return output.getEmptyStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.WELDING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.WELDING.get();
    }

    public Ingredient getFirstInput()
    {
        return firstInput;
    }

    public Ingredient getSecondInput()
    {
        return secondInput;
    }

    public boolean shouldCombineForgingBonus()
    {
        return combineForgingBonus;
    }

    public interface Inventory extends RecipeInput
    {
        ItemStack getLeft();

        ItemStack getRight();

        int getTier();
    }
}
