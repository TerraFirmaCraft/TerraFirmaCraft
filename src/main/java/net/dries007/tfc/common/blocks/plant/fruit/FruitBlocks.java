/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.common.tileentity.FruitTreeLeavesTileEntity;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;

import static net.dries007.tfc.common.blocks.plant.fruit.Lifecycle.*;

public class FruitBlocks
{
    private static final Lifecycle[] CRANBERRY_STAGES = new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT};
    private static final Lifecycle[] BANANA_STAGES = new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT};

    public static WaterloggedBerryBushBlock createCranberry()
    {
        return new WaterloggedBerryBushBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCTileEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(Food.CRANBERRY), CRANBERRY_STAGES, 9);
    }

    public static Block createBananaSapling()
    {
        return new BananaSaplingBlock(new ForgeBlockProperties(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCTileEntities.TICK_COUNTER).flammable(60, 30), BANANA_STAGES, TFCBlocks.BANANA_PLANT, 6);
    }

    public static Block createBananaPlant()
    {
        return new BananaPlantBlock(new ForgeBlockProperties(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion()).blockEntity(TFCTileEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(Food.BANANA), BANANA_STAGES);
    }

    public enum SpreadingBush
    {
        BLACKBERRY(Food.BLACKBERRY, new Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT}, 4, 7),
        RASPBERRY(Food.RASPBERRY, new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 3, 9),
        BLUEBERRY(Food.BLUEBERRY, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 2, 13),
        ELDERBERRY(Food.ELDERBERRY, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 3, 9);

        private final Food product;
        private final Lifecycle[] stages;
        private final int maxHeight;
        private final int deathChance;

        SpreadingBush(Food product, Lifecycle[] stages, int maxHeight, int deathChance)
        {
            this.product = product;
            this.stages = stages;
            this.maxHeight = maxHeight;
            this.deathChance = deathChance;
        }

        public Block createBush()
        {
            return new SpreadingBushBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCTileEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_CANES.get(this), maxHeight, deathChance);
        }

        public Block createCane()
        {
            return new SpreadingCaneBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCTileEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(product), stages, TFCBlocks.SPREADING_BUSHES.get(this), maxHeight, deathChance);
        }
    }

    public enum StationaryBush
    {
        SNOWBERRY(Food.SNOWBERRY, new Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 10),
        BUNCHBERRY(Food.BUNCHBERRY, new Lifecycle[] {DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 8),
        GOOSEBERRY(Food.GOOSEBERRY, new Lifecycle[] {DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT}, 9),
        CLOUDBERRY(Food.CLOUDBERRY, new Lifecycle[] {DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 8),
        STRAWBERRY(Food.STRAWBERRY, new Lifecycle[] {FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY}, 11),
        WINTERGREEN_BERRY(Food.WINTERGREEN_BERRY, new Lifecycle[] {DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING}, 12);

        private final Food product;
        private final Lifecycle[] stages;
        private final int deathChance;

        StationaryBush(Food product, Lifecycle[] stages, int deathChance)
        {
            this.product = product;
            this.stages = stages;
            this.deathChance = deathChance;
        }

        public Block create()
        {
            return new StationaryBerryBushBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).blockEntity(TFCTileEntities.BERRY_BUSH).flammable(60, 30), TFCItems.FOOD.get(product), stages, deathChance);
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
            return new FruitTreeSaplingBlock(new ForgeBlockProperties(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCTileEntities.TICK_COUNTER).flammable(60, 30), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(this), 8);
        }

        public Block createLeaves()
        {
            return new FruitTreeLeavesBlock(new ForgeBlockProperties(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion()).blockEntity(TFCTileEntities.FRUIT_TREE).flammable(90, 60), TFCItems.FOOD.get(product), stages);
        }

        public Block createBranch()
        {
            return new FruitTreeBranchBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).flammable(60, 30));
        }

        public Block createGrowingBranch()
        {
            return new GrowingFruitTreeBranchBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).blockEntity(TFCTileEntities.TICK_COUNTER).flammable(60, 30), TFCBlocks.FRUIT_TREE_BRANCHES.get(this), TFCBlocks.FRUIT_TREE_LEAVES.get(this));
        }
    }
}
