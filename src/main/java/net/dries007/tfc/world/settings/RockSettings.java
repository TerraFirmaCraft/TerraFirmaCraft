package net.dries007.tfc.world.settings;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.world.Codecs;

public record RockSettings(ResourceLocation id, Block raw, Block hardened, Block gravel, Block cobble, Block sand, Block sandstone, Optional<Block> spike, Optional<Block> loose, boolean topLayer, boolean middleLayer, boolean bottomLayer)
{
    public static final Codec<RockSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(c -> c.id),
        Codecs.BLOCK.fieldOf("raw").forGetter(c -> c.raw),
        Codecs.BLOCK.fieldOf("hardened").forGetter(c -> c.hardened),
        Codecs.BLOCK.fieldOf("gravel").forGetter(c -> c.gravel),
        Codecs.BLOCK.fieldOf("cobble").forGetter(c -> c.cobble),
        Codecs.BLOCK.fieldOf("sand").forGetter(c -> c.sand),
        Codecs.BLOCK.fieldOf("sandstone").forGetter(c -> c.sandstone),
        Codecs.BLOCK.optionalFieldOf("spike").forGetter(c -> c.spike),
        Codecs.BLOCK.optionalFieldOf("loose").forGetter(c -> c.loose),
        Codec.BOOL.fieldOf("top_layer").forGetter(c -> c.topLayer),
        Codec.BOOL.fieldOf("middle_layer").forGetter(c -> c.middleLayer),
        Codec.BOOL.fieldOf("bottom_layer").forGetter(c -> c.bottomLayer)
    ).apply(instance, RockSettings::new));

    public Block get(Rock.BlockType type)
    {
        return switch (type)
            {
                case RAW -> raw;
                case HARDENED -> hardened;
                case GRAVEL -> gravel;
                case COBBLE -> cobble;
                case SPIKE -> spike.orElseThrow();
                case LOOSE -> loose.orElseThrow();
                default -> throw new IllegalArgumentException("Type " + type + " does not define a block");
            };
    }
}
