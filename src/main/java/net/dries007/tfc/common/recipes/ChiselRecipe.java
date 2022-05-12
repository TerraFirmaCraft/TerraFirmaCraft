/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Locale;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.events.ChiselResultEvent;
import org.jetbrains.annotations.Nullable;

public class ChiselRecipe extends SimpleBlockRecipe
{
    @Nullable
    public static BlockState computeResultWithEvent(Player player, BlockState state, BlockHitResult hit)
    {
        BlockState output = computeResult(player, state, hit);
        ChiselResultEvent event = new ChiselResultEvent(player, state, hit, output);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            return output;
        }
        return event.getOutputState();
    }

    @Nullable
    public static BlockState computeResult(Player player, BlockState state, BlockHitResult hit)
    {
        ItemStack held = player.getMainHandItem();
        if (Helpers.isItem(held, TFCTags.Items.CHISELS) && Helpers.isItem(player.getOffhandItem(), TFCTags.Items.HAMMERS))
        {
            BlockPos pos = hit.getBlockPos();
            BlockState returned = player.getCapability(PlayerDataCapability.CAPABILITY).map(cap -> {
                final Mode mode = cap.getChiselMode();
                final ChiselRecipe recipe = ChiselRecipe.getRecipe(state, held, mode);
                if (recipe != null)
                {
                    final BlockState chiseled = recipe.getBlockCraftingResult(state);
                    if (mode == Mode.SLAB || mode == Mode.STAIR)
                    {
                        BlockState placed = chiseled.getBlock().getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(chiseled.getBlock()), hit));
                        if (placed != null) return placed;
                    }
                    else
                    {
                        return chiseled;
                    }
                }
                return Blocks.AIR.defaultBlockState();
            }).orElse(null);
            // doing a fake getStateForPlacement call might mess this up, so let's give it our best shot here.
            // that said this is not possible with any of our default options
            if (returned != null && !returned.isAir())
            {
                BlockState fluidFilled = FluidHelpers.fillWithFluid(returned, player.level.getFluidState(pos).getType());
                return fluidFilled == null ? returned : fluidFilled;
            }

        }
        return null;
    }

    public static final IndirectHashCollection<Block, ChiselRecipe> CACHE = IndirectHashCollection.createForRecipe(recipe -> recipe.getBlockIngredient().getValidBlocks(), TFCRecipeTypes.CHISEL);

    @Nullable
    public static ChiselRecipe getRecipe(BlockState state, ItemStack held, Mode mode)
    {
        for (ChiselRecipe recipe : CACHE.getAll(state.getBlock()))
        {
            if (recipe.matches(state, held, mode))
            {
                return recipe;
            }
        }
        return null;
    }

    private final Mode mode;
    @Nullable
    private final Ingredient itemIngredient;
    private final ItemStackProvider extraDrop;

    public ChiselRecipe(ResourceLocation id, BlockIngredient ingredient, BlockState outputState, Mode mode, @Nullable Ingredient itemIngredient, ItemStackProvider extraDrop)
    {
        super(id, ingredient, outputState, false);
        this.mode = mode;
        this.itemIngredient = itemIngredient;
        this.extraDrop = extraDrop;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.CHISEL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.CHISEL.get();
    }

    public boolean matches(BlockState state, ItemStack stack, Mode mode)
    {
        if (itemIngredient != null && !itemIngredient.test(stack))
        {
            return false;
        }
        return mode == this.mode && matches(state);
    }

    public Mode getMode()
    {
        return mode;
    }

    @Nullable
    public Ingredient getItemIngredient()
    {
        return itemIngredient;
    }

    public ItemStack getExtraDrop(ItemStack chisel)
    {
        return extraDrop.getStack(chisel);
    }

    public static class Serializer extends RecipeSerializerImpl<ChiselRecipe>
    {
        @Override
        public ChiselRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            BlockIngredient ingredient = BlockIngredients.fromJson(JsonHelpers.get(json, "ingredient"));
            BlockState state = JsonHelpers.getBlockState(GsonHelper.getAsString(json, "result"));
            Mode mode = JsonHelpers.getEnum(json, "mode", Mode.class, Mode.SMOOTH);
            Ingredient itemIngredient = json.has("item_ingredient") ? Ingredient.fromJson(json.get("item_ingredient")) : null;
            ItemStackProvider drop = json.has("extra_drop") ? ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "extra_drop")) : ItemStackProvider.empty();
            return new ChiselRecipe(recipeId, ingredient, state, mode, itemIngredient, drop);
        }

        @Nullable
        @Override
        public ChiselRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final BlockIngredient ingredient = BlockIngredients.fromNetwork(buffer);
            final BlockState state = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS).defaultBlockState();
            final Mode mode = buffer.readEnum(Mode.class);
            final Ingredient itemIngredient = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final ItemStackProvider drop = ItemStackProvider.fromNetwork(buffer);
            return new ChiselRecipe(recipeId, ingredient, state, mode, itemIngredient, drop);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ChiselRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, recipe.outputState.getBlock());
            buffer.writeEnum(recipe.getMode());
            Helpers.encodeNullable(recipe.itemIngredient, buffer, Ingredient::toNetwork);
            recipe.extraDrop.toNetwork(buffer);
        }
    }

    public enum Mode implements StringRepresentable
    {
        SMOOTH,
        STAIR,
        SLAB;

        public static Mode valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : SMOOTH;
        }

        public static final Mode[] VALUES = values();

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }

        public Mode next()
        {
            return valueOf(ordinal() + 1);
        }
    }
}
