/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class TallPlantConfig implements FeatureConfiguration
{
    public static final Codec<TallPlantConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.fieldOf("body").forGetter(TallPlantConfig::getBodyState),
        Codecs.LENIENT_BLOCKSTATE.fieldOf("head").forGetter(TallPlantConfig::getBodyState),
        Codec.intRange(1, 128).fieldOf("tries").forGetter(c -> c.tries),
        Codec.intRange(1, 16).fieldOf("radius").forGetter(c -> c.radius),
        Codec.intRange(1, 100).fieldOf("minHeight").forGetter(c -> c.minHeight),
        Codec.intRange(1, 100).fieldOf("maxHeight").forGetter(c -> c.maxHeight)
    ).apply(instance, TallPlantConfig::new));

    private final BlockState bodyState;
    private final BlockState headState;
    private final int tries;
    private final int radius;
    private final int minHeight;
    private final int maxHeight;

    public TallPlantConfig(BlockState body, BlockState head, int tries, int radius, int minHeight, int maxHeight)
    {
        this.bodyState = body;
        this.headState = head;
        this.tries = tries;
        this.radius = radius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public BlockState getBodyState()
    {
        return bodyState;
    }

    public BlockState getHeadState()
    {
        return headState;
    }

    public int getTries()
    {
        return tries;
    }

    public int getRadius()
    {
        return radius;
    }

    public int getMinHeight()
    {
        return minHeight;
    }

    public int getMaxHeight()
    {
        return maxHeight;
    }

}
