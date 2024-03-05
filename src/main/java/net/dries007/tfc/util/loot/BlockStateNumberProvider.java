/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;


import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public class BlockStateNumberProvider implements NumberProvider
{
    private final Block block;
    private final Property<? extends Number> property;

    public BlockStateNumberProvider(Block block, Property<? extends Number> property)
    {
        this.block = block;
        this.property = property;
    }

    @Override
    public float getFloat(LootContext context)
    {
        final BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);

        if (state != null)
        {
            return state.getValue(property).floatValue();
        }

        return 0;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return TFCLoot.BLOCK_STATE.get();
    }

    public record Serializer(BiFunction<Block, Property<? extends Number>, BlockStateNumberProvider> factory) implements net.minecraft.world.level.storage.loot.Serializer<BlockStateNumberProvider>
    {

        @Override
        public void serialize(JsonObject json, BlockStateNumberProvider value, JsonSerializationContext context)
        {
            json.addProperty("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(value.block)).toString());
            json.addProperty("property", value.property.getName());
        }

        @Override
        public BlockStateNumberProvider deserialize(JsonObject json, JsonDeserializationContext context)
        {
            final ResourceLocation blockLocation = new ResourceLocation(GsonHelper.getAsString(json, "block"));
            final Block block = ForgeRegistries.BLOCKS.getDelegateOrThrow(blockLocation).get();

            final String propertyName = GsonHelper.getAsString(json, "property");
            final Property<?> property = block.getStateDefinition().getProperty(propertyName);
            if (property == null)
            {
                throw new JsonSyntaxException("Block " + block + " has no property " + propertyName);
            }

            if (!Number.class.isAssignableFrom(property.getValueClass()))
            {
                throw new JsonSyntaxException("Property " + propertyName + " of block " + block + " is not numeric");
            }

            // We check this above, but IDEA doesn't realize the getValueClass actually checks type
            //noinspection unchecked
            return factory.apply(block, (Property<? extends Number>) property);
        }
    }
}
