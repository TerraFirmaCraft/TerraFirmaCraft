package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import org.jetbrains.annotations.Nullable;

public class InstantBarrelRecipe extends BarrelRecipe
{
    public InstantBarrelRecipe(ResourceLocation id, ItemStackIngredient inputItem, FluidStackIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid)
    {
        super(id, inputItem, inputFluid, outputItem, outputFluid);
    }

    @Override
    public boolean matches(BarrelBlockEntity.BarrelInventory container, @Nullable Level level)
    {
        // Instant recipes must have enough input items to convert fully, all the fluid. Excess items will get placed into the overflow
        return super.matches(container, level) && (inputFluid.amount() == 0 || container.getFluidInTank(0).getAmount() / this.inputFluid.amount() <= container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM).getCount() / this.inputItem.count());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.INSTANT_BARREL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BARREL_INSTANT.get();
    }

    public static class Serializer extends RecipeSerializerImpl<InstantBarrelRecipe>
    {
        @Override
        public InstantBarrelRecipe fromJson(ResourceLocation recipeId_, JsonObject serializedRecipe_)
        {
            return null;
        }

        @Nullable
        @Override
        public InstantBarrelRecipe fromNetwork(ResourceLocation recipeId_, FriendlyByteBuf buffer_)
        {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer_, InstantBarrelRecipe recipe_)
        {

        }
    }
}
