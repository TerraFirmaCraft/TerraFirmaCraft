/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api;

import java.util.function.Supplier;

import net.dries007.tfc.objects.blocks.wood.ToolRackBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.wood.SaplingBlock;

public class Wood
{
    /**
     * Default wood types used for block registration calls
     * Not extensible
     *
     * todo: re-evaluate if there is any data driven behavior that needs a json driven ore
     *
     * @see Wood instead and register via json
     */
    public enum Default
    {
        ACACIA(false),
        ASH(false),
        ASPEN(false),
        BIRCH(false),
        BLACKWOOD(false),
        CHESTNUT(false),
        DOUGLAS_FIR(false),
        HICKORY(false),
        KAPOK(false),
        MAPLE(false),
        OAK(false),
        PALM(false),
        PINE(true),
        ROSEWOOD(false),
        SEQUOIA(true),
        SPRUCE(true),
        SYCAMORE(false),
        WHITE_CEDAR(false),
        WILLOW(false);

        private final boolean conifer;

        Default(boolean conifer)
        {
            this.conifer = conifer;
        }

        public boolean isConifer()
        {
            return conifer;
        }

        public MaterialColor getMaterialColor()
        {
            return MaterialColor.ADOBE; // todo: in 1.16 there are two material colors, one for the top, one for bark. We need to figure out which materials match our logs.
        }
    }

    public enum BlockType
    {
        LOG(wood -> new LogBlock(MaterialColor.SAND, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(0.5F).sound(SoundType.WOOD)), false),
        STRIPPED_LOG(wood -> new LogBlock(MaterialColor.WOOD, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        LEAVES(wood -> new LeavesBlock(Block.Properties.create(Material.LEAVES, wood.getMaterialColor()).hardnessAndResistance(0.5F).sound(SoundType.PLANT).tickRandomly().notSolid()), false),
        PLANKS(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new SaplingBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)), false),
        BOOKSHELF(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        TRAPDOOR(wood -> new TrapDoorBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        BUTTON(wood -> new WoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        SLAB(wood -> new SlabBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        STAIRS(wood -> new StairsBlock(() -> TFCBlocks.WOODS.get(wood).get(PLANKS).get().getDefaultState(), Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD).notSolid()) {}, true);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : LOG;
        }

        private final NonNullFunction<Default, Block> blockFactory;
        private final boolean isPlanksVariant;

        BlockType(NonNullFunction<Default, Block> blockFactory, boolean isPlanksVariant)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
        }

        public Supplier<Block> create(Default wood)
        {
            return () -> blockFactory.apply(wood);
        }

        public String id(Default wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name().toLowerCase() : "wood/" + name() + "/" + wood.name()).toLowerCase();
        }
    }
}
