/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.registry.RegistryRock;

/**
 * Default rocks that are used for block registration calls. Not extensible.
 */
public enum Rock implements RegistryRock
{
    GRANITE(RockDisplayCategory.FELSIC_IGNEOUS_INTRUSIVE, MapColor.STONE, SandBlockType.BROWN),
    DIORITE(RockDisplayCategory.INTERMEDIATE_IGNEOUS_INTRUSIVE, MapColor.METAL, SandBlockType.WHITE),
    GABBRO(RockDisplayCategory.MAFIC_IGNEOUS_INTRUSIVE, MapColor.COLOR_GRAY, SandBlockType.BLACK),
    SHALE(RockDisplayCategory.SEDIMENTARY, MapColor.COLOR_GRAY, SandBlockType.BLACK),
    CLAYSTONE(RockDisplayCategory.SEDIMENTARY, MapColor.TERRACOTTA_YELLOW, SandBlockType.BROWN),
    LIMESTONE(RockDisplayCategory.SEDIMENTARY, MapColor.TERRACOTTA_WHITE, SandBlockType.WHITE),
    CONGLOMERATE(RockDisplayCategory.SEDIMENTARY, MapColor.TERRACOTTA_LIGHT_GRAY, SandBlockType.GREEN),
    DOLOMITE(RockDisplayCategory.SEDIMENTARY, MapColor.COLOR_GRAY, SandBlockType.BLACK),
    CHERT(RockDisplayCategory.SEDIMENTARY, MapColor.TERRACOTTA_ORANGE, SandBlockType.YELLOW),
    CHALK(RockDisplayCategory.SEDIMENTARY, MapColor.QUARTZ, SandBlockType.WHITE),
    RHYOLITE(RockDisplayCategory.FELSIC_IGNEOUS_EXTRUSIVE, MapColor.TERRACOTTA_LIGHT_GRAY, SandBlockType.RED),
    BASALT(RockDisplayCategory.MAFIC_IGNEOUS_EXTRUSIVE, MapColor.COLOR_BLACK, SandBlockType.RED),
    ANDESITE(RockDisplayCategory.INTERMEDIATE_IGNEOUS_EXTRUSIVE, MapColor.TERRACOTTA_CYAN, SandBlockType.RED),
    DACITE(RockDisplayCategory.INTERMEDIATE_IGNEOUS_EXTRUSIVE, MapColor.STONE, SandBlockType.RED),
    QUARTZITE(RockDisplayCategory.METAMORPHIC, MapColor.TERRACOTTA_WHITE, SandBlockType.YELLOW),
    SLATE(RockDisplayCategory.METAMORPHIC, MapColor.WOOD, SandBlockType.BROWN),
    PHYLLITE(RockDisplayCategory.METAMORPHIC, MapColor.TERRACOTTA_LIGHT_BLUE, SandBlockType.BROWN),
    SCHIST(RockDisplayCategory.METAMORPHIC, MapColor.TERRACOTTA_LIGHT_GREEN, SandBlockType.GREEN),
    GNEISS(RockDisplayCategory.METAMORPHIC, MapColor.TERRACOTTA_LIGHT_GRAY, SandBlockType.GREEN),
    MARBLE(RockDisplayCategory.METAMORPHIC, MapColor.WOOL, SandBlockType.WHITE);

    public static final Rock[] VALUES = values();

    private final String serializedName;
    private final RockDisplayCategory category;
    private final MapColor color;
    private final SandBlockType sandType;

