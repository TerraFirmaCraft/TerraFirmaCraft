/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.EnumSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.registry.RegistryPlant;

/*
 * Plant slowdown speeds guidelines--use these values unless you have a reason to make an exception.
 * Standard flowers: 1f
 * Short Grass: 0.9f
 * Tall Grass: 0.8f
 * Shrubs (Including double-tall plants, ferns, dense undergrowth): 0.6f
 * Reeds: 0.6f
 * Floating: 0.7f
 * Water grass/plants: 0.8f
 * Epiphytes: 1.0f
 */
public enum Plant implements RegistryPlant
{
    // Clay Indicators
    ATHYRIUM_FERN(BlockType.STANDARD, 0.7F),
    CANNA(BlockType.STANDARD, 0.7F, false, -0.1F, 0.43F, 0.51F, 0.6F, 0.74F, 0.93F),
    GOLDENROD(BlockType.STANDARD, 1.0F, false, -0.06F, 0.38F, 0.45F, 0.51F, 0.72F, 0.89F),
    PAMPAS_GRASS(BlockType.TALL_GRASS, 0.8F, true, 0F, 0.4F, 0.45F, 0.5F, 0.8F, 0.9F),
    PEROVSKIA(BlockType.DRY, 1.0F, false, 0.03F, 0.33F, 0.42F, 0.5F, 0.66F, 0.87F),
    ROSE(BlockType.TALL_GRASS, 0.6F, false, -0.167F, 0.42F, 0.5F, 0.61F, 0.76F, 0.92F),

    // Short Grasses
    BEACHGRASS(BlockType.BEACH_GRASS, 0.9f),
    BLUEGRASS(BlockType.SHORT_GRASS, 0.9F, false, -0.08F, 0.5F, 0.55F, 0.6F, 0.75F, 0.89F),
    BROMEGRASS(BlockType.SHORT_GRASS, 0.9F, false, -0.14F, 0.5F, 0.55F, 0.6F, 0.75F, 0.89F),
    FOUNTAIN_GRASS(BlockType.SHORT_GRASS, 0.9F),
    MANATEE_GRASS(BlockType.GRASS_WATER, 0.8F),
    ORCHARD_GRASS(BlockType.SHORT_GRASS, 0.9F),
    RYEGRASS(BlockType.SHORT_GRASS, 0.9F, false, -0.19F, 0.46F, 0.55F, 0.6F, 0.75F, 0.89F),
    SCUTCH_GRASS(BlockType.SHORT_GRASS, 0.9F),
    STAR_GRASS(BlockType.GRASS_WATER, 0.8F),
    TIMOTHY_GRASS(BlockType.SHORT_GRASS, 0.9F),
    RADDIA_GRASS(BlockType.SHORT_GRASS, 0.9F),

