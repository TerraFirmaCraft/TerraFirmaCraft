/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class InstantBarrelRecipe extends BarrelRecipe
{
    public InstantBarrelRecipe(ResourceLocation id, Builder builder)
    {
        super(id, builder);
    }

    @Override
    public boolean matches(BarrelBlockEntity.BarrelInventory container, @Nullable Level level)
    {
        // Instant recipes change behavior depending on the fluid content. If the recipe has no input fluid, or has no output fluid, it behaves as normal.
        // Otherwise, it must have enough input items to fully consume all input fluid.
        return super.matches(container, level) && (inputFluid.amount() == 0 || outputFluid.getAmount() == 0 || container.getFluidInTank(0).getAmount() / this.inputFluid.amount() <= container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM).getCount() / this.inputItem.count());
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
        public InstantBarrelRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Builder builder = Builder.fromJson(json);
            return new InstantBarrelRecipe(recipeId, builder);
        }

        @Nullable
        @Override
        public InstantBarrelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Builder builder = Builder.fromNetwork(buffer);
            return new InstantBarrelRecipe(recipeId, builder);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, InstantBarrelRecipe recipe)
        {
            Builder.toNetwork(recipe, buffer);
        }
    }
}