    Rock(RockDisplayCategory category, MapColor color, SandBlockType sandType)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.category = category;
        this.color = color;
        this.sandType = sandType;
    }

    public SandBlockType getSandType()
    {
        return sandType;
    }

    @Override
    public RockDisplayCategory displayCategory()
    {
        return category;
    }

    @Override
    public MapColor color()
    {
        return color;
    }

    @Override
    public Supplier<? extends Block> getBlock(BlockType type)
    {
        return TFCBlocks.ROCK_BLOCKS.get(this).get(type);
    }

    @Override
    public Supplier<? extends Block> getAnvil()
    {
        return TFCBlocks.ROCK_ANVILS.get(this);
    }

    @Override
    public Supplier<? extends SlabBlock> getSlab(BlockType type)
    {
        return TFCBlocks.ROCK_DECORATIONS.get(this).get(type).slab();
    }

    @Override
    public Supplier<? extends StairBlock> getStair(BlockType type)
    {
        return TFCBlocks.ROCK_DECORATIONS.get(this).get(type).stair();
    }

    @Override
    public Supplier<? extends WallBlock> getWall(BlockType type)
    {
        return TFCBlocks.ROCK_DECORATIONS.get(this).get(type).wall();
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public enum BlockType implements StringRepresentable
    {
        RAW((rock, self) -> RockConvertableToAnvilBlock.createForIgneousOnly(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops(), rock, false), true),
        HARDENED((rock, self) -> RockConvertableToAnvilBlock.createForIgneousOnly(properties(rock).strength(rock.category().hardness(8f), 10).requiresCorrectToolForDrops(), rock, true), false),
        SMOOTH((rock, self) -> new Block(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops()), true),
        COBBLE((rock, self) -> new MossGrowingBlock(properties(rock).strength(rock.category().hardness(5.5f), 10).requiresCorrectToolForDrops(), rock.getBlock(Objects.requireNonNull(self.mossy()))), true),
        BRICKS((rock, self) -> new MossGrowingBlock(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops(), rock.getBlock(Objects.requireNonNull(self.mossy()))), true),
        GRAVEL((rock, self) -> new Block(Block.Properties.of().mapColor(rock.color()).sound(SoundType.GRAVEL).instrument(NoteBlockInstrument.SNARE).strength(rock.category().hardness(2.0f))), false),
        SPIKE((rock, self) -> new RockSpikeBlock(properties(rock).strength(rock.category().hardness(4f), 10).requiresCorrectToolForDrops().lightLevel(TFCBlocks.lavaLoggedBlockEmission())), false),
        CRACKED_BRICKS((rock, self) -> new Block(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops()), true),
        MOSSY_BRICKS((rock, self) -> new MossSpreadingBlock(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops()), true),
        MOSSY_COBBLE((rock, self) -> new MossSpreadingBlock(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops()), true),
        CHISELED((rock, self) -> new Block(properties(rock).strength(rock.category().hardness(8f), 10).requiresCorrectToolForDrops()), false),
        LOOSE((rock, self) -> new LooseRockBlock(properties(rock).strength(0.05f, 0.0f).noCollission()), false),
        MOSSY_LOOSE((rock, self) -> new LooseRockBlock(properties(rock).strength(0.05f, 0.0f).noCollission()), false),
        PRESSURE_PLATE((rock, self) -> new PressurePlateBlock(BlockSetType.STONE, properties(rock).requiresCorrectToolForDrops().noCollission().strength(0.5f)), false),
        BUTTON((rock, self) -> new ButtonBlock(BlockSetType.STONE, 20, properties(rock).noCollission().strength(0.5f)), false),
        AQUEDUCT((rock, self) -> new AqueductBlock(properties(rock).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops()), false);

        public static final BlockType[] VALUES = BlockType.values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        private static BlockBehaviour.Properties properties(RegistryRock rock)
        {
            return BlockBehaviour.Properties.of().mapColor(rock.color()).sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM);
        }

        private final boolean variants;
        private final BiFunction<RegistryRock, BlockType, Block> blockFactory;
        private final String serializedName;

        BlockType(BiFunction<RegistryRock, BlockType, Block> blockFactory, boolean variants)
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

        public Block create(RegistryRock rock)
        {
            return blockFactory.apply(rock, this);
        }

        public SlabBlock createSlab(RegistryRock rock)
        {
            final BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            final BlockType mossy = mossy();
            if (mossy == this)
            {
                return new MossSpreadingSlabBlock(properties);
            }
            else if (mossy != null)
            {
                return new MossGrowingSlabBlock(properties, rock.getSlab(mossy));
            }
            return new SlabBlock(properties);
        }

        public StairBlock createStairs(RegistryRock rock)
        {
            final Supplier<BlockState> state = () -> rock.getBlock(this).get().defaultBlockState();
            final BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            final BlockType mossy = mossy();
            if (mossy == this)
            {
                return new MossSpreadingStairBlock(state, properties);
            }
            else if (mossy != null)
            {
                return new MossGrowingStairsBlock(state, properties, rock.getStair(mossy));
            }
            return new StairBlock(state.get(), properties);
        }

        public WallBlock createWall(RegistryRock rock)
        {
            final BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5f, 10).requiresCorrectToolForDrops();
            final BlockType mossy = mossy();
            if (mossy == this)
            {
                return new MossSpreadingWallBlock(properties);
            }
            else if (mossy != null)
            {
                return new MossGrowingWallBlock(properties, rock.getWall(mossy));
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
