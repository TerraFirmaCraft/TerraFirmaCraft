/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.objects.blocks.rock.RawRockVariantBlock;
import net.dries007.tfc.objects.blocks.rock.RockVariantBlock;
import net.dries007.tfc.world.gen.rock.RockCategory;

@ParametersAreNonnullByDefault
public class Rock extends TFCType
{
    private final RockCategory category;
    private final Map<BlockType, Block> blockVariants;

    public Rock(RockCategory category, Map<BlockType, Block> blockVariants)
    {
        this.category = category;
        this.blockVariants = blockVariants;
    }

    @Nonnull
    public Block getBlock(BlockType type)
    {
        return blockVariants.get(type);
    }

    @Nonnull
    public RockCategory getCategory()
    {
        return category;
    }

    /**
     * Default rocks that are used for block registration calls.
     * Not extensible.
     */
    public enum Default
    {
        GRANITE,
        DIORITE,
        GABBRO,
        SHALE,
        CLAYSTONE,
        ROCKSALT,
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

    /**
     * The block types that are used to create rock/type permutations
     * Extensible via addons
     */
    public enum BlockType
    {
        RAW(rock -> new RawRockVariantBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        SMOOTH(rock -> new RockVariantBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        COBBLE(rock -> new RockVariantBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        BRICKS(rock -> new RockVariantBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        GRAVEL(rock -> new RockVariantBlock(Block.Properties.create(Material.SAND, MaterialColor.STONE).sound(SoundType.STONE).hardnessAndResistance(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL)));

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
