/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Locale;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class ChiselRecipe extends SimpleBlockRecipe
{
    /**
     * In a sentence, this method returns "Either" a BlockState, which the caller must handle, or an InteractionResult to be returned
     */
    public static Either<BlockState, InteractionResult> computeResult(Player player, BlockState state, BlockHitResult hit, boolean informWhy)
    {
        final ItemStack held = player.getMainHandItem();
        if (Helpers.isItem(held, TFCTags.Items.CHISELS) && Helpers.isItem(player.getOffhandItem(), TFCTags.Items.HAMMERS))
        {
            final BlockPos pos = hit.getBlockPos();
            final Mode mode = PlayerData.get(player).getChiselMode();
            final ChiselRecipe recipe = ChiselRecipe.getRecipe(state, held, mode);
            if (recipe == null)
            {
                if (informWhy) complain(player, "no_recipe");
                return Either.<BlockState, InteractionResult>right(InteractionResult.PASS);
            }
            else
            {
                @Nullable BlockState chiseled = recipe.getBlockCraftingResult(state);

                // The block crafting result will be a single, simple block state, unaware of the placement context, however we want the chisel
                // to meaningfully respond similar to how slab/stair placement naturally works. For this, we have different behavior based on the
                // mode, which should handle certain edge cases:
                // 1. SMOOTH
                //    There should be no contextual placement information
                // 2. STAIR
                //    We use `getStateForPlacement`. This is NOT ACCURATE, as the block in question is querying the wrong world position for i.e. the fluid position.
                //    We can fix this, however, after the fact. Stairs also don't have any issues with placing on top of one another. Just sanity check we are only
                //    calling this method for `StairBlock`s
                // 3. SLAB
                //    Slabs run into an issue where, chiseling adjacent to a slab, with the placement context, infers it to be a double slab (wrong + duplication glitch)
                //    So, we copy the slab contextual placement and write it correctly - there's no good way to perform this simulation without modifying the world, or having
                //    a whole simulation world which I do *not* want to do.
                if (mode == Mode.STAIR && chiseled.getBlock() instanceof StairBlock stair)
                {
                    // Use the stair placement state, but fill with fluid after the fact
                    chiseled = stair.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(stair), hit));
                    if (chiseled != null)
                    {
                        chiseled = FluidHelpers.fillWithFluid(chiseled, state.getFluidState().getType());
                    }
                }
                else if (mode == Mode.SLAB && chiseled.getBlock() instanceof SlabBlock && chiseled.hasProperty(SlabBlock.TYPE))
                {
                    // Copied from SlabBlock.getStateForPlacement, but avoids placing double slabs
                    final Direction hitFace = hit.getDirection();
                    final SlabType slabType = hitFace != Direction.DOWN && (hitFace == Direction.UP || !(hit.getLocation().y - pos.getY() > 0.5D))
                        ? SlabType.BOTTOM
                        : SlabType.TOP;

                    chiseled = chiseled.setValue(SlabBlock.TYPE, slabType);
                    chiseled = FluidHelpers.fillWithFluid(chiseled, state.getFluidState().getType());
                }

                if (chiseled == null)
                {
                    if (informWhy) complain(player, "cannot_place");
                    return Either.right(InteractionResult.FAIL);
                }

                return Either.left(chiseled);
            }
        }
        return Either.right(InteractionResult.PASS);
    }

    private static void complain(Player player, String message)
    {
        player.displayClientMessage(Component.translatable("tfc.chisel." + message), true);
    }

    public static final IndirectHashCollection<Block, ChiselRecipe> CACHE = IndirectHashCollection.createForRecipe(recipe -> recipe.getBlockIngredient().blocks(), TFCRecipeTypes.CHISEL);

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
        return extraDrop.getSingleStack(chisel);
    }

    public static class Serializer extends RecipeSerializerImpl<ChiselRecipe>
    {
        @Override
        public ChiselRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            BlockIngredient ingredient = BlockIngredient.fromJson(JsonHelpers.get(json, "ingredient"));
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
            final BlockIngredient ingredient = BlockIngredient.fromNetwork(buffer);
            final BlockState state = BuiltInRegistries.BLOCK.byId(buffer.readVarInt()).defaultBlockState();
            final Mode mode = buffer.readEnum(Mode.class);
            final Ingredient itemIngredient = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final ItemStackProvider drop = ItemStackProvider.fromNetwork(buffer);
            return new ChiselRecipe(recipeId, ingredient, state, mode, itemIngredient, drop);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ChiselRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeVarInt(BuiltInRegistries.BLOCK.getId(recipe.outputState.getBlock()));
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
