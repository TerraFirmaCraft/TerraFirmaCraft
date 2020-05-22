/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.Metal;
import net.dries007.tfc.objects.types.MetalManager;

/**
 * Handles mapping item stacks -> metals
 *
 * @see Metal
 * @see MetalManager
 */
public class MetalItemRecipe implements ISimpleRecipe<ItemStackRecipeWrapper>
{
    public static Optional<MetalItem> getMetalItem(World world, ItemStack stack)
    {
        ItemStackRecipeWrapper wrapper = new ItemStackRecipeWrapper(stack);
        return RecipeCache.INSTANCE.get(TFCRecipeTypes.METAL_ITEM, world, wrapper)
            .map(recipe -> new MetalItem(recipe.getMetal(), recipe.getAmount()));
    }

    public static void addTooltipInfo(World world, ItemStack stack, List<ITextComponent> text)
    {
        getMetalItem(world, stack).ifPresent(metalItem -> {
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.metal", metalItem.getMetal().getDisplayName()));
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.units", metalItem.getAmount()));
            text.add(metalItem.getMetal().getTier().getDisplayName());
        });
    }

    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final ResourceLocation metal; // Have to store the resource location because at load time metals are not accessible yet
    protected final int amount;

    public MetalItemRecipe(ResourceLocation id, Ingredient ingredient, ResourceLocation metal, int amount)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.metal = metal;
        this.amount = amount;
    }

    public Metal getMetal()
    {
        return MetalManager.INSTANCE.getOrDefault(metal);
    }

    public int getAmount()
    {
        return amount;
    }

    @Override
    public boolean matches(ItemStackRecipeWrapper recipeWrapper, World world)
    {
        return ingredient.test(recipeWrapper.getStack());
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public ItemStack getCraftingResult(ItemStackRecipeWrapper inv)
    {
        return inv.getStack().copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.METAL_ITEM.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.METAL_ITEM;
    }

    /**
     * Wraps Metal and smelt amount for convenience
     */
    public static class MetalItem
    {
        private final Metal metal;
        private final int amount;

        public MetalItem(Metal metal, int amount)
        {
            this.metal = metal;
            this.amount = amount;
        }

        public Metal getMetal()
        {
            return metal;
        }

        public int getAmount()
        {
            return amount;
        }
    }

    public static class Serializer extends RecipeSerializer<MetalItemRecipe>
    {
        @Override
        public MetalItemRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            Ingredient ingredient = CraftingHelper.getIngredient(JSONUtils.getJsonObject(json, "ingredient"));
            ResourceLocation metal = new ResourceLocation(JSONUtils.getString(json, "metal"));
            int amount = JSONUtils.getInt(json, "amount");
            return new MetalItemRecipe(recipeId, ingredient, metal, amount);
        }

        @Nullable
        @Override
        public MetalItemRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            return new MetalItemRecipe(recipeId, Ingredient.read(buffer), buffer.readResourceLocation(), buffer.readInt());
        }

        @Override
        public void write(PacketBuffer buffer, MetalItemRecipe recipe)
        {
            recipe.ingredient.write(buffer);
            buffer.writeResourceLocation(recipe.metal);
            buffer.writeInt(recipe.amount);
        }
    }
}
