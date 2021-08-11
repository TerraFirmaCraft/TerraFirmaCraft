/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class RandomPropertyPlacer extends BlockPlacer
{
    private static final Logger LOGGER = LogManager.getLogger();
    @SuppressWarnings("deprecation")
    public static final Codec<RandomPropertyPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.nonDefaultedRegistryCodec(Registry.BLOCK).fieldOf("block").forGetter(c -> c.block),
        Codec.STRING.fieldOf("property").forGetter(c -> c.propertyName)
    ).apply(instance, RandomPropertyPlacer::create));

    private static RandomPropertyPlacer create(Block block, String propertyName)
    {
        final Property<?> property = block.getStateDefinition().getProperty(propertyName);
        if (property == null)
        {
            LOGGER.error("No property: " + propertyName + " found on block: " + block.getRegistryName() + " At 'tfc:random_property' block placer.");
            return new RandomPropertyPlacer(block, propertyName, (state, random) -> state);
        }
        return new RandomPropertyPlacer(block, propertyName, createPropertySetter(property));
    }

    /**
     * Extracted into a method to it can be generic on the property type
     */
    private static <T extends Comparable<T>> BiFunction<BlockState, Random, BlockState> createPropertySetter(Property<T> property)
    {
        final List<T> values = new ArrayList<>(property.getPossibleValues());
        return (state, random) -> state.setValue(property, values.get(random.nextInt(values.size())));
    }

    private final Block block;
    private final String propertyName;
    private final BiFunction<BlockState, Random, BlockState> propertySetter;

    public RandomPropertyPlacer(Block block, String propertyName, BiFunction<BlockState, Random, BlockState> propertySetter)
    {
        this.block = block;
        this.propertyName = propertyName;
        this.propertySetter = propertySetter;
    }

    @Override
    public void place(LevelAccessor worldIn, BlockPos pos, BlockState state, Random random)
    {
        worldIn.setBlock(pos, propertySetter.apply(state, random), 2);
    }

    @Override
    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.RANDOM_PROPERTY.get();
    }
}