    // Other Plants
    ALLIUM(BlockType.STANDARD, 1.0F, false, -0.15F, 0.2F, 0.3F, 0.4F, 0.64F, 0.92F),
    ANTHURIUM(BlockType.STANDARD, 1.0F),
    ARROWHEAD(BlockType.TALL_WATER_FRESH, 0.6F),
    BADDERLOCKS(BlockType.TALL_WATER, 0.8F),
    BARREL_CACTUS(BlockType.CACTUS, 0F, false, -0.25F, 0.24F, 0.36F, 0.5F, 0.73F, 0.9F),
    BLOOD_LILY(BlockType.STANDARD, 1.0F, false, -0.167F, 0.333F, 0.431F, 0.523F, 0.845F, 0.878F),
    BLUE_GINGER(BlockType.STANDARD, 1.0F),
    BLUE_ORCHID(BlockType.STANDARD, 1.0F),
    BUR_REED(BlockType.TALL_WATER_FRESH, 0.6F, false, 0F, 0.19F, 0.27F, 0.5F, 0.72F, 0.88F),
    BUTTERFLY_MILKWEED(BlockType.STANDARD, 1.0F, false, -0.11F, 0.378F, 0.49F, 0.62F, 0.78F, 0.94F),
    BLACK_ORCHID(BlockType.STANDARD, 1.0F),
    CALENDULA(BlockType.STANDARD, 1F, false, -0.074F, 0.31F, 0.43F, 0.56F, 0.7F, 0.92F),
    CATTAIL(BlockType.TALL_WATER_FRESH, 0.6F, false, -0.21F, 0.28F, 0.41F, 0.52F, 0.73F, 0.9F),
    COBBLESTONE_LICHEN(BlockType.CREEPING_STONE, 1f),
    COONTAIL(BlockType.GRASS_WATER_FRESH, 0.8F),
    CORDGRASS(BlockType.TALL_WATER, 0.6F, false, 0F, 0.27F, 0.39F, 0.5F, 0.666F, 0.92F),
    DANDELION(BlockType.STANDARD, 1F, false, -0.15F, 0.28F, 0.46F, 0.57F, 0.71F, 0.93F),
    DEAD_BUSH(BlockType.DRY, 0.7F),
    DESERT_FLAME(BlockType.STANDARD, 1F, true, -0.5F, 0.33F, 0.420F, 0.5F, 0.833F, 0.92F),
    DUCKWEED(BlockType.FLOATING_FRESH, 0.7F),
    EEL_GRASS(BlockType.GRASS_WATER_FRESH, 0.8F),
    FIELD_HORSETAIL(BlockType.STANDARD, 0.9F),
    FOXGLOVE(BlockType.TALL_GRASS, 0.6F, false, -0.087F, 0.333F, 0.43F, 0.5F, 0.072F, 0.091F),
    GRAPE_HYACINTH(BlockType.STANDARD, 1F, false, -0.333F, 0.25F, 0.38F, 0.63F, 0.72F, 0.93F),
    GREEN_ALGAE(BlockType.FLOATING_FRESH, 0.7F),
    GUTWEED(BlockType.WATER, 0.8F),
    GUZMANIA(BlockType.EPIPHYTE, 1F),
    HELICONIA(BlockType.STANDARD, 1F),
    HEATHER(BlockType.STANDARD, 0.6F, false, -0.065F, 0.42F, 0.53F, 0.59F, 0.74F, 0.9F),
    HIBISCUS(BlockType.TALL_GRASS, 0.6F, false, -0.12F, 0.41F, 0.52F, 0.61F, 0.74F, 0.89F),
    HOUSTONIA(BlockType.STANDARD, 1F, false, -0.25F, 0.29F, 0.41F, 0.62F, 0.76F, 0.92F),
    KANGAROO_PAW(BlockType.STANDARD, 1F, true, -0.25F, 0.32F, 0.44F, 0.55F, 0.68F, 0.88F),
    KING_FERN(BlockType.TALL_GRASS, 0.4F),
    LABRADOR_TEA(BlockType.STANDARD, 1F, false, -0.15F, 0.2F, 0.3F, 0.4F, 0.74F, 0.9F),
    LADY_FERN(BlockType.STANDARD, 0.6F),
    LAMINARIA(BlockType.WATER, 0.9F),
    LICORICE_FERN(BlockType.EPIPHYTE, 1F),
    ARTISTS_CONK(BlockType.EPIPHYTE, 1F),
    LILY_OF_THE_VALLEY(BlockType.STANDARD, 1F, false, -0.167F, 0.18F, 0.29F, 0.5F, 0.72F, 0.9F),
    LILAC(BlockType.TALL_GRASS, 0.6F, false, -0.21F, 0.18F, 0.29F, 0.45F, 0.73F, 0.9F),
    LOTUS(BlockType.FLOATING_FRESH, 0.7F, true, -0.12F, 0.25F, 0.35F, 0.5F, 0.75F, 0.92F, 1000, 12500),
    MAIDEN_PINK(BlockType.FLOWERBED, 1f, false, -0.333F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F),
    MARIGOLD(BlockType.TALL_WATER_FRESH, 0.6F, false, 0.14F, 0.18F, 0.26F, 0.35F, 0.58F, 0.86F),
    MEADS_MILKWEED(BlockType.STANDARD, 1F, false, -0.17F, 0.18F, 0.29F, 0.5F, 0.72F, 0.93F),
    MILFOIL(BlockType.WATER_FRESH, 0.8F),
    MORNING_GLORY(BlockType.CREEPING, 0.8F, false, -0.083F, 0.333F, 0.417F, 0.5F, 0.75F, 0.917F, 21000, 5000),
    PHILODENDRON(BlockType.CREEPING, 0.7F),
    MOSS(BlockType.CREEPING, 1F),
    NASTURTIUM(BlockType.STANDARD, 1F, true),
    OSTRICH_FERN(BlockType.TALL_GRASS, 0.6F),
    OXEYE_DAISY(BlockType.STANDARD, 0.9F, false, 0.09F, 0.31F, 0.42F, 0.52F, 0.73F, 0.92F),
    PHRAGMITE(BlockType.TALL_WATER_FRESH, 0.6F, false, 0.06F, 0.23F, 0.34F, 0.43F, 0.66F, 0.88F),
    PICKERELWEED(BlockType.TALL_WATER_FRESH, 0.6F, false, -0.9F, 0.32F, 0.43F, 0.52F, 0.77F, 0.91F),
    PISTIA(BlockType.FLOATING_FRESH, 0.8F),
    POPPY(BlockType.STANDARD, 1F, false, -0.25F, 0.23F, 0.33F, 0.58F, 0.79F, 0.92F),
    PRIMROSE(BlockType.STANDARD, 1F, false, -0.4F, 0.34F, 0.41F, 0.8F, 0.89F, 0.95F),
    PULSATILLA(BlockType.STANDARD, 1F, false, -0.31F, 0.168F, 0.25F, 0.73F, 0.84F, 0.93F),
    RED_ALGAE(BlockType.FLOATING, 0.7F),
    REINDEER_LICHEN(BlockType.CREEPING, 1F),
    RED_SEALING_WAX_PALM(BlockType.TALL_GRASS, 0.4F),
    SACRED_DATURA(BlockType.STANDARD, 1F, false, -0.2f, 0.4f, 0.5f, 0.6f, 0.8f, 0.95f, 12000, 0),
    SAGEBRUSH(BlockType.DRY, 0.5F, false, -0.09F, 0.48F, 0.54F, 0.6F, 0.72F, 0.91F),
    SAGO(BlockType.WATER_FRESH, 0.8F),
    SAGUARO_FRUIT(BlockType.CACTUS_FLOWER, 0.7F, true, -0.2F, 0.22F, 0.3F, 0.5F, 0.73F, 0.9F),
    SAPPHIRE_TOWER(BlockType.TALL_GRASS, 0.6F, true, -0.05F, 0.1F, 0.2F, 0.52F, 0.76F, 0.92F),
    SARGASSUM(BlockType.FLOATING, 0.7F),
    SEA_LAVENDER(BlockType.TALL_WATER, 0.6F, false, 0.08F, 0.18F, 0.31F, 0.4F, 0.7F, 0.91F),
    SEA_PALM(BlockType.DRY, 0.6f),
    SILVER_SPURFLOWER(BlockType.STANDARD, 1F, false, 0.02F, 0.17F, 0.26F, 0.39F, 0.65F, 0.9F),
    SNAPDRAGON_PINK(BlockType.STANDARD, 1F, false, -0.08F, 0.28F, 0.4F, 0.5F, 0.72F, 0.89F),
    SNAPDRAGON_RED(BlockType.STANDARD, 1F, false, -0.09F, 0.28F, 0.4F, 0.5F, 0.72F, 0.89F),
    SNAPDRAGON_WHITE(BlockType.STANDARD, 1F, false, -0.04F, 0.28F, 0.4F, 0.5F, 0.72F, 0.89F),
    SNAPDRAGON_YELLOW(BlockType.STANDARD, 1F, false, -0.06F, 0.28F, 0.4F, 0.5F, 0.72F, 0.89F),
    STRELITZIA(BlockType.STANDARD, 1F, true, -0.333F, 0.417F, 0.5F, 0.61f, 0.75f, 0.9f),
    SWITCHGRASS(BlockType.TALL_GRASS, 0.8F, false, -0.14F, 0.38F, 0.49F, 0.58F, 0.77F, 0.93F),
    SWORD_FERN(BlockType.STANDARD, 0.6F),
    TALL_FESCUE_GRASS(BlockType.TALL_GRASS, 0.5F),
    TOQUILLA_PALM(BlockType.TALL_GRASS, 0.6F),
    TRILLIUM(BlockType.STANDARD, 1F, false, -0.27F, 0.24F, 0.32F, 0.61F, 0.83F, 0.92F),
    TROPICAL_MILKWEED(BlockType.STANDARD, 1F),
    TULIP_ORANGE(BlockType.STANDARD, 1F, false, -0.21F, 0.12F, 0.2F, 0.62F, 0.86F, 0.94F),
    TULIP_PINK(BlockType.STANDARD, 1F, false, -0.2F, 0.12F, 0.2F, 0.62F, 0.86F, 0.94F),
    TULIP_RED(BlockType.STANDARD, 1F, false, -0.24F, 0.12F, 0.2F, 0.62F, 0.86F, 0.94F),
    TULIP_WHITE(BlockType.STANDARD, 1F, false, -0.23F, 0.12F, 0.2F, 0.62F, 0.86F, 0.94F),
    TURTLE_GRASS(BlockType.GRASS_WATER, 0.8F),
    VRIESEA(BlockType.EPIPHYTE, 1F),
    WATER_CANNA(BlockType.FLOATING_FRESH, 0.8F),
    WHITE_WATER_LILY(BlockType.FLOATING_FRESH, 0.7F, false, -0.11F, 0.33F, 0.42F, 0.5F, 0.8F, 0.92F),
    PURPLE_WATER_LILY(BlockType.FLOATING_FRESH, 0.7F, false, -0.05F, 0.32F, 0.41F, 0.5F, 0.8F, 0.92F),
    YELLOW_WATER_LILY(BlockType.FLOATING_FRESH, 0.7F, false, -0.083F, 0.32F, 0.41F, 0.5F, 0.8F, 0.92F),
    WATER_TARO(BlockType.TALL_WATER_FRESH, 0.6F),
    YUCCA(BlockType.DRY, 0.8F, true, -0.12F, 0.31F, 0.42F, 0.51F, 0.72F, 0.9F),

