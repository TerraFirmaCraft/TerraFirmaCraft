/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class RandomPropertyConfig implements FeatureConfiguration
{
    public static final Codec<RandomPropertyConfig> CODEC = RecordCodecBuilder.<RandomPropertyConfig>create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("state").forGetter(c -> c.state),
        Codec.STRING.fieldOf("property").forGetter(c -> c.propertyName)
    ).apply(instance, RandomPropertyConfig::new)).comapFlatMap(RandomPropertyConfig::guardPropertySetter, Function.identity());

    private final BlockState state;
    private final String propertyName;
    private Function<Random, BlockState> propertySetter;

    public RandomPropertyConfig(BlockState state, String propertyName)
    {
        this.state = state;
        this.propertyName = propertyName;
        this.propertySetter = random -> state;
    }

    public DataResult<RandomPropertyConfig> guardPropertySetter()
    {
        final Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);
        if (property == null)
        {
            return DataResult.error("No property: " + propertyName + " found on block: " + state.getBlock().getRegistryName());
        }
        propertySetter = createPropertySetter(property);
        return DataResult.success(this);
    }

    public BlockState state(Random random)
    {
        return propertySetter.apply(random);
    }

    /**
     * Extracted into a method to it can be generic on the property type
     */
    private <T extends Comparable<T>> Function<Random, BlockState> createPropertySetter(Property<T> property)
    {
        final List<T> values = new ArrayList<>(property.getPossibleValues());
        return random -> state.setValue(property, values.get(random.nextInt(values.size())));
    }
}
