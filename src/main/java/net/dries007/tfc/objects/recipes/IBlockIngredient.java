/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;

/**
 * This is a simple predicate wrapper for block states.
 * It can compare a single or multiple blocks, or a tag.
 */
public interface IBlockIngredient extends Predicate<BlockState>
{
    IBlockIngredient EMPTY = new IBlockIngredient()
    {
        @Override
        public boolean test(BlockState blockState)
        {
            return false;
        }

        @Override
        public Collection<Block> getValidBlocks()
        {
            return Collections.emptyList();
        }
    };

    /**
     * Test if the specified block state is accepted by the ingredient
     */
    @Override
    boolean test(BlockState blockState);

    /**
     * Return a list of all possible blocks that can be accepted by the ingredient.
     * This is mostly for populating visual lists of recipes and does not obey the exact nature of the ingredient.
     */
    Collection<Block> getValidBlocks();

    enum Serializer implements JsonDeserializer<IBlockIngredient>
    {
        INSTANCE;

        @Override
        public IBlockIngredient deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return read(element);
        }

        public IBlockIngredient read(JsonElement element)
        {
            if (element.isJsonArray())
            {
                List<IBlockIngredient> subIngredients = new ArrayList<>();
                for (JsonElement subElement : element.getAsJsonArray())
                {
                    subIngredients.add(read(subElement));
                }
                // Lazy initialize because tags aren't ready to be resolved yet
                Lazy<Collection<Block>> lazyBlockCollection = Lazy.of(() -> subIngredients.stream().flatMap(i -> i.getValidBlocks().stream()).collect(Collectors.toSet()));
                return new IBlockIngredient()
                {
                    @Override
                    public boolean test(BlockState blockState)
                    {
                        for (IBlockIngredient ingredient : subIngredients)
                        {
                            if (ingredient.test(blockState))
                            {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public Collection<Block> getValidBlocks()
                    {
                        return lazyBlockCollection.get();
                    }
                };
            }
            else if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("tag") && obj.has("block"))
                {
                    throw new JsonParseException("Block ingredient cannot be both tag and block");
                }
                else if (obj.has("block"))
                {
                    return createSingle(JSONUtils.getString(obj, "block"));
                }
                else if (obj.has("tag"))
                {
                    return createTag(JSONUtils.getString(obj, "tag"));
                }
                else
                {
                    throw new JsonParseException("Block ingredient must be either tag or block");
                }
            }
            else
            {
                String value = element.getAsString();
                return createSingle(value);
            }
        }

        /**
         * This is not a direct read, it only populates the block list
         */
        public IBlockIngredient read(PacketBuffer buffer)
        {
            int amount = buffer.readVarInt();
            List<Block> validBlocks = new ArrayList<>();
            for (int i = 0; i < amount; i++)
            {
                validBlocks.add(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
            }
            return new IBlockIngredient()
            {
                @Override
                public boolean test(BlockState blockState)
                {
                    return false;
                }

                @Override
                public Collection<Block> getValidBlocks()
                {
                    return validBlocks;
                }
            };
        }

        public void write(PacketBuffer buffer, IBlockIngredient ingredient)
        {
            Collection<Block> validBlocks = ingredient.getValidBlocks();
            buffer.writeVarInt(validBlocks.size());
            for (Block block : validBlocks)
            {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
            }
        }

        private IBlockIngredient createSingle(String blockName) throws JsonParseException
        {
            BlockStateParser parser = Helpers.parseBlockState(blockName, false);
            if (parser.getState() == null)
            {
                throw new JsonParseException("Unable to parse block state");
            }
            Block block = parser.getState().getBlock();
            List<Block> blockList = Collections.singletonList(block);
            if (parser.getProperties().isEmpty())
            {
                return new IBlockIngredient()
                {
                    @Override
                    public boolean test(BlockState blockState)
                    {
                        return blockState.getBlock() == block;
                    }

                    @Override
                    public Collection<Block> getValidBlocks()
                    {
                        return blockList;
                    }
                };
            }
            else
            {
                return new IBlockIngredient()
                {
                    @Override
                    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
                    public boolean test(BlockState stateIn)
                    {
                        if (stateIn.getBlock() == block)
                        {
                            for (Map.Entry<IProperty<?>, Comparable<?>> entry : parser.getProperties().entrySet())
                            {
                                if (!stateIn.get(entry.getKey()).equals(entry.getValue()))
                                {
                                    return false;
                                }
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public Collection<Block> getValidBlocks()
                    {
                        return blockList;
                    }
                };
            }
        }

        private IBlockIngredient createTag(String tagName) throws JsonParseException
        {
            Tag<Block> tag = BlockTags.getCollection().get(new ResourceLocation(tagName));
            if (tag != null)
            {
                return new IBlockIngredient()
                {
                    @Override
                    public boolean test(BlockState blockState)
                    {
                        return tag.contains(blockState.getBlock());
                    }

                    @Override
                    public Collection<Block> getValidBlocks()
                    {
                        return tag.getAllElements();
                    }
                };
            }
            else
            {
                throw new JsonParseException("Unknown tag: " + tagName);
            }
        }
    }
}
