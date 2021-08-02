/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.rock.*;
import net.dries007.tfc.util.Helpers;

public class Rock
{
    private final Block sand, sandstone;
    private final Block raw, hardened, gravel, cobble; // Required blocks
    @Nullable
    private final Block spike, loose; // Optional blocks (only required for certain features)
    private final ResourceLocation id;

    public Rock(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.sand = Helpers.getBlockFromJson(json, "sand");
        this.sandstone = Helpers.getBlockFromJson(json, "sandstone");
        this.raw = Helpers.getBlockFromJson(json, "raw");
        this.hardened = Helpers.getBlockFromJson(json, "hardened");
        this.gravel = Helpers.getBlockFromJson(json, "gravel");
        this.cobble = Helpers.getBlockFromJson(json, "cobble");

        this.spike = json.has("spike") ? Helpers.getBlockFromJson(json, "spike") : null;
        this.loose = json.has("loose") ? Helpers.getBlockFromJson(json, "loose") : null;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Block getBlock(BlockType type)
    {
        Block block = getBlockOrNull(type);
        if (block == null)
        {
            throw new IllegalStateException("Cannot get type " + type.getSerializedName() + " from rock " + id + " as it was not present");
        }
        return block;
    }

    @Nullable
    public Block getBlockOrNull(BlockType type)
    {
        switch (type)
        {
            case RAW:
                return raw;
            case HARDENED:
                return hardened;
            case GRAVEL:
                return gravel;
            case COBBLE:
                return cobble;
            case SPIKE:
                return spike;
            case LOOSE:
                return loose;
            default:
                return null;
        }
    }

    public Block getSand()
    {
        return sand;
    }

    public Block getSandstone()
    {
        return sandstone;
    }

    /**
     * Default rocks that are used for block registration calls.
     * Not extensible.
     *
     * @see Rock instead and register via json
     */
    public enum Default implements IStringSerializable
    {
        GRANITE(RockCategory.IGNEOUS_INTRUSIVE),
        DIORITE(RockCategory.IGNEOUS_INTRUSIVE),
        GABBRO(RockCategory.IGNEOUS_INTRUSIVE),
        SHALE(RockCategory.SEDIMENTARY),
        CLAYSTONE(RockCategory.SEDIMENTARY),
        LIMESTONE(RockCategory.SEDIMENTARY),
        CONGLOMERATE(RockCategory.SEDIMENTARY),
        DOLOMITE(RockCategory.SEDIMENTARY),
        CHERT(RockCategory.SEDIMENTARY),
        CHALK(RockCategory.SEDIMENTARY),
        RHYOLITE(RockCategory.IGNEOUS_EXTRUSIVE),
        BASALT(RockCategory.IGNEOUS_EXTRUSIVE),
        ANDESITE(RockCategory.IGNEOUS_EXTRUSIVE),
        DACITE(RockCategory.IGNEOUS_EXTRUSIVE),
        QUARTZITE(RockCategory.METAMORPHIC),
        SLATE(RockCategory.METAMORPHIC),
        PHYLLITE(RockCategory.METAMORPHIC),
        SCHIST(RockCategory.METAMORPHIC),
        GNEISS(RockCategory.METAMORPHIC),
        MARBLE(RockCategory.METAMORPHIC);

        private final String serializedName;
        private final RockCategory category;

        Default(RockCategory category)
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.category = category;
        }

        public RockCategory getCategory()
        {
            return category;
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }

    public enum BlockType implements IStringSerializable
    {
        RAW((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2 + rock.category.getHardness(), 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        HARDENED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.25f + rock.category.getHardness(), 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        SMOOTH((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        COBBLE((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        BRICKS((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        GRAVEL((rock, self) -> new Block(Block.Properties.of(Material.SAND, MaterialColor.STONE).sound(SoundType.GRAVEL).strength(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL)), false),
        SPIKE((rock, self) -> new RockSpikeBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.4f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        CRACKED_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_COBBLE((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        CHISELED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        LOOSE((rock, self) -> new LooseRockBlock(Block.Properties.of(TFCMaterials.NON_SOLID_STONE).strength(0.05f, 0.0f).sound(SoundType.STONE).noOcclusion()), false),
        PRESSURE_PLATE((rock, self) -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().noCollission().strength(0.5F)), false),
        BUTTON((rock, self) -> new StoneButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F)), false);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        private final boolean variants;
        private final BiFunction<Default, BlockType, Block> blockFactory;
        private final String serializedName;

        BlockType(BiFunction<Default, BlockType, Block> blockFactory, boolean variants)
        {
            this.blockFactory = blockFactory;
            this.variants = variants;
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        /**
         * @return if this block type should be given slab, stair and wall variants
         */
        public boolean hasVariants()
        {
            return variants;
        }

        public Block create(Default rock)
        {
            return blockFactory.apply(rock, this);
        }

        public SlabBlock createSlab(Default rock)
        {
            AbstractBlock.Properties properties = AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE);
            if (mossy() == this)
            {
                return new MossSpreadingSlabBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingSlabBlock(properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy())::getSlab);
            }
            return new SlabBlock(properties);
        }

        public StairsBlock createStairs(Default rock)
        {
            Supplier<BlockState> state = () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(this).get().defaultBlockState();
            AbstractBlock.Properties properties = AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE);
            if (mossy() == this)
            {
                return new MossSpreadingStairBlock(state, properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingStairsBlock(state, properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy())::getStair);
            }
            return new StairsBlock(state, properties);
        }

        public WallBlock createWall(Default rock)
        {
            AbstractBlock.Properties properties = AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE);
            if (mossy() == this)
            {
                return new MossSpreadingWallBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingWallBlock(properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy())::getWall);
            }
            return new WallBlock(properties);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }

        @Nullable
        private BlockType mossy()
        {
            switch (this)
            {
                case COBBLE:
                case MOSSY_COBBLE:
                    return MOSSY_COBBLE;
                case BRICKS:
                case MOSSY_BRICKS:
                    return MOSSY_BRICKS;
                default:
                    return null;
            }
        }
    }
}