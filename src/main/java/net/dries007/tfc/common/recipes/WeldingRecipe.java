/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Locale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.forge.ForgingBonusComponent;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.network.StreamCodecs;

public class WeldingRecipe implements INoopInputRecipe, IRecipePredicate<WeldingRecipe.Inventory>
{
    public static final MapCodec<WeldingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("first_input").forGetter(c -> c.firstInput),
        Ingredient.CODEC.fieldOf("second_input").forGetter(c -> c.secondInput),
        Codec.INT.optionalFieldOf("tier", -1).forGetter(c -> c.tier),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.output),
        Behavior.CODEC.optionalFieldOf("bonus", Behavior.IGNORE).forGetter(c -> c.bonus)
    ).apply(i, WeldingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeldingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.firstInput,
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.secondInput,
        ByteBufCodecs.VAR_INT, c -> c.tier,
        ItemStackProvider.STREAM_CODEC, c -> c.output,
        Behavior.STREAM_CODEC, c -> c.bonus,
        WeldingRecipe::new
    );

    private final Ingredient firstInput, secondInput;
    private final int tier;
    private final ItemStackProvider output;
    private final Behavior bonus;

    public WeldingRecipe(Ingredient firstInput, Ingredient secondInput, int tier, ItemStackProvider output, Behavior bonus)
    {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.tier = tier;
        this.output = output;
        this.bonus = bonus;
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
        if (bonus != Behavior.IGNORE)
        {
            // Compare the two bonuses and copy the actual component that we want
            // This preserves the author of the actual bonus - not the author of the welding
            // If both bonuses are identical, we arbitrarily pick one author (including potentially no author)
            //
            // This makes the most sense imo, other options are use the welding author, or drop the author entirely
            // This is a flavor addition, it's fine.
            final ForgingBonus left = ForgingBonusComponent.get(input.getLeft());
            final ForgingBonus right = ForgingBonusComponent.get(input.getRight());

            final boolean leftIsHigher = left.ordinal() > right.ordinal();
            final boolean copyHigher = bonus == Behavior.COPY_BEST;

            ForgingBonusComponent.copy(leftIsHigher == copyHigher ? input.getLeft() : input.getRight(), stack);
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

    public interface Inventory extends RecipeInput
    {
        ItemStack getLeft();

        ItemStack getRight();

        int getTier();
    }


    public enum Behavior implements StringRepresentable
    {
        COPY_BEST, // Copy the best of both inputs. If one input has no bonus, the other will be copied
        COPY_WORST, // Copy the worst of both inputs. If one input has no bonus, no bonus will be present
        IGNORE; // No bonus will be present on the output item

        public static final Codec<Behavior> CODEC = StringRepresentable.fromEnum(Behavior::values);
        public static final StreamCodec<ByteBuf, Behavior> STREAM_CODEC = StreamCodecs.forEnum(Behavior::values);

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
