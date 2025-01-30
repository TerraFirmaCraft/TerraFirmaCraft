/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.settings;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public record RockSettings(Block raw, Block hardened, Block gravel, Block cobble, Block sand, Block sandstone, Optional<Block> spike, Optional<Block> loose, Optional<Block> mossyLoose, Optional<Boolean> karst)
{
    public static final Codec<RockSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK.fieldOf("raw").forGetter(c -> c.raw),
        Codecs.BLOCK.fieldOf("hardened").forGetter(c -> c.hardened),
        Codecs.BLOCK.fieldOf("gravel").forGetter(c -> c.gravel),
        Codecs.BLOCK.fieldOf("cobble").forGetter(c -> c.cobble),
        Codecs.BLOCK.fieldOf("sand").forGetter(c -> c.sand),
        Codecs.BLOCK.fieldOf("sandstone").forGetter(c -> c.sandstone),
        Codecs.BLOCK.optionalFieldOf("spike").forGetter(c -> c.spike),
        Codecs.BLOCK.optionalFieldOf("loose").forGetter(c -> c.loose),
        Codecs.BLOCK.optionalFieldOf("mossy_loose").forGetter(c -> c.mossyLoose),
        Codec.BOOL.optionalFieldOf("karst").forGetter(c -> c.karst)
    ).apply(instance, RockSettings::new));

    public boolean isRawOrHardened(BlockState state)
    {
        return Helpers.isBlock(state, raw()) || Helpers.isBlock(state, hardened());
    }
}
