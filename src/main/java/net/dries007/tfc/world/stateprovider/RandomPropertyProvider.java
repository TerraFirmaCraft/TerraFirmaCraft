/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.stateprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class RandomPropertyProvider extends BlockStateProvider
{
    public static final Codec<RandomPropertyProvider> CODEC = RecordCodecBuilder.<RandomPropertyProvider>create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("state").forGetter(c -> c.state),
        Codec.STRING.fieldOf("property").forGetter(c -> c.propertyName)
    ).apply(instance, RandomPropertyProvider::new)); // Cannot use .comapFlatMap on dispatch codecs

    private final BlockState state;
    private final String propertyName;
    private Function<Random, BlockState> propertySetter;

    public RandomPropertyProvider(BlockState state, String propertyName)
    {
        this.state = state;
        this.propertyName = propertyName;
        this.propertySetter = random -> state;

        final Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);
        if (property != null)
        {
            propertySetter = createPropertySetter(property);
        }
    }

    /**
     * Extracted into a method to it can be generic on the property type
     */
    private <T extends Comparable<T>> Function<Random, BlockState> createPropertySetter(Property<T> property)
    {
        final List<T> values = new ArrayList<>(property.getPossibleValues());
        return random -> state.setValue(property, values.get(random.nextInt(values.size())));
    }

    @Override
    public BlockState getState(Random random, BlockPos pos)
    {
        return propertySetter.apply(random);
    }

    @Override
    protected BlockStateProviderType<?> type()
    {
        return TFCStateProviders.RANDOM_PROPERTY.get();
    }
}
