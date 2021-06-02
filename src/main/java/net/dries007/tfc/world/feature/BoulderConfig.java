/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.Codecs;

public class BoulderConfig implements IFeatureConfig
{
    public static final Codec<BoulderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(
            ResourceLocation.CODEC.comapFlatMap(r -> {
                Rock rock = RockManager.INSTANCE.get(r);
                return rock == null ? DataResult.error("No rock: " + r) : DataResult.success(rock);
            }, Rock::getId), "rock",
            Codecs.LENIENT_BLOCKSTATE.listOf(), "blocks"
        )).fieldOf("states").forGetter(c -> c.states)
    ).apply(instance, BoulderConfig::new));

    private final Map<Rock, List<BlockState>> states;

    public BoulderConfig(Map<Rock, List<BlockState>> states)
    {
        this.states = states;
    }

    public List<BlockState> getStates(Rock rock)
    {
        return states.get(rock);
    }
}