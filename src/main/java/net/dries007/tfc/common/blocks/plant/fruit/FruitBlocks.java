/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.ClimateRanges;

import static net.dries007.tfc.common.blocks.plant.fruit.Lifecycle.*;

public final class FruitBlocks
{
    private static final Lifecycle[] CRANBERRY_STAGES = new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT};
    private static final Lifecycle[] BANANA_STAGES = new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT};

    public static WaterloggedBerryBushBlock createCranberry()
    {
        return new WaterloggedBerryBushBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCBlockEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(Food.CRANBERRY), CRANBERRY_STAGES, ClimateRanges.CRANBERRY_BUSH);
    }

    public static Block createBananaSapling()
    {
        return new BananaSaplingBlock(ExtendedProperties.of(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(60, 30), BANANA_STAGES, TFCBlocks.BANANA_PLANT, 6);
    }

    public static Block createBananaPlant()
    {
        return new BananaPlantBlock(ExtendedProperties.of(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion()).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammable(60, 30), TFCItems.FOOD.get(Food.BANANA), BANANA_STAGES);
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
            return new SpreadingBushBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammable(60, 30), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_CANES.get(this), maxHeight, ClimateRanges.SPREADING_BUSHES.get(this));
        }

        public Block createCane()
        {
            return new SpreadingCaneBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammable(60, 30), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_BUSHES.get(this), maxHeight, ClimateRanges.SPREADING_BUSHES.get(this));
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
            return new StationaryBerryBushBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammable(60, 30), TFCItems.FOOD.get(product), stages, ClimateRanges.STATIONARY_BUSHES.get(this));
        }
    }

    public enum Tree
    {
        CHERRY(Food.CHERRY, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}),
        GREEN_APPLE(Food.GREEN_APPLE, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}),
        LEMON(Food.LEMON, new Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT}),
        OLIVE(Food.OLIVE, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}),
        ORANGE(Food.ORANGE, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}),
        PEACH(Food.PEACH, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}),
        PLUM(Food.PLUM, new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT}),
        RED_APPLE(Food.RED_APPLE, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT});

        private final Food product;
        private final Lifecycle[] stages;

        Tree(Food product, Lifecycle[] stages)
        {
            this.product = product;
            this.stages = stages;
        }

        public Block createSapling()
        {
            return new FruitTreeSaplingBlock(ExtendedProperties.of(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(60, 30), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(this), 8, ClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createLeaves()
        {
            return new FruitTreeLeavesBlock(ExtendedProperties.of(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).noOcclusion()).blockEntity(TFCBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammable(90, 60), TFCItems.FOOD.get(product), stages, ClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createBranch()
        {
            return new FruitTreeBranchBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).flammable(60, 30));
        }

        public Block createGrowingBranch()
        {
            return new GrowingFruitTreeBranchBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(60, 30), TFCBlocks.FRUIT_TREE_BRANCHES.get(this), TFCBlocks.FRUIT_TREE_LEAVES.get(this), ClimateRanges.FRUIT_TREES.get(this));
        }
    }
}
