/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.dries007.tfc.common.blocks.rock.PebbleBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.rock.RawRockBlock;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.Helpers;

public class Rock
{
    private final SandBlockType desertSandColor, beachSandColor;
    private final RockCategory category;
    private final Map<BlockType, Block> blockVariants;
    private final ResourceLocation id;

    public Rock(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        String rockCategoryName = JSONUtils.getAsString(json, "category");
        this.category = Helpers.mapSafeOptional(() -> RockCategory.valueOf(rockCategoryName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown rock category for rock: " + rockCategoryName));
        String desertSandColorName = JSONUtils.getAsString(json, "desert_sand_color");
        this.desertSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(desertSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown sand color for rock: " + desertSandColorName));

        String beachSandColorName = JSONUtils.getAsString(json, "beach_sand_color");
        this.beachSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(beachSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown beach sand color for rock: " + beachSandColorName));

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
        RAW(rock -> new RawRockBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        SMOOTH(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        COBBLE(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        BRICKS(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        GRAVEL(rock -> new Block(Block.Properties.of(Material.SAND, MaterialColor.STONE).sound(SoundType.STONE).strength(0.8f).harvestLevel(0).harvestTool(ToolType.SHOVEL)), false),
        SPIKE(rock -> new RockSpikeBlock(), false),
        CRACKED_BRICKS(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_BRICKS(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        MOSSY_COBBLE(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), true),
        CHISELED(rock -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), false),
        PEBBLE(rock -> new PebbleBlock(rock), false);

        public static final BlockType[] VALUES = values();
        public static final Codec<BlockType> CODEC = IStringSerializable.fromEnum(BlockType::values, BlockType::byName);
        public static final Map<String, BlockType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(k -> k.name().toLowerCase(), v -> v));

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        public static BlockType byName(String id)
        {
            return BY_NAME.get(id);
        }

        private final boolean variants;
        private final NonNullFunction<Default, Block> blockFactory;
        private final String serializedName;

        BlockType(NonNullFunction<Default, Block> blockFactory, boolean variants)
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
            return blockFactory.apply(rock);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}