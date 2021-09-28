package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.util.JsonHelpers;

/**
 * An ingredient for a (fluid, amount) pair.
 * Used in conjunction with recipes that accept {@link FluidStack}s.
 */
public final class FluidStackIngredient implements Predicate<FluidStack>
{
    public static FluidStackIngredient fromJson(JsonObject json)
    {
        return new FluidStackIngredient(json);
    }

    public static FluidStackIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        return new FluidStackIngredient(buffer);
    }

    public static void toNetwork(FriendlyByteBuf buffer, FluidStackIngredient ingredient)
    {
        FluidIngredient.toNetwork(buffer, ingredient.fluid);
        buffer.writeVarInt(ingredient.amount);
    }

    private final FluidIngredient fluid;
    private final int amount;

    private FluidStackIngredient(JsonObject json)
    {
        this.fluid = FluidIngredient.fromJson(JsonHelpers.get(json, "fluid"));
        this.amount = JsonHelpers.getAsInt(json, "amount", FluidAttributes.BUCKET_VOLUME);
    }

    private FluidStackIngredient(FriendlyByteBuf buffer)
    {
        this.fluid = FluidIngredient.fromNetwork(buffer);
        this.amount = buffer.readVarInt();
    }

    @Override
    public boolean test(FluidStack stack)
    {
        return stack.getAmount() >= amount && fluid.test(stack.getFluid());
    }

    public Collection<Fluid> getMatchingFluids()
    {
        return fluid.getMatchingFluids();
    }
}
