/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.world.Codecs;

public class ChiselRecipe implements INoopInputRecipe
{
    public static final IndirectHashCollection<Block, ChiselRecipe> CACHE = IndirectHashCollection.createForRecipe(r -> r.ingredient.blocks(), TFCRecipeTypes.CHISEL);

    public static final MapCodec<ChiselRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codecs.BLOCK_STATE.fieldOf("result").forGetter(c -> c.output),
        ChiselMode.REGISTRY.byNameCodec().fieldOf("mode").forGetter(c -> c.mode),
        ItemStackProvider.CODEC.optionalFieldOf("item_output", ItemStackProvider.empty()).forGetter(c -> c.itemOutput)
    ).apply(i, ChiselRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChiselRecipe> STREAM_CODEC = StreamCodec.composite(
        BlockIngredient.STREAM_CODEC, c -> c.ingredient,
        StreamCodecs.BLOCK_STATE, c -> c.output,
        ByteBufCodecs.registry(ChiselMode.KEY), c -> c.mode,
        ItemStackProvider.STREAM_CODEC, c -> c.itemOutput,
        ChiselRecipe::new
    );


    /**
     * Attempts to chisel a block, returning the result if chiseling would occur, for the caller to handle.
     *
     * @param player The player doing the chiseling
     * @param state The initial state of the targeted block
     * @param hit The hit result on the targeted block, used for orientation of slabs and stairs
     * @param informWhy If {@code true}, this will trigger a client message for the player if the recipe fails, explaining why it failed
     * @return Either a block state to be set in the position of the input, or an interaction result with a fail/pass if the chiseling
     * was not successful.
     */
    public static Either<BlockState, InteractionResult> computeResult(Player player, BlockState state, BlockHitResult hit, boolean informWhy)
    {
        final ItemStack held = player.getMainHandItem();
        if (Helpers.isItem(held, TFCTags.Items.TOOLS_CHISEL) && Helpers.isItem(player.getOffhandItem(), TFCTags.Items.TOOLS_HAMMER))
        {
            final ChiselMode mode = IPlayerInfo.get(player).chiselMode();
            final ChiselRecipe recipe = ChiselRecipe.getRecipe(state, mode);
            if (recipe == null)
            {
                if (informWhy) complain(player, "no_recipe");
                return Either.<BlockState, InteractionResult>right(InteractionResult.PASS);
            }
            else
            {
                @Nullable BlockState chiseled = mode.modifyStateForPlacement(state, recipe.output, player, hit);

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

    @Nullable
    public static ChiselRecipe getRecipe(BlockState state, ChiselMode mode)
    {
        for (ChiselRecipe recipe : CACHE.getAll(state.getBlock()))
        {
            if (recipe.matches(state, mode))
            {
                return recipe;
            }
        }
        return null;
    }

    private final BlockIngredient ingredient;
    private final BlockState output;
    private final ChiselMode mode;
    private final ItemStackProvider itemOutput;

    public ChiselRecipe(BlockIngredient ingredient, BlockState output, ChiselMode mode, ItemStackProvider itemOutput)
    {
        this.ingredient = ingredient;
        this.output = output;
        this.mode = mode;
        this.itemOutput = itemOutput;
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries)
    {
        return new ItemStack(output.getBlock());
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

    public boolean matches(BlockState state, ChiselMode mode)
    {
        return this.mode == mode && ingredient.test(state);
    }

    public ChiselMode getMode()
    {
        return mode;
    }

    public BlockIngredient getIngredient()
    {
        return ingredient;
    }

    public ItemStack getItemOutput(ItemStack chisel)
    {
        return itemOutput.getSingleStack(chisel);
    }
}