    // Unique
    HANGING_VINES_PLANT(BlockType.WEEPING, 1.0F),
    HANGING_VINES(BlockType.WEEPING_TOP, 1.0F),
    SPANISH_MOSS_PLANT(BlockType.WEEPING, 1f),
    SPANISH_MOSS(BlockType.WEEPING_TOP, 1f),
    LIANA_PLANT(BlockType.WEEPING, 1.0F),
    LIANA(BlockType.WEEPING_TOP, 1.0F),
    TREE_FERN_PLANT(BlockType.TWISTING_SOLID, 0F),
    TREE_FERN(BlockType.TWISTING_SOLID_TOP, 0F),
    ARUNDO_PLANT(BlockType.TWISTING, 0.3F),
    ARUNDO(BlockType.TWISTING_TOP, 0.3F),
    DRY_PHRAGMITE_PLANT(BlockType.TWISTING, 0.3F),
    DRY_PHRAGMITE(BlockType.TWISTING_TOP, 0.3F),
    WINGED_KELP_PLANT(BlockType.KELP, 0.7F),
    WINGED_KELP(BlockType.KELP_TOP, 1.0F),
    LEAFY_KELP_PLANT(BlockType.KELP, 0.7F),
    LEAFY_KELP(BlockType.KELP_TOP, 1.0F),
    GIANT_KELP_PLANT(BlockType.KELP_TREE, 0.2F),
    GIANT_KELP_FLOWER(BlockType.KELP_TREE_FLOWER, 1.0F),
    IVY(BlockType.CREEPING, 1.0F),
    JUNGLE_VINES(BlockType.VINE, 1.0F),
    SAGUARO_PLANT(BlockType.BRANCHING_CACTUS, 1f),
    SAGUARO(BlockType.BRANCHING_CACTUS_TOP, 1f)
    ;

