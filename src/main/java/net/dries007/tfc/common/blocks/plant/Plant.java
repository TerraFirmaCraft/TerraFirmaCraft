/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WaterLilyBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.registry.RegistryPlant;
import org.jetbrains.annotations.Nullable;

public enum Plant implements RegistryPlant
{
    // Clay Indicators
    ATHYRIUM_FERN(BlockType.STANDARD, 0.8F),
    CANNA(BlockType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}),
    GOLDENROD(BlockType.STANDARD, 0.6F, new int[] {4, 4, 4, 0, 0, 0, 1, 2, 2, 2, 2, 3}),
    PAMPAS_GRASS(BlockType.TALL_GRASS, 0.6F),
    PEROVSKIA(BlockType.DRY, 0.8F, new int[] {5, 5, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4}),

    // Short Grasses
    BLUEGRASS(BlockType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0}),
    BROMEGRASS(BlockType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0}),
    FOUNTAIN_GRASS(BlockType.SHORT_GRASS, 0.8F),
    MANATEE_GRASS(BlockType.GRASS_WATER, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    ORCHARD_GRASS(BlockType.SHORT_GRASS, 0.8F),
    RYEGRASS(BlockType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}),
    SCUTCH_GRASS(BlockType.SHORT_GRASS, 0.7F),
    STAR_GRASS(BlockType.GRASS_WATER, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    TIMOTHY_GRASS(BlockType.SHORT_GRASS, 0.8F),
    RADDIA_GRASS(BlockType.SHORT_GRASS, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),

    // Other Plants
    ALLIUM(BlockType.STANDARD, 0.8F, new int[] {6, 6, 7, 0, 1, 1, 2, 2, 3, 4, 5, 6}),
    ANTHURIUM(BlockType.STANDARD, 0.8F, new int[] {0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1}),
    ARROWHEAD(BlockType.TALL_WATER_FRESH, 0.6F),
    HOUSTONIA(BlockType.STANDARD, 0.9F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    BADDERLOCKS(BlockType.TALL_WATER, 0.8F),
    BARREL_CACTUS(BlockType.CACTUS, 0F, new int[] {0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 3, 0}),
    BLOOD_LILY(BlockType.STANDARD, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    BLUE_ORCHID(BlockType.STANDARD, 0.9F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    BLUE_GINGER(BlockType.STANDARD, 0.8F, new int[] {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1}),
    CATTAIL(BlockType.TALL_WATER_FRESH, 0.6F),
    LAMINARIA(BlockType.WATER, 0.6F),
    MARIGOLD(BlockType.TALL_WATER_FRESH, 0.4F),
    BUR_REED(BlockType.TALL_WATER_FRESH, 0.6F, new int[] {0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0}),
    BUTTERFLY_MILKWEED(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    BLACK_ORCHID(BlockType.STANDARD, 0.8F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    COONTAIL(BlockType.GRASS_WATER_FRESH, 0.7F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    DANDELION(BlockType.STANDARD, 0.9F, new int[] {9, 9, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8}),
    DEAD_BUSH(BlockType.DRY, 0.9F),
    DESERT_FLAME(BlockType.STANDARD, 0.8F, new int[] {1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1}),
    DUCKWEED(BlockType.FLOATING_FRESH, 0.8F),
    EEL_GRASS(BlockType.GRASS_WATER_FRESH, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    FIELD_HORSETAIL(BlockType.STANDARD, 0.7F, new int[] {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1}),
    FOXGLOVE(BlockType.TALL_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 4}),
    GRAPE_HYACINTH(BlockType.STANDARD, 0.8F, new int[] {3, 3, 3, 0, 1, 1, 2, 3, 3, 3, 3, 3}),
    GUTWEED(BlockType.WATER, 0.9F),
    HELICONIA(BlockType.STANDARD, 0.8F, new int[] {0, 0, 1, 2, 0, 0, 0, 0, 1, 2, 0, 0}),
    HIBISCUS(BlockType.TALL_GRASS, 0.9F, new int[] {2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 2, 2}),
    KANGAROO_PAW(BlockType.STANDARD, 0.8F, new int[] {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}),
    KING_FERN(BlockType.TALL_GRASS, 0.4F),
    LABRADOR_TEA(BlockType.STANDARD, 0.8F, new int[] {0, 0, 1, 2, 3, 4, 4, 5, 6, 0, 0, 0}),
    LADY_FERN(BlockType.STANDARD, 0.6F),
    LICORICE_FERN(BlockType.EPIPHYTE, 0.7F),
    LILAC(BlockType.TALL_GRASS, 0.7F, new int[] {0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0}),
    LOTUS(BlockType.FLOATING_FRESH, 0.9F, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 0, 0}),
    CALENDULA(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    MEADS_MILKWEED(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    MILFOIL(BlockType.WATER_FRESH, 0.7F),
    MORNING_GLORY(BlockType.CREEPING, 0.9F, new int[] {2, 2, 2, 0, 0, 1, 1, 1, 1, 1, 2, 2}),
    MOSS(BlockType.CREEPING, 0.7F),
    NASTURTIUM(BlockType.STANDARD, 0.8F, new int[] {4, 4, 4, 0, 1, 2, 2, 2, 2, 2, 3, 3}),
    OSTRICH_FERN(BlockType.TALL_GRASS, 0.6F, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 0}),
    OXEYE_DAISY(BlockType.STANDARD, 0.9F, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 3, 4, 4, 5}),
    PHRAGMITE(BlockType.TALL_WATER_FRESH, 0.6F, new int[] {0, 0, 0, 1, 1, 1, 2, 2, 3, 1, 1, 0}),
    PICKERELWEED(BlockType.TALL_WATER_FRESH, 0.6F, new int[] {0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 0, 0}),
    PISTIA(BlockType.FLOATING_FRESH, 0.8F),
    POPPY(BlockType.STANDARD, 0.9F, new int[] {4, 4, 4, 0, 1, 2, 2, 3, 3, 3, 3, 4}),
    PRIMROSE(BlockType.STANDARD, 0.9F, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2}),
    PULSATILLA(BlockType.STANDARD, 0.8F, new int[] {0, 1, 2, 3, 3, 4, 5, 5, 5, 0, 0, 0}),
    REINDEER_LICHEN(BlockType.CREEPING, 0.7F),
    RED_SEALING_WAX_PALM(BlockType.TALL_GRASS, 0.4F),
    ROSE(BlockType.TALL_GRASS, 0.9F, new int[] {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0}),
    SACRED_DATURA(BlockType.STANDARD, 0.8F, new int[] {3, 3, 3, 0, 1, 2, 2, 2, 2, 2, 2, 2}),
    SAGEBRUSH(BlockType.DRY, 0.5F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0}),
    SAGO(BlockType.WATER_FRESH, 0.7F),
    SAPPHIRE_TOWER(BlockType.TALL_GRASS, 0.6F, new int[] {2, 3, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    SARGASSUM(BlockType.FLOATING, 0.9F),
    GUZMANIA(BlockType.EPIPHYTE, 0.9F),
    SILVER_SPURFLOWER(BlockType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 1, 2, 2, 2, 0, 0, 0, 0}),
    SNAPDRAGON_PINK(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_RED(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_WHITE(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_YELLOW(BlockType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SPANISH_MOSS(BlockType.HANGING, 0.7F),
    STRELITZIA(BlockType.STANDARD, 0.8F, new int[] {0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2}),
    SWITCHGRASS(BlockType.TALL_GRASS, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0}),
    SWORD_FERN(BlockType.STANDARD, 0.7F),
    TALL_FESCUE_GRASS(BlockType.TALL_GRASS, 0.5F),
    TOQUILLA_PALM(BlockType.TALL_GRASS, 0.4F),
    TRILLIUM(BlockType.STANDARD, 0.8F, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 4, 4, 4, 4}),
    TROPICAL_MILKWEED(BlockType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 0}),
    TULIP_ORANGE(BlockType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_PINK(BlockType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_RED(BlockType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_WHITE(BlockType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TURTLE_GRASS(BlockType.GRASS_WATER, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    VRIESEA(BlockType.EPIPHYTE, 0.8F, new int[] {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}),
    WATER_CANNA(BlockType.FLOATING_FRESH, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}),
    WATER_LILY(BlockType.FLOATING_FRESH, 0.8F, new int[] {5, 5, 6, 0, 1, 2, 2, 2, 2, 3, 4, 5}),
    WATER_TARO(BlockType.TALL_WATER_FRESH, 0.6F),
    YUCCA(BlockType.DRY, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 3}),

    // Unique
    HANGING_VINES_PLANT(BlockType.WEEPING, 1.0F),
    HANGING_VINES(BlockType.WEEPING_TOP, 1.0F),
    LIANA_PLANT(BlockType.WEEPING, 1.0F),
    LIANA(BlockType.WEEPING_TOP, 1.0F),
    TREE_FERN_PLANT(BlockType.TWISTING_SOLID, 0F),
    TREE_FERN(BlockType.TWISTING_SOLID_TOP, 0F),
    ARUNDO_PLANT(BlockType.TWISTING, 0.3F),
    ARUNDO(BlockType.TWISTING_TOP, 0.3F),
    DRY_PHRAGMITE_PLANT(BlockType.TWISTING, 0.3F),
    DRY_PHRAGMITE(BlockType.TWISTING_TOP, 0.3F),
    WINGED_KELP_PLANT(BlockType.KELP, 0.7F),
    WINGED_KELP(BlockType.KELP_TOP, 0.7F),
    LEAFY_KELP_PLANT(BlockType.KELP, 0.7F),
    LEAFY_KELP(BlockType.KELP_TOP, 0.7F),
    GIANT_KELP_PLANT(BlockType.KELP_TREE, 0.2F),
    GIANT_KELP_FLOWER(BlockType.KELP_TREE_FLOWER, 0.2F),
    IVY(BlockType.VINE, 1.0F),
    JUNGLE_VINES(BlockType.VINE, 1.0F);

    private final float speedFactor;
    @Nullable private final IntegerProperty property;
    private final int @Nullable[] stagesByMonth;
    private final BlockType type;

    Plant(BlockType type, float speedFactor)
    {
        this(type, speedFactor, null);
    }

    Plant(BlockType type, float speedFactor, int @Nullable[] stagesByMonth)
    {
        this.type = type;
        this.speedFactor = speedFactor;
        this.stagesByMonth = stagesByMonth;

        int maxStage = 0;
        if (stagesByMonth != null)
        {
            maxStage = Arrays.stream(stagesByMonth).max().orElse(0);
        }

        this.property = maxStage > 0 ? TFCBlockStateProperties.getStageProperty(maxStage) : null;
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

    @Override
    public int stageFor(Month month)
    {
        assert stagesByMonth != null;
        return stagesByMonth.length < month.ordinal() ? 0 : stagesByMonth[month.ordinal()];
    }

    @Override
    @Nullable
    public IntegerProperty getStageProperty()
    {
        return property;
    }

    public boolean needsItem()
    {
        return type != BlockType.WEEPING && type != BlockType.TWISTING_SOLID && type != BlockType.KELP && type != BlockType.KELP_TREE && type != BlockType.TWISTING;
    }

    public boolean isFoliage()
    {
        return type == BlockType.WEEPING || type == BlockType.WEEPING_TOP || type == BlockType.FLOATING_FRESH || type == BlockType.FLOATING || type == BlockType.WATER_FRESH || type == BlockType.GRASS_WATER_FRESH || type == BlockType.GRASS_WATER;
    }

    public boolean isSeasonal()
    {
        return type == BlockType.VINE;
    }

    public boolean isTallGrass()
    {
        return type == BlockType.TALL_GRASS || type == BlockType.SHORT_GRASS;
    }

    public boolean isItemTinted()
    {
        return this == BLUEGRASS || this == BROMEGRASS || this == FOUNTAIN_GRASS || this == ORCHARD_GRASS || this == RYEGRASS || this == SCUTCH_GRASS || this == TIMOTHY_GRASS || this == KANGAROO_PAW || this == KING_FERN || this == MOSS || this == SAGO || this == SWITCHGRASS || this == TALL_FESCUE_GRASS || this == IVY || this == JUNGLE_VINES || this == HANGING_VINES || this == GUTWEED;
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
                default -> throw new IllegalStateException("Uhh why did you try to transform something that's not a tall plant?");
            });
    }

    enum BlockType
    {
        STANDARD((plant, type) -> PlantBlock.create(plant, fire(nonSolid(plant)))),
        CACTUS((plant, type) -> TFCCactusBlock.create(plant, fire(solid().strength(0.25F).sound(SoundType.WOOL)).pathType(BlockPathTypes.DAMAGE_CACTUS))),
        DRY((plant, type) -> DryPlantBlock.create(plant, fire(nonSolid(plant)))),
        CREEPING((plant, type) -> CreepingPlantBlock.create(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))), // Post process ensures shape is updated after world gen
        HANGING((plant, type) -> HangingPlantBlock.create(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))),
        EPIPHYTE((plant, type) -> EpiphytePlantBlock.create(plant, fire(nonSolid(plant).hasPostProcess(TFCBlocks::always)))),
        SHORT_GRASS((plant, type) -> ShortGrassBlock.create(plant, fire(nonSolid(plant)))),
        TALL_GRASS((plant, type) -> TFCTallGrassBlock.create(plant, fire(nonSolid(plant)))),
        VINE((plant, type) -> new TFCVineBlock(fire(nonSolid(plant)))),
        WEEPING((plant, type) -> new BodyPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.DOWN)),
        WEEPING_TOP((plant, type) -> new TopPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), Direction.DOWN, BodyPlantBlock.WEEPING_SHAPE)),
        TWISTING((plant, type) -> new BodyPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.UP)),
        TWISTING_TOP((plant, type) -> new TopPlantBlock(fire(nonSolidTallPlant(plant)), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_SHAPE)),
        TWISTING_SOLID((plant, type) -> new BodyPlantBlock(fire(solidTallPlant()), plant.transform(), BodyPlantBlock.BODY_SHAPE, Direction.UP)),
        TWISTING_SOLID_TOP((plant, type) -> new TopPlantBlock(fire(solidTallPlant()), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_SHAPE)),
        // Water
        KELP((plant, type) -> TFCKelpBlock.create(nonSolidTallPlant(plant).lootFrom(plant.transform()), plant.transform(), Direction.UP, BodyPlantBlock.THIN_BODY_SHAPE, TFCBlockStateProperties.SALT_WATER)),
        KELP_TOP(((plant, type) -> TFCKelpTopBlock.create(nonSolidTallPlant(plant), plant.transform(), Direction.UP, BodyPlantBlock.TWISTING_THIN_SHAPE, TFCBlockStateProperties.SALT_WATER))),
        KELP_TREE((plant, type) -> KelpTreeBlock.create(kelp(plant), TFCBlockStateProperties.SALT_WATER)),
        KELP_TREE_FLOWER((plant, type) -> KelpTreeFlowerBlock.create(kelp(plant), plant.transform())),
        FLOATING((plant, type) -> FloatingWaterPlantBlock.create(plant, TFCFluids.SALT_WATER.source(), nonSolid(plant)), WaterLilyBlockItem::new),
        FLOATING_FRESH((plant, type) -> FloatingWaterPlantBlock.create(plant, () -> Fluids.WATER, nonSolid(plant)), WaterLilyBlockItem::new),
        TALL_WATER((plant, type) -> TallWaterPlantBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant))),
        TALL_WATER_FRESH((plant, type) -> TallWaterPlantBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant))),
        WATER((plant, type) -> WaterPlantBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant))),
        WATER_FRESH((plant, type) -> WaterPlantBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant))),
        GRASS_WATER((plant, type) -> TFCSeagrassBlock.create(plant, TFCBlockStateProperties.SALT_WATER, nonSolid(plant))),
        GRASS_WATER_FRESH((plant, type) -> TFCSeagrassBlock.create(plant, TFCBlockStateProperties.FRESH_WATER, nonSolid(plant)));

        /**
         * Default properties to avoid rewriting them out every time
         */
        private static BlockBehaviour.Properties solid()
        {
            return Block.Properties.of(Material.REPLACEABLE_PLANT).noOcclusion().sound(SoundType.GRASS).randomTicks();
        }

        private static BlockBehaviour.Properties nonSolid(Plant plant)
        {
            return solid().instabreak().speedFactor(plant.speedFactor).noCollission();
        }

        private static BlockBehaviour.Properties solidTallPlant()
        {
            return BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT).randomTicks().sound(SoundType.WEEPING_VINES);
        }

        private static BlockBehaviour.Properties nonSolidTallPlant(Plant plant)
        {
            return solidTallPlant().instabreak().noCollission().speedFactor(plant.speedFactor);
        }

        private static BlockBehaviour.Properties kelp(Plant plant)
        {
            return BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.PLANT).noCollission().randomTicks().speedFactor(plant.speedFactor).strength(1.0f).sound(SoundType.WET_GRASS);
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
