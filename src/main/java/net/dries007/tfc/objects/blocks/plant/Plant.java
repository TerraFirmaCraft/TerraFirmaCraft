/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.dries007.tfc.util.calendar.Month;

public enum Plant
{
    ALLIUM(PlantType.STANDARD, 0.8F, new int[] {6, 6, 7, 0, 1, 1, 2, 2, 3, 4, 5, 6}),
    ATHYRIUM_FERN(PlantType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    BARREL_CACTUS(PlantType.CACTUS, 0F, new int[] {0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 3, 0}),
    BLACK_ORCHID(PlantType.STANDARD, 0.8F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    BLOOD_LILY(PlantType.STANDARD, 0.9F, new int[] {3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    BLUE_ORCHID(PlantType.STANDARD, 0.9F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    BUTTERFLY_MILKWEED(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    CALENDULA(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    CANNA(PlantType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}),
    DANDELION(PlantType.STANDARD, 0.9F, new int[] {9, 9, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8}),
    DUCKWEED(PlantType.FLOATING, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    FIELD_HORSETAIL(PlantType.STANDARD, 0.7F, new int[] {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1}),
    FOUNTAIN_GRASS(PlantType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    FOXGLOVE(PlantType.TALL_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 4}),
    GOLDENROD(PlantType.STANDARD, 0.6F, new int[] {4, 4, 4, 0, 0, 0, 1, 2, 2, 2, 2, 3}),
    GRAPE_HYACINTH(PlantType.STANDARD, 0.8F, new int[] {3, 3, 3, 0, 1, 1, 2, 3, 3, 3, 3, 3}),
    GUZMANIA(PlantType.EPIPHYTE, 0.9F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    HOUSTONIA(PlantType.STANDARD, 0.9F, new int[] {2, 2, 2, 0, 1, 1, 1, 2, 2, 2, 2, 2}),
    LABRADOR_TEA(PlantType.STANDARD, 0.8F, new int[] {0, 0, 1, 2, 3, 4, 4, 5, 6, 0, 0, 0}),
    LADY_FERN(PlantType.STANDARD, 0.6F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    LICORICE_FERN(PlantType.EPIPHYTE, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    LOTUS(PlantType.FLOATING, 0.9F, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 0, 0}),
    MEADS_MILKWEED(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 2, 3, 3, 3, 3, 4, 5}),
    MORNING_GLORY(PlantType.CREEPING, 0.9F, new int[] {2, 2, 2, 0, 0, 1, 1, 1, 1, 1, 2, 2}),
    MOSS(PlantType.CREEPING, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    NASTURTIUM(PlantType.STANDARD, 0.8F, new int[] {4, 4, 4, 0, 1, 2, 2, 2, 2, 2, 3, 3}),
    ORCHARD_GRASS(PlantType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    OSTRICH_FERN(PlantType.TALL_GRASS, 0.6F, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 0}),
    OXEYE_DAISY(PlantType.STANDARD, 0.9F, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 3, 4, 4, 5}),
    PAMPAS_GRASS(PlantType.TALL_GRASS, 0.6F, new int[] {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1}),
    PEROVSKIA(PlantType.STANDARD, 0.8F, new int[] {5, 5, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4}),
    PISTIA(PlantType.FLOATING, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    POPPY(PlantType.STANDARD, 0.9F, new int[] {4, 4, 4, 0, 1, 2, 2, 3, 3, 3, 3, 4}),
    PRIMROSE(PlantType.STANDARD, 0.9F, new int[] {0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2}),
    PULSATILLA(PlantType.STANDARD, 0.8F, new int[] {0, 1, 2, 3, 3, 4, 5, 5, 5, 0, 0, 0}),
    REINDEER_LICHEN(PlantType.CREEPING, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    ROSE(PlantType.TALL_GRASS, 0.9F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    RYEGRASS(PlantType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}),
    SACRED_DATURA(PlantType.STANDARD, 0.8F, new int[] {3, 3, 3, 0, 1, 2, 2, 2, 2, 2, 2, 2}),
    SAGEBRUSH(PlantType.STANDARD, 0.5F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0}),
    SAPPHIRE_TOWER(PlantType.TALL_GRASS, 0.6F, new int[] {2, 3, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2}),
    SARGASSUM(PlantType.FLOATING, 0.9F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    SCUTCH_GRASS(PlantType.SHORT_GRASS, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    SNAPDRAGON_PINK(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_RED(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_WHITE(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SNAPDRAGON_YELLOW(PlantType.STANDARD, 0.8F, new int[] {6, 6, 6, 0, 1, 1, 2, 3, 4, 1, 1, 5}),
    SPANISH_MOSS(PlantType.HANGING, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    STRELITZIA(PlantType.STANDARD, 0.8F, new int[] {0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2}),
    SWITCHGRASS(PlantType.TALL_GRASS, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0}),
    SWORD_FERN(PlantType.STANDARD, 0.7F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    TALL_FESCUE_GRASS(PlantType.TALL_GRASS, 0.5F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    TIMOTHY_GRASS(PlantType.SHORT_GRASS, 0.8F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    TOQUILLA_PALM(PlantType.TALL_GRASS, 0.4F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    TREE_FERN(PlantType.TALL_PLANT, 0F, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
    TRILLIUM(PlantType.STANDARD, 0.8F, new int[] {5, 5, 5, 0, 1, 2, 3, 3, 4, 4, 4, 4}),
    TROPICAL_MILKWEED(PlantType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 0}),
    TULIP_ORANGE(PlantType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_PINK(PlantType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_RED(PlantType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    TULIP_WHITE(PlantType.STANDARD, 0.9F, new int[] {4, 4, 5, 0, 1, 1, 2, 2, 2, 2, 3, 4}),
    VRIESEA(PlantType.EPIPHYTE, 0.8F, new int[] {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}),
    WATER_CANNA(PlantType.FLOATING, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 0}),
    WATER_LILY(PlantType.FLOATING, 0.8F, new int[] {5, 5, 6, 0, 1, 2, 2, 2, 2, 3, 4, 5}),
    YUCCA(PlantType.STANDARD, 0.8F, new int[] {0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 3});


    private final float speedFactor;
    private final int maxStage;
    private final int[] monthStage;
    private final PlantType plantType;

    Plant(PlantType plantType, float speedFactor, int[] monthStage)
    {
        this.plantType = plantType;
        this.speedFactor = speedFactor;
        this.monthStage = monthStage;

        int max = 0;
        for (int i : monthStage)
        {
            max = Math.max(max, i);
        }
        this.maxStage = max;
    }

    public Block create()
    {
        return this.plantType.create(this);
    }

    public float getSpeedFactor()
    {
        return speedFactor;
    }

    public int getMaxStage()
    {
        return maxStage;
    }

    public int getStage(Month month)
    {
        return monthStage.length < month.ordinal() ? 0 : monthStage[month.ordinal()];
    }

    public enum PlantType
    {
        STANDARD,
        CACTUS,
        CREEPING,
        HANGING,
        FLOATING,
        EPIPHYTE,
        SHORT_GRASS,
        TALL_GRASS,
        TALL_PLANT; // Tall grass that blocks movement

        public Block create(Plant plant)
        {
            // Common plant properties
            Block.Properties properties = Block.Properties.create(Material.TALL_PLANTS).notSolid().speedFactor(plant.speedFactor).hardnessAndResistance(0).sound(SoundType.PLANT);
            switch (this)
            {
                case STANDARD:
                    return new PlantBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case SHORT_GRASS:
                    return new ShortGrassBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case TALL_GRASS:
                    return new TFCTallGrassBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case TALL_PLANT:
                    return new TFCTallGrassBlock(properties.tickRandomly())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case CACTUS:
                    return new TFCCactusBlock(properties.tickRandomly())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case CREEPING:
                    return new CreepingPlantBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case EPIPHYTE:
                    return new EpiphytePlantBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case HANGING:
                    return new HangingPlantBlock(properties.tickRandomly().doesNotBlockMovement())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
                case FLOATING:
                    return new FloatingWaterPlantBlock(properties.tickRandomly())
                    {
                        @Override
                        public Plant getPlant()
                        {
                            return plant;
                        }
                    };
            }
            return new TFCBushBlock(properties.doesNotBlockMovement());
        }
    }
}
