/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.common.base.Splitter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.container.SewingTableContainer;

public class SewingRecipe implements ISimpleRecipe<SewingTableContainer.Input>
{
    private static final int MAX_STITCHES = SewingTableContainer.MAX_STITCHES;

    private static Codec<String> flatCodec(int width, int height)
    {
        return Codec.string(width, width)
            .listOf(height, height)
            .xmap(list -> String.join("", list), text -> Splitter.fixedLength(width).splitToList(text));
    }

    public static final MapCodec<SewingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        flatCodec(9, 5).comapFlatMap(
            SewingRecipe::stitchesToBitset,
            bitset -> {
                final StringBuilder builder = new StringBuilder();
                for (int j = 0; j < MAX_STITCHES; j++)
                {
                    builder.append(bitFind(bitset, j) ? '#' : ' ');
                }
                return builder.toString();
            }
        ).fieldOf("stitches").forGetter(c -> c.stitches),
        flatCodec(8, 4).fieldOf("squares").forGetter(c -> c.squares),
        ItemStack.CODEC.fieldOf("result").forGetter(c -> c.result)
    ).apply(i, SewingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SewingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.stitches,
        ByteBufCodecs.STRING_UTF8, c -> c.squares,
        ItemStack.STREAM_CODEC, c -> c.result,
        SewingRecipe::new
    );

    public static SewingRecipe from(String stitches, String squares, ItemStack result)
    {
        return new SewingRecipe(stitchesToBitset(stitches).getOrThrow(), squares, result);
    }

    private static DataResult<Long> stitchesToBitset(String text)
    {
        if (text.length() != MAX_STITCHES) return DataResult.error(() -> "Must be exactly " + MAX_STITCHES + " stitches");

        long bitset = 0;
        for (int j = 0; j < MAX_STITCHES; j++)
        {
            bitset |= (text.charAt(j) != ' ' ? (1L << j) : 0);
        }
        return DataResult.success(bitset);
    }

    private static boolean bitFind(long bitset, int index)
    {
        return ((bitset >> index) & 1) == 1;
    }

    private final long stitches; // A bitset of 45 total stitches, where 0 = false, 1 = true
    private final String squares; // A string of 32 total squares (characters), where ' ' = none, 'B' = burlap, 'N' = normal

    private final ItemStack result;

    public SewingRecipe(long stitches, String squares, ItemStack result)
    {
        this.stitches = stitches;
        this.squares = squares;
        this.result = result;
    }

    public boolean getStitch(int index)
    {
        return bitFind(stitches, index);
    }

    public int getSquare(int index)
    {
        return squares.charAt(index) - '0';
    }

    @Override
    public boolean matches(SewingTableContainer.Input inventory, Level level)
    {
        return inventory.squaresMatch(this) && inventory.stitchesMatch(this);
    }

    @Override
    public ItemStack assemble(SewingTableContainer.Input input, HolderLookup.Provider registries)
    {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SEWING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.SEWING.get();
    }
}