    private static final EnumSet<Plant> SPECIAL_POTTED_PLANTS = EnumSet.of(BARREL_CACTUS, FOXGLOVE, MORNING_GLORY, MOSS, OSTRICH_FERN, REINDEER_LICHEN, ROSE, SAPPHIRE_TOWER, TOQUILLA_PALM, TREE_FERN, PHILODENDRON);
    private static final EnumSet<Plant> BLOCK_TINTED_PLANTS = EnumSet.of(PAMPAS_GRASS, BLUEGRASS, BROMEGRASS, FOUNTAIN_GRASS, ORCHARD_GRASS, RYEGRASS, SCUTCH_GRASS, TIMOTHY_GRASS, RADDIA_GRASS, ARROWHEAD, BUR_REED, CATTAIL, DUCKWEED, FIELD_HORSETAIL, GUTWEED, KANGAROO_PAW, KING_FERN, LADY_FERN, LICORICE_FERN, LOTUS, MORNING_GLORY, PHILODENDRON, MOSS, OSTRICH_FERN, PHRAGMITE, PICKERELWEED, PISTIA, SAGO, SEA_LAVENDER, SWITCHGRASS, SWORD_FERN, TALL_FESCUE_GRASS, TOQUILLA_PALM, WHITE_WATER_LILY, YELLOW_WATER_LILY, PURPLE_WATER_LILY, WATER_TARO, HANGING_VINES_PLANT, HANGING_VINES, SPANISH_MOSS_PLANT, SPANISH_MOSS, TREE_FERN_PLANT, TREE_FERN, IVY, JUNGLE_VINES, MAIDEN_PINK);
    private static final EnumSet<Plant> ITEM_TINTED_PLANTS = EnumSet.of(BLUEGRASS, BROMEGRASS, FOUNTAIN_GRASS, ORCHARD_GRASS, RYEGRASS, SCUTCH_GRASS, TIMOTHY_GRASS, RADDIA_GRASS, KING_FERN, MOSS, SAGO, SWITCHGRASS, TALL_FESCUE_GRASS, IVY, JUNGLE_VINES, HANGING_VINES, GUTWEED);
    private static final EnumSet<Plant> FLOWERPOT_TINTED_PLANTS = EnumSet.of(PHILODENDRON, MOSS, TREE_FERN);

    private final @Nullable IntegerProperty ageProperty;
    private final float speedFactor;
    private final boolean isWetSeasonBlooming;
    private final float bloomOffset;
    private final float bloomingEnd;
    private final float seedingEnd;
    private final float dyingEnd;
    private final float dormantEnd;
    private final float sproutingEnd;
    private final int startTime;
    private final int endTime;
    private final BlockType type;

    Plant(BlockType type, float speedFactor)
    {
        this(type, speedFactor, false);
    }

    Plant(BlockType type, float speedFactor, boolean isWetSeasonBlooming)
    {
        this(type, speedFactor, isWetSeasonBlooming, -0.124f, 0.25f, 0.42f, 0.5f, 0.75f, 0.88f);
    }

    Plant(BlockType type, float speedFactor, boolean isWetSeasonBlooming, float bloomOffset, float bloomingEnd, float seedingEnd, float dyingEnd, float dormantEnd, float sproutingEnd)
    {
        this(type, speedFactor, isWetSeasonBlooming, bloomOffset, bloomingEnd, seedingEnd, dyingEnd, dormantEnd, sproutingEnd, 0, 0);
    }

    Plant(BlockType type, float speedFactor, boolean isWetSeasonBlooming, float bloomOffset, float bloomingEnd, float seedingEnd, float dyingEnd, float dormantEnd, float sproutingEnd, int startTime, int endTime)
    {
        this.type = type;
        this.speedFactor = speedFactor;
        this.isWetSeasonBlooming = isWetSeasonBlooming;
        this.bloomOffset = bloomOffset;
        this.bloomingEnd = bloomingEnd;
        this.seedingEnd = seedingEnd;
        this.dyingEnd = dyingEnd;
        this.dormantEnd = dormantEnd;
        this.sproutingEnd = sproutingEnd;
        this.startTime = startTime;
        this.endTime = endTime;

        final int maxAge = getMaxAgeForType(type);
        this.ageProperty = maxAge > 0 ? TFCBlockStateProperties.getAgeProperty(maxAge) : null;
    }

    private int getMaxAgeForType(BlockType type)
    {
        if (type == BlockType.GRASS_WATER || type == BlockType.GRASS_WATER_FRESH || type == BlockType.BEACH_GRASS || type == BlockType.SHORT_GRASS
            || type == BlockType.TALL_GRASS || type == BlockType.FLOWERBED || type == BlockType.FLOATING_FRESH || type == BlockType.FLOATING
            || type == BlockType.KELP_TREE || type == BlockType.KELP_TREE_FLOWER || type == BlockType.TWISTING_TOP || type == BlockType.TWISTING_SOLID_TOP
            || type == BlockType.BRANCHING_CACTUS_TOP || type == BlockType.KELP_TOP || type == BlockType.WEEPING_TOP || type == BlockType.TALL_WATER_FRESH)
        {
            return 3;
        }
        else
        {
            return 0;
        }
    }

    public Block create()
    {
        return type.factory.apply(this, type);
    }

    @Nullable
    public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
    {
        return needsItem() ? block -> type.blockItemFactory.apply(block, properties) : null;
    }

    public boolean isWetSeasonBlooming()
    {
        return isWetSeasonBlooming;
    }

    public int getStartTime()
    {
        return startTime;
    }

    public int getEndTime()
    {
        return endTime;
    }

    public float getBloomOffset()
    {
        return bloomOffset;
    }

    public float getBloomingEnd()
    {
        return bloomingEnd;
    }

    public float getSeedingEnd()
    {
        return seedingEnd;
    }

    public float getDyingEnd()
    {
        return dyingEnd;
    }

    public float getDormantEnd()
    {
        return dormantEnd;
    }

    public float getSproutingEnd()
    {
        return sproutingEnd;
    }

    public boolean needsItem()
    {
        return !BlockType.NO_ITEM_TYPES.contains(type);
    }

    public boolean canBeSnowPiled()
    {
        return type == BlockType.STANDARD || type == BlockType.SHORT_GRASS || type == BlockType.TALL_GRASS || type == BlockType.CREEPING || type == BlockType.CREEPING_STONE || type == BlockType.BEACH_GRASS;
    }

    public boolean canBeIcePiled()
    {
        return type == BlockType.TALL_WATER || type == BlockType.TALL_WATER_FRESH || type == BlockType.FLOATING || type == BlockType.FLOATING_FRESH;
    }

    public boolean isFoliage()
    {
        return BlockType.FOLIAGE_TYPES.contains(type);
    }

    public boolean isSeasonal()
    {
        return type == BlockType.VINE;
    }

    public boolean isTallGrass()
    {
        return type == BlockType.TALL_GRASS || type == BlockType.SHORT_GRASS;
    }

    public boolean isBlockTinted()
    {
        return BLOCK_TINTED_PLANTS.contains(this);
    }

    public boolean isItemTinted()
    {
        return ITEM_TINTED_PLANTS.contains(this);
    }

    public boolean isFlowerpotTinted()
    {
        return FLOWERPOT_TINTED_PLANTS.contains(this);
    }

    public boolean hasFlowerPot()
    {
        return type == BlockType.STANDARD || type == BlockType.FLOWERBED || type == BlockType.DRY || type == BlockType.CACTUS_FLOWER || SPECIAL_POTTED_PLANTS.contains(this);
    }

    @Nullable
    public IntegerProperty getAgeProperty()
    {
        return ageProperty;
    }

    /**
     * Compiler hack to allow forward references to paired plants.
     */
    private Supplier<? extends Block> transform()
    {
        return TFCBlocks.PLANTS.get(switch (this)
            {
                case HANGING_VINES -> HANGING_VINES_PLANT;
                case HANGING_VINES_PLANT -> HANGING_VINES;
                case TREE_FERN -> TREE_FERN_PLANT;
                case TREE_FERN_PLANT -> TREE_FERN;
                case WINGED_KELP_PLANT -> WINGED_KELP;
                case WINGED_KELP -> WINGED_KELP_PLANT;
                case GIANT_KELP_FLOWER -> GIANT_KELP_PLANT;
                case LEAFY_KELP -> LEAFY_KELP_PLANT;
                case LEAFY_KELP_PLANT -> LEAFY_KELP;
                case ARUNDO -> ARUNDO_PLANT;
                case ARUNDO_PLANT -> ARUNDO;
                case DRY_PHRAGMITE -> DRY_PHRAGMITE_PLANT;
                case DRY_PHRAGMITE_PLANT -> DRY_PHRAGMITE;
                case LIANA -> LIANA_PLANT;
                case LIANA_PLANT -> LIANA;
                case SPANISH_MOSS_PLANT -> SPANISH_MOSS;
                case SPANISH_MOSS -> SPANISH_MOSS_PLANT;
                case SAGUARO_PLANT -> SAGUARO;
                case SAGUARO -> SAGUARO_PLANT;
                default -> throw new IllegalStateException("Uhh why did you try to transform something that's not a tall plant?");
            });
    }

    private Supplier<? extends Block> secondTransform()
    {
        if (this == SAGUARO)
        {
            return TFCBlocks.PLANTS.get(SAGUARO_FRUIT);
        }
        throw new IllegalStateException("Uhh why did you try to transform something that's not a tall plant?");
    }

    enum BlockType
    {
        STANDARD((plant, type) -> PlantBlock.create(plant, fire(nonSolid(plant)).offsetType(BlockBehaviour.OffsetType.XZ))),
        FLOWERBED((plant, type) -> PlantBlock.createFlat(plant, fire(nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ)))),
        CACTUS_FLOWER((plant, type) -> PlantBlock.createCactusFlower(plant, fire(nonSolid(plant)).sound(SoundType.CROP))),
        CACTUS((plant, type) -> TFCCactusBlock.create(plant, fire(solid().strength(0.25F).sound(SoundType.WOOL)).pathType(PathType.DAMAGE_OTHER))),
        DRY((plant, type) -> PlantBlock.createDry(plant, fire(nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ)))),
        CREEPING((plant, type) -> CreepingPlantBlock.create(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))), // Post process ensures shape is updated after world gen
        CREEPING_STONE((plant, type) -> CreepingPlantBlock.createStone(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))),
        EPIPHYTE((plant, type) -> EpiphytePlantBlock.create(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))),
        SHORT_GRASS((plant, type) -> ShortGrassBlock.create(plant, fire(nonSolid(plant)).offsetType(BlockBehaviour.OffsetType.XZ))),
        BEACH_GRASS((plant, type) -> ShortGrassBlock.createBeachGrass(plant, fire(nonSolid(plant)).offsetType(BlockBehaviour.OffsetType.XZ))),
        TALL_GRASS((plant, type) -> TFCTallGrassBlock.create(plant, fire(nonSolid(plant)).offsetType(BlockBehaviour.OffsetType.XZ))),
        VINE((plant, type) -> new TFCVineBlock(fire(nonSolid(plant)))),
        WEEPING((plant, type) -> new BodyPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.DOWN)),
        WEEPING_TOP((plant, type) -> new TopPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), Direction.DOWN, BodyPlantBlock.WEEPING_SHAPE)),
        TWISTING((plant, type) -> new BodyPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.UP)),
        TWISTING_TOP((plant, type) -> new TopPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_SHAPE)),
        TWISTING_SOLID((plant, type) -> new BodyPlantBlock(fire(solidTallPlant()), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.UP)),
        TWISTING_SOLID_TOP((plant, type) -> new TopPlantBlock(fire(solidTallPlant()), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_SHAPE)),
        BRANCHING_CACTUS((plant, type) -> BranchingCactusBlock.createBody(fire(solid()).noLootTable().strength(0.25f).sound(SoundType.WOOL).pathType(PathType.DAMAGE_OTHER))),
        BRANCHING_CACTUS_TOP((plant, type) -> GrowingBranchingCactusBlock.createGrowing(fire(solid()).noLootTable().randomTicks().strength(0.25f).sound(SoundType.WOOL).pathType(PathType.DAMAGE_OTHER), plant.transform(), plant.secondTransform())),
        // Water
        KELP((plant, type) -> TFCKelpBlock.create(nonSolidTallPlant(plant).lootFrom(plant.transform()), plant.transform(), Direction.UP, BodyPlantBlock.THIN_BODY_SHAPE, TFCBlockStateProperties.SALT_WATER)),
        KELP_TOP(((plant, type) -> TFCKelpTopBlock.create(nonSolidTallPlant(plant), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_THIN_SHAPE, TFCBlockStateProperties.SALT_WATER))),
        KELP_TREE((plant, type) -> KelpTreeBlock.create(ExtendedProperties.of(kelp(plant)), TFCBlockStateProperties.SALT_WATER)),
        KELP_TREE_FLOWER((plant, type) -> KelpTreeFlowerBlock.create(kelp(plant), plant.transform())),
        FLOATING((plant, type) -> FloatingWaterPlantBlock.create(plant, TFCFluids.SALT_WATER.source(), nonSolid(plant)), PlaceOnWaterBlockItem::new),
        FLOATING_FRESH((plant, type) -> FloatingWaterPlantBlock.create(plant, () -> Fluids.WATER, nonSolid(plant)), PlaceOnWaterBlockItem::new),
        TALL_WATER((plant, type) -> TallWaterPlantBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant))),
        TALL_WATER_FRESH((plant, type) -> TallWaterPlantBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant))),
        WATER((plant, type) -> WaterPlantBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ))),
        WATER_FRESH((plant, type) -> WaterPlantBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ))),
        GRASS_WATER((plant, type) -> TFCSeagrassBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ))),
        GRASS_WATER_FRESH((plant, type) -> TFCSeagrassBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant).offsetType(BlockBehaviour.OffsetType.XZ)));

        private static final EnumSet<BlockType> NO_ITEM_TYPES = EnumSet.of(WEEPING, TWISTING_SOLID, KELP, KELP_TREE, TWISTING, BRANCHING_CACTUS);
        private static final EnumSet<BlockType> FOLIAGE_TYPES = EnumSet.of(WEEPING, WEEPING_TOP, FLOATING_FRESH, FLOATING, WATER_FRESH, GRASS_WATER_FRESH, GRASS_WATER);

        /**
         * Default properties to avoid rewriting them out every time
         */
        private static BlockBehaviour.Properties solid()
        {
            return Block.Properties.of().instabreak().noOcclusion().sound(SoundType.GRASS).randomTicks().pushReaction(PushReaction.DESTROY);
        }

        private static BlockBehaviour.Properties nonSolid(Plant plant)
        {
            return solid().replaceable().instabreak().speedFactor(plant.speedFactor).noCollission();
        }

        private static BlockBehaviour.Properties solidTallPlant()
        {
            return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).instabreak().noOcclusion().randomTicks().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY);
        }

        private static BlockBehaviour.Properties nonSolidTallPlant(Plant plant)
        {
            return solidTallPlant().instabreak().noCollission().speedFactor(plant.speedFactor).pushReaction(PushReaction.DESTROY);
        }

        private static BlockBehaviour.Properties kelp(Plant plant)
        {
            return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().speedFactor(plant.speedFactor).strength(1.0f).sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY);
        }

        private static ExtendedProperties fire(BlockBehaviour.Properties properties)
        {
            return ExtendedProperties.of(properties).flammable(60, 30);
        }

        private final BiFunction<Plant, BlockType, ? extends Block> factory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;

        BlockType(BiFunction<Plant, BlockType, ? extends Block> factory)
        {
            this(factory, BlockItem::new);
        }

        BlockType(BiFunction<Plant, BlockType, ? extends Block> factory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.factory = factory;
            this.blockItemFactory = blockItemFactory;
        }
    }
}
