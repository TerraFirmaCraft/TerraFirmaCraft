/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.rock.*;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.Helpers;

public class Rock
{
    private final SandBlockType desertSandColor, beachSandColor;
    private final RockCategory category;
    private final boolean naturallyGenerating;
    private final Map<BlockType, Block> blockVariants;
    private final ResourceLocation id;

    public Rock(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        String rockCategoryName = JSONUtils.getString(json, "category");
        this.category = Helpers.mapSafeOptional(() -> RockCategory.valueOf(rockCategoryName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown rock category: " + rockCategoryName));
        String desertSandColorName = JSONUtils.getString(json, "desert_sand_color");
        this.desertSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(desertSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown sand color: " + desertSandColorName));
        String beachSandColorName = JSONUtils.getString(json, "beach_sand_color");
        this.beachSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(beachSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown beach sand color: " + beachSandColorName));
        this.naturallyGenerating = JSONUtils.getAsBoolean(json, "naturally_generated", true);

        this.blockVariants = Helpers.findRegistryObjects(json, "blocks", ForgeRegistries.BLOCKS, Arrays.asList(Rock.BlockType.values()), type -> type.name().toLowerCase());
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Block getBlock(BlockType type)
    {
        return blockVariants.get(type);
    }

    public RockCategory getCategory()
    {
        return category;
    }

    public SandBlockType getDesertSandColor()
    {
        return desertSandColor;
    }

    public SandBlockType getBeachSandColor()
    {
        return beachSandColor;
    }

    public boolean isNaturallyGenerating()
    {
        return naturallyGenerating;
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

    public enum BlockType implements IStringSerializable
    {
        RAW((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        HARDENED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        SMOOTH((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        COBBLE((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        BRICKS((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        GRAVEL((rock, self) -> new Block(Block.Properties.of(Material.SAND, MaterialColor.STONE).sound(SoundType.STONE).strength(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL)), false),
        SPIKE((rock, self) -> new RockSpikeBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.4f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        CRACKED_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_COBBLE((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        CHISELED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        LOOSE((rock, self) -> new LooseRockBlock(Block.Properties.of(TFCMaterials.NON_SOLID_STONE).strength(0.05f, 0.0f).sound(SoundType.STONE).noOcclusion()), false);

        public static final BlockType[] VALUES = values();
        public static final Map<String, BlockType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(k -> k.name().toLowerCase(), v -> v));
        public static final Codec<BlockType> CODEC = IStringSerializable.fromEnum(BlockType::values, BlockType::byName);

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        public static BlockType byName(String id)
        {
            return BY_NAME.get(id);
        }

        private final boolean variants;
        private final BiFunction<Default, BlockType, Block> blockFactory;
        private final String serializedName;

        BlockType(BiFunction<Default, BlockType, Block> blockFactory, boolean variants)
        {
            this.blockFactory = blockFactory;
            this.variants = variants;
            this.serializedName = name().toLowerCase();
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
                return new MossGrowingSlabBlock(properties, TFCBlocks.ROCK_SLABS.get(rock).get(mossy()));
            }
            return new SlabBlock(properties);
        }

        public StairsBlock createStairs(Default rock)
        {
            Supplier<BlockState> state = () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(this).get().getDefaultState();
            AbstractBlock.Properties properties = AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE);
            if (mossy() == this)
            {
                return new MossSpreadingStairBlock(state, properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingStairsBlock(state, properties, TFCBlocks.ROCK_STAIRS.get(rock).get(mossy()));
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
                return new MossGrowingWallBlock(properties, TFCBlocks.ROCK_WALLS.get(rock).get(mossy()));
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