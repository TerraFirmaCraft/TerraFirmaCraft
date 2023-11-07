/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Map;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.stateprovider.SpecialRootPlacer;

public record RootConfig(Map<Block, IWeighted<BlockState>> blocks, int width, int height, int tries, Optional<SpecialRootPlacer> specialPlacer, boolean required)
{
    public static final Codec<RootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_TO_WEIGHTED_BLOCKSTATE.fieldOf("blocks").forGetter(c -> c.blocks),
        Codecs.POSITIVE_INT.optionalFieldOf("width", 4).forGetter(c -> c.width),
        Codecs.POSITIVE_INT.optionalFieldOf("height", 3).forGetter(c -> c.height),
        Codecs.POSITIVE_INT.optionalFieldOf("tries", 5).forGetter(c -> c.tries),
        SpecialRootPlacer.CODEC.optionalFieldOf("special_placer").forGetter(c -> c.specialPlacer),
        Codecs.optionalFieldOf(Codec.BOOL, "required", false).forGetter(c -> c.required)
    ).apply(instance, RootConfig::new));
}
