/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.soil.SandBlockType;

/**
 * Default rocks that are used for block registration calls. Not extensible.
 */
public enum Rock implements StringRepresentable
{
    GRANITE(RockCategory.IGNEOUS_INTRUSIVE, SandBlockType.BROWN),
    DIORITE(RockCategory.IGNEOUS_INTRUSIVE, SandBlockType.WHITE),
    GABBRO(RockCategory.IGNEOUS_INTRUSIVE, SandBlockType.BLACK),
    SHALE(RockCategory.SEDIMENTARY, SandBlockType.BLACK),
    CLAYSTONE(RockCategory.SEDIMENTARY, SandBlockType.BROWN),
    LIMESTONE(RockCategory.SEDIMENTARY, SandBlockType.WHITE),
    CONGLOMERATE(RockCategory.SEDIMENTARY, SandBlockType.GREEN),
    DOLOMITE(RockCategory.SEDIMENTARY, SandBlockType.BLACK),
    CHERT(RockCategory.SEDIMENTARY, SandBlockType.YELLOW),
    CHALK(RockCategory.SEDIMENTARY, SandBlockType.WHITE),
    RHYOLITE(RockCategory.IGNEOUS_EXTRUSIVE, SandBlockType.RED),
    BASALT(RockCategory.IGNEOUS_EXTRUSIVE, SandBlockType.RED),
    ANDESITE(RockCategory.IGNEOUS_EXTRUSIVE, SandBlockType.RED),
    DACITE(RockCategory.IGNEOUS_EXTRUSIVE, SandBlockType.RED),
    QUARTZITE(RockCategory.METAMORPHIC, SandBlockType.YELLOW),
    SLATE(RockCategory.METAMORPHIC, SandBlockType.BROWN),
    PHYLLITE(RockCategory.METAMORPHIC, SandBlockType.BROWN),
    SCHIST(RockCategory.METAMORPHIC, SandBlockType.GREEN),
    GNEISS(RockCategory.METAMORPHIC, SandBlockType.GREEN),
    MARBLE(RockCategory.METAMORPHIC, SandBlockType.WHITE);

    private final String serializedName;
    private final RockCategory category;
    private final SandBlockType sandType;

    Rock(RockCategory category, SandBlockType sandType)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.category = category;
        this.sandType = sandType;
    }

    public RockCategory getCategory()
    {
        return category;
    }

    public SandBlockType getSandType()
    {
        return sandType;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }


    public enum BlockType implements StringRepresentable
    {
        RAW((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2 + rock.getCategory().getHardness(), 10).requiresCorrectToolForDrops()), true),
        HARDENED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.25f + rock.getCategory().getHardness(), 10).requiresCorrectToolForDrops()), false),
        SMOOTH((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops()), true),
        COBBLE((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops(), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        BRICKS((rock, self) -> new MossGrowingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops(), TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        GRAVEL((rock, self) -> new Block(Block.Properties.of(Material.SAND, MaterialColor.STONE).sound(SoundType.GRAVEL).strength(0.8f)), false),
        SPIKE((rock, self) -> new RockSpikeBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.4f, 10).requiresCorrectToolForDrops()), false),
        CRACKED_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops()), true),
        MOSSY_BRICKS((rock, self) -> new MossSpreadingBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops()), true),
        MOSSY_COBBLE((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops()), true),
        CHISELED((rock, self) -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops()), false),
        LOOSE((rock, self) -> new LooseRockBlock(Block.Properties.of(TFCMaterials.NON_SOLID_STONE).strength(0.05f, 0.0f).sound(SoundType.STONE).noCollission()), false),
        PRESSURE_PLATE((rock, self) -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().noCollission().strength(0.5F)), false),
        BUTTON((rock, self) -> new StoneButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F)), false);

        public static final BlockType[] VALUES = BlockType.values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        private final boolean variants;
        private final BiFunction<Rock, BlockType, Block> blockFactory;
        private final String serializedName;

        BlockType(BiFunction<Rock, BlockType, Block> blockFactory, boolean variants)
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

        public Block create(Rock rock)
        {
            return blockFactory.apply(rock, this);
        }

        public SlabBlock createSlab(Rock rock)
        {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            if (mossy() == this)
            {
                return new MossSpreadingSlabBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingSlabBlock(properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy()).slab());
            }
            return new SlabBlock(properties);
        }

        public StairBlock createStairs(Rock rock)
        {
            Supplier<BlockState> state = () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(this).get().defaultBlockState();
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            if (mossy() == this)
            {
                return new MossSpreadingStairBlock(state, properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingStairsBlock(state, properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy()).stair());
            }
            return new StairBlock(state, properties);
        }

        public WallBlock createWall(Rock rock)
        {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            if (mossy() == this)
            {
                return new MossSpreadingWallBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingWallBlock(properties, TFCBlocks.ROCK_DECORATIONS.get(rock).get(mossy()).wall());
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
            return switch (this)
                {
                    case COBBLE, MOSSY_COBBLE -> MOSSY_COBBLE;
                    case BRICKS, MOSSY_BRICKS -> MOSSY_BRICKS;
                    default -> null;
                };
        }
    }
}
