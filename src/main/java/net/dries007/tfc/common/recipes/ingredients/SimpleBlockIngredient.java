/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class SimpleBlockIngredient implements BlockIngredient
{
    private final Set<Block> blocks;

    SimpleBlockIngredient(Block block)
    {
        this.blocks = Collections.singleton(block);
    }

    SimpleBlockIngredient(Collection<Block> blocks)
    {
        this.blocks = new ObjectOpenHashSet<>(blocks);
    }

    @Override
    public boolean test(BlockState state)
    {
        return blocks.contains(state.getBlock());
    }

    @Override
    public Collection<Block> getValidBlocks()
    {
        return blocks;
    }

    @Override
    public BlockIngredient.Serializer<?> serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements BlockIngredient.Serializer<SimpleBlockIngredient>
    {
        INSTANCE;

        @Override
        public SimpleBlockIngredient fromJson(JsonObject json)
        {
            return BlockIngredients.fromJsonString(GsonHelper.getAsString(json, "block"));
        }

        @Override
        public SimpleBlockIngredient fromNetwork(FriendlyByteBuf buffer)
        {
            final int size = buffer.readVarInt();
            if (size == 1)
            {
                return new SimpleBlockIngredient(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
            }
            final Set<Block> blocks = new HashSet<>();
            for (int i = 0; i < size; i++)
            {
                blocks.add(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
            }
            return new SimpleBlockIngredient(blocks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SimpleBlockIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.blocks.size());
            for (Block block : ingredient.blocks)
            {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
            }
        }
    }
}
