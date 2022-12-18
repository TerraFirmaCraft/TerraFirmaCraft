/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRanges;

import static net.dries007.tfc.common.blocks.plant.fruit.Lifecycle.*;

public final class FruitBlocks
{
    private static final Lifecycle[] CRANBERRY_STAGES = new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT};
    private static final Lifecycle[] BANANA_STAGES = new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT};

    public static WaterloggedBerryBushBlock createCranberry()
    {
        return new WaterloggedBerryBushBlock(ExtendedProperties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.BERRY_BUSH).flammableLikeLeaves(), TFCItems.FOOD.get(Food.CRANBERRY), CRANBERRY_STAGES, ClimateRanges.CRANBERRY_BUSH);
    }

    public static Block createBananaSapling()
    {
        return new BananaSaplingBlock(ExtendedProperties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS).blockEntity(TFCBlockEntities.TICK_COUNTER).flammableLikeLeaves(), BANANA_STAGES, TFCBlocks.BANANA_PLANT, TFCConfig.SERVER.bananaSaplingGrowthDays);
    }

    public static Block createPottedBananaSapling()
    {
        return new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, TFCBlocks.BANANA_SAPLING, BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion());
    }

    public static Block createBananaPlant()
    {
        return new BananaPlantBlock(ExtendedProperties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion().blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), TFCItems.FOOD.get(Food.BANANA), BANANA_STAGES);
    }

    public enum SpreadingBush
    {
        BLACKBERRY(Food.BLACKBERRY, new Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT}, 4),
        RASPBERRY(Food.RASPBERRY, new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 3),
        BLUEBERRY(Food.BLUEBERRY, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 2),
        ELDERBERRY(Food.ELDERBERRY, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3);

        private final Food product;
        private final Lifecycle[] stages;
        private final int maxHeight;

        SpreadingBush(Food product, Lifecycle[] stages, int maxHeight)
        {
            this.product = product;
            this.stages = stages;
            this.maxHeight = maxHeight;
        }

        public Block createBush()
        {
            return new SpreadingBushBlock(ExtendedProperties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_CANES.get(this), maxHeight, ClimateRanges.SPREADING_BUSHES.get(this));
        }

        public Block createCane()
        {
            return new SpreadingCaneBlock(ExtendedProperties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_BUSHES.get(this), maxHeight, ClimateRanges.SPREADING_BUSHES.get(this));
        }
    }

    public enum StationaryBush
    {
        SNOWBERRY(Food.SNOWBERRY, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT),
        BUNCHBERRY(Food.BUNCHBERRY, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT),
        GOOSEBERRY(Food.GOOSEBERRY, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT),
        CLOUDBERRY(Food.CLOUDBERRY, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT),
        STRAWBERRY(Food.STRAWBERRY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY),
        WINTERGREEN_BERRY(Food.WINTERGREEN_BERRY, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING);

        private final Food product;
        private final Lifecycle[] stages;

        StationaryBush(Food product, Lifecycle... stages)
        {
            this.product = product;
            this.stages = stages;
        }

        public Block create()
        {
            return new StationaryBerryBushBlock(ExtendedProperties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), TFCItems.FOOD.get(product), stages, ClimateRanges.STATIONARY_BUSHES.get(this));
        }
    }

    public enum Tree implements StringRepresentable
    {
        CHERRY(Food.CHERRY, 8, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}),
        GREEN_APPLE(Food.GREEN_APPLE, 10, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}),
        LEMON(Food.LEMON, 8, new Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT}),
        OLIVE(Food.OLIVE, 12, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}),
        ORANGE(Food.ORANGE, 7, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}),
        PEACH(Food.PEACH, 11, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}),
        PLUM(Food.PLUM, 8, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT}),
        RED_APPLE(Food.RED_APPLE, 10, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT});

        private final Food product;
        private final Lifecycle[] stages;
        private final String serializedName;
        private final int treeGrowthDays;

        Tree(Food product, int treeGrowthDays, Lifecycle[] stages)
        {
            this.product = product;
            this.stages = stages;
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.treeGrowthDays = treeGrowthDays;
        }

        public Block createSapling()
        {
            return new FruitTreeSaplingBlock(ExtendedProperties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS).blockEntity(TFCBlockEntities.TICK_COUNTER).flammableLikeLeaves(), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(this), this::daysToGrow, ClimateRanges.FRUIT_TREES.get(this), stages);
        }

        public Block createPottedSapling()
        {
            return new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, TFCBlocks.FRUIT_TREE_SAPLINGS.get(this), BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion());
        }

        public Block createLeaves()
        {
            return new FruitTreeLeavesBlock(ExtendedProperties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion().blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), TFCItems.FOOD.get(product), stages, ClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createBranch()
        {
            return new FruitTreeBranchBlock(ExtendedProperties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f).flammableLikeLogs(), ClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createGrowingBranch()
        {
            return new GrowingFruitTreeBranchBlock(ExtendedProperties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f).blockEntity(TFCBlockEntities.TICK_COUNTER).flammableLikeLogs(), TFCBlocks.FRUIT_TREE_BRANCHES.get(this), TFCBlocks.FRUIT_TREE_LEAVES.get(this), ClimateRanges.FRUIT_TREES.get(this));
        }

        public int daysToGrow()
        {
            return TFCConfig.SERVER.fruitSaplingGrowthDays.get(this).get();
        }

        public int defaultDaysToGrow()
        {
            return treeGrowthDays;
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}
