/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.objects.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.util.IResourceNameable;

@ParametersAreNonnullByDefault
public class Rock implements IResourceNameable
{
    private final RockCategory category;
    private final Map<BlockType, Block> blockVariants;
    private ResourceLocation id;

    public Rock(RockCategory category, Map<BlockType, Block> blockVariants)
    {
        this.category = category;
        this.blockVariants = blockVariants;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public void setId(ResourceLocation id)
    {
        this.id = id;
    }

    public Block getBlock(BlockType type)
    {
        return blockVariants.get(type);
    }

    public RockCategory getCategory()
    {
        return category;
    }

    /**
     * Default rocks that are used for block registration calls.
     * Not extensible.
     *
     * @see Rock instead and register via json
     */
    public enum Default
    {
        GRANITE,
        DIORITE,
        GABBRO,
        SHALE,
        CLAYSTONE,
        LIMESTONE,
        CONGLOMERATE,
        DOLOMITE,
        CHERT,
        CHALK,
        RHYOLITE,
        BASALT,
        ANDESITE,
        DACITE,
        QUARTZITE,
        SLATE,
        PHYLLITE,
        SCHIST,
        GNEISS,
        MARBLE,
    }

    public enum BlockType
    {
        RAW(rock -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        SMOOTH(rock -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        COBBLE(rock -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        BRICKS(rock -> new Block(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        GRAVEL(rock -> new Block(Block.Properties.create(Material.SAND, MaterialColor.STONE).sound(SoundType.STONE).hardnessAndResistance(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL))),
        SPIKE(rock -> new RockSpikeBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.4f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)));

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        private final NonNullFunction<Default, Block> blockFactory;

        BlockType(NonNullFunction<Default, Block> blockFactory)
        {
            this.blockFactory = blockFactory;
        }

        @Nonnull
        public Block create(Default rock)
        {
            return blockFactory.apply(rock);
        }
    }
}
