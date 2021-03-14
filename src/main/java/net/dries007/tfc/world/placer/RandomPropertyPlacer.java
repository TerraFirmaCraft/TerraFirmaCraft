/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class RandomPropertyPlacer extends BlockPlacer
{
    @SuppressWarnings("deprecation")
    public static final Codec<RandomPropertyPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.nonDefaultedRegistryCodec(Registry.BLOCK).fieldOf("block").forGetter(c -> c.block),
        Codec.STRING.fieldOf("property").forGetter(c -> c.propertyName)
    ).apply(instance, RandomPropertyPlacer::new));

    private final Block block;
    private final String propertyName;
    private final BiFunction<BlockState, Random, BlockState> propertySetter;

    public RandomPropertyPlacer(Block block, String propertyName)
    {
        this.block = block;
        this.propertyName = propertyName;
        this.propertySetter = createPropertySetter(Objects.requireNonNull(block.getStateDefinition().getProperty(propertyName), "No property: " + propertyName + " found on block: " + block.getRegistryName()));
    }

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        worldIn.setBlock(pos, propertySetter.apply(state, random), 2);
    }

    @Override
    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.RANDOM_PROPERTY.get();
    }

    /**
     * Extracted into a method to it can be generic on the property type
     */
    private <T extends Comparable<T>> BiFunction<BlockState, Random, BlockState> createPropertySetter(Property<T> property)
    {
        final List<T> values = new ArrayList<>(property.getPossibleValues());
        return (state, random) -> state.setValue(property, values.get(random.nextInt(values.size())));
    }
}
