/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.ClutchBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.MetalItem;

public final class PatchouliIntegration
{
    public static final ResourceLocation BOOK_ID = Helpers.identifier("field_guide");
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/book/icons.png");

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ItemStack getFieldGuide(boolean useHotReloadedBook)
    {
        return PatchouliAPI.get().getBookStack(useHotReloadedBook ? Helpers.resourceLocation("patchouli", "field_guide") : BOOK_ID);
    }

    /**
     * Does not detect if the nod Patchouli is present (as we hard depend on it). Only detects if the client wants to see it, effectively hiding it even if we do depend on it.
     */
    public static void ifEnabled(Runnable action)
    {
        if (TFCConfig.CLIENT.showGuideBookTabInInventory.get())
        {
            action.run();
        }
    }

    public static void openGui(ServerPlayer player)
    {
        PatchouliAPI.get().openBookGUI(player, BOOK_ID);
    }

    public static void openGui(ServerPlayer player, ResourceLocation entry, int page)
    {
        PatchouliAPI.get().openBookEntry(player, BOOK_ID, entry, page);
    }

    public static void registerMultiBlocks()
    {
        registerMultiblock("bloomery", PatchouliIntegration::bloomery);
        registerMultiblock("blast_furnace", api -> blastFurnace(api, false));
        registerMultiblock("full_blast_furnace", api -> blastFurnace(api, true));
        registerMultiblock("rock_anvil", PatchouliIntegration::rockAnvil);
        registerMultiblock("charcoal_forge", PatchouliIntegration::charcoalForge);
        registerMultiblock("windmill", PatchouliIntegration::windmill);
        registerMultiblock("water_wheel", PatchouliIntegration::waterWheel);
        registerMultiblock("clutch_off", api -> clutch(api, false));
        registerMultiblock("clutch_on", api -> clutch(api, true));
        registerMultiblock("crankshaft", PatchouliIntegration::crankshaft);
        registerMultiblock("rotating_quern", PatchouliIntegration::rotatingQuern);
    }

    private static IMultiblock blastFurnace(PatchouliAPI.IPatchouliAPI api, boolean fullSize)
    {
        //        ^ W
        //   1    |
        //  2.3   +-> S
        // 4...5
        //  6.7
        //   8
        final String[][] pattern = fullSize ?
            new String[][] {
                { "RRR", "R R", "RRR"},
                { "RRR", "R R", "RRR"},
                { "RRR", "R R", "RRR"},
                { "RRR", "R R", "RRR"},
                { "RRR", "R R", "RRR"},
                {"   ", " 0B", "   "},
                {"   ", " C ", "   "},
            } :
            new String[][] {
                { "RRR", "R R", "RRR"},
                {"   ", " 0 ", "   "},
            };

        final IMultiblock multiblock = api.makeMultiblock(pattern,
            '0', api.looseBlockMatcher(TFCBlocks.BLAST_FURNACE.get()),
            ' ', api.anyMatcher(),
            'R', api.predicateMatcher(TFCBlocks.REINFORCED_FIRE_BRICKS.get(), BlastFurnaceBlock::isBlastFurnaceInsulationBlock),
            'B', api.looseBlockMatcher(TFCBlocks.BELLOWS.get()),
            'C', api.looseBlockMatcher(TFCBlocks.CRUCIBLE.get())
        );
        return multiblock;
    }

    private static IMultiblock bloomery(PatchouliAPI.IPatchouliAPI api)
    {
        final IStateMatcher bloomeryInsulation = api.predicateMatcher(TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.BRICKS).get(), BloomeryBlock::isBloomeryInsulationBlock);

        return api.makeMultiblock(new String[][] {
                {" S ", "SAS", " S "},
                {"SBS", "SAS", " S "},
                {" S ", " 0 ", "   "}
            },
            '0', bloomeryInsulation,
            'A', api.airMatcher(),
            'S', bloomeryInsulation,
            'B', api.predicateMatcher(TFCBlocks.BLOOMERY.get().defaultBlockState().setValue(BloomeryBlock.FACING, Direction.NORTH), state -> Helpers.isBlock(state, TFCBlocks.BLOOMERY.get())),
            ' ', api.anyMatcher()
        );
    }

    private static IMultiblock charcoalForge(PatchouliAPI.IPatchouliAPI api)
    {
        final IStateMatcher forgeInsulation = api.predicateMatcher(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.COBBLE).get(), CharcoalForgeBlock::isForgeInsulationBlock);
        final BlockState charcoalPile = TFCBlocks.CHARCOAL_PILE.get().defaultBlockState();

        return api.makeMultiblock(new String[][] {
                {" S ", "S0S", " S "},
                {"   ", " S ", "   "}
            },
            '0', api.predicateMatcher(charcoalPile.setValue(CharcoalPileBlock.LAYERS, 7), state -> Helpers.isBlock(state, TFCBlocks.CHARCOAL_PILE.get()) && state.getValue(CharcoalPileBlock.LAYERS) >= 7),
            ' ', api.anyMatcher(),
            'S', forgeInsulation
        );
    }

    private static IMultiblock rockAnvil(PatchouliAPI.IPatchouliAPI api)
    {
        final IMultiblock multiblock = api.makeMultiblock(new String[][] {
                {" 0 "}, {"RAR"}
            },
            '0', api.airMatcher(),
            ' ', api.anyMatcher(),
            'R', api.strictBlockMatcher(TFCBlocks.ROCK_BLOCKS.get(Rock.GABBRO).get(Rock.BlockType.RAW).get()),
            'A', api.strictBlockMatcher(TFCBlocks.ROCK_ANVILS.get(Rock.GABBRO).get())
        );

        sneakIntoMultiblock(multiblock)
            .flatMap(access -> access.getBlockEntity(new BlockPos(0, 0, 1), TFCBlockEntities.ANVIL.get()))
            .ifPresent(anvil -> anvil.setInventoryFromOutsideWorld(
                    new ItemStack(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.INGOT).get()),
                    new ItemStack(TFCItems.ROCK_TOOLS.get(RockCategory.IGNEOUS_EXTRUSIVE).get(RockCategory.ItemType.HAMMER).get()),
                    new ItemStack(TFCItems.POWDERS.get(Powder.FLUX).get())
                )
            );

        return multiblock;
    }

    private static IMultiblock windmill(PatchouliAPI.IPatchouliAPI api)
    {
        final IMultiblock multiblock = api.makeMultiblock(new String[][] {
                {"             "},
                {"             "},
                {"             "},
                {"             "},
                {"             "},
                {"             "},
                {"      0      "},
                {"             "},
                {"             "},
                {"             "},
                {"             "},
                {"             "},
                {"             "}
            },
            '0', api.predicateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.WINDMILL).get().defaultBlockState().setValue(WindmillBlock.COUNT, 5), state -> state.getBlock() instanceof WindmillBlock),
            ' ', api.airMatcher()
        );

        sneakIntoMultiblock(multiblock)
            .flatMap(access -> access.getBlockEntity(new BlockPos(0, 6, 6), TFCBlockEntities.WINDMILL.get()))
            .ifPresent(entity -> entity.getRotationNode().setRotationFromOutsideWorld());

        return multiblock;
    }

    private static IMultiblock waterWheel(PatchouliAPI.IPatchouliAPI api)
    {
        final IMultiblock multiblock = api.makeMultiblock(new String[][] {
                {"     "},
                {"     "},
                {"  0  "},
                {"WWWWW"},
                {"WWWWW"},
            },
            '0', api.stateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.WATER_WHEEL).get().defaultBlockState()),
            'W', api.predicateMatcher(Blocks.WATER, state -> state.getFluidState().getType() == Fluids.WATER),
            ' ', api.airMatcher()
        );

        sneakIntoMultiblock(multiblock)
            .flatMap(access -> access.getBlockEntity(new BlockPos(0, 2, 2), TFCBlockEntities.WATER_WHEEL.get()))
            .ifPresent(entity -> entity.getRotationNode().setRotationFromOutsideWorld());

        return multiblock;
    }

    private static IMultiblock clutch(PatchouliAPI.IPatchouliAPI api, boolean powered)
    {
        final IMultiblock multiblock = api.makeMultiblock(new String[][] {
                {"     ", "     ", "     "},
                {"  RL ", "  0  ", "AACAA"}
            },
            'L', api.stateMatcher(Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH).setValue(LeverBlock.FACE, AttachFace.FLOOR).setValue(LeverBlock.POWERED, powered)),
            'R', api.stateMatcher(Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE).setValue(RedStoneWireBlock.WEST, RedstoneSide.NONE).setValue(RedStoneWireBlock.POWER, powered ? 15 : 0)),
            '0', api.stateMatcher(Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.NONE).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE).setValue(RedStoneWireBlock.POWER, powered ? 14 : 0)),
            'C', api.stateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.CLUTCH).get().defaultBlockState().setValue(ClutchBlock.AXIS, Direction.Axis.Z).setValue(ClutchBlock.POWERED, powered)),
            'A', api.stateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.AXLE).get().defaultBlockState().setValue(AxleBlock.AXIS, Direction.Axis.Z)),
            ' ', api.airMatcher()
        );

        sneakIntoMultiblock(multiblock).ifPresent(access -> {
            for (int z = 0; z < (powered ? 3 : 5); z++)
            {
                access.getBlockEntity(new BlockPos(2, 0, z), TFCBlockEntities.AXLE.get()).ifPresent(axle -> axle.getRotationNode().setRotationFromOutsideWorld());
            }
        });

        return multiblock;
    }

    private static IMultiblock crankshaft(PatchouliAPI.IPatchouliAPI api)
    {
        final IMultiblock multiblock = api.makeMultiblock(new String[][] {
                {" A ", "S0 ", "   "}
            },
            'A', api.stateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.AXLE).get().defaultBlockState().setValue(AxleBlock.AXIS, Direction.Axis.X)),
            '0', api.stateMatcher(TFCBlocks.CRANKSHAFT.get().defaultBlockState().setValue(CrankshaftBlock.PART, CrankshaftBlock.Part.BASE).setValue(CrankshaftBlock.FACING, Direction.NORTH)),
            'S', api.stateMatcher(TFCBlocks.CRANKSHAFT.get().defaultBlockState().setValue(CrankshaftBlock.PART, CrankshaftBlock.Part.SHAFT).setValue(CrankshaftBlock.FACING, Direction.NORTH)),
            ' ', api.airMatcher()
        );

        sneakIntoMultiblock(multiblock).ifPresent(access -> {
            access.getBlockEntity(new BlockPos(0, 0, 1), TFCBlockEntities.AXLE.get()).ifPresent(axle -> axle.getRotationNode().setRotationFromOutsideWorld());
            access.getBlockEntity(new BlockPos(1, 0, 1), TFCBlockEntities.CRANKSHAFT.get()).ifPresent(shaft -> shaft.getRotationNode().setRotationFromOutsideWorld());
            access.getBlockEntity(new BlockPos(1, 0, 0), TFCBlockEntities.CRANKSHAFT.get()).ifPresent(shaft -> shaft.getRotationNode().setRotationFromOutsideWorld());
        });

        return multiblock;
    }

    private static IMultiblock rotatingQuern(PatchouliAPI.IPatchouliAPI api)
    {
        final IMultiblock multiblock = api.makeMultiblock(
            new String[][] {{"T"}, {"0"}, {" "}},
            ' ', api.airMatcher(),
            '0', api.stateMatcher(TFCBlocks.QUERN.get().defaultBlockState()),
            'T', api.stateMatcher(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.AXLE).get().defaultBlockState().setValue(AxleBlock.AXIS, Direction.Axis.Y)));

        sneakIntoMultiblock(multiblock).ifPresent(access -> {
            access.getBlockEntity(new BlockPos(0, 1, 0), TFCBlockEntities.QUERN.get()).ifPresent(quern -> {
                quern.getRotationNode().setRotationFromOutsideWorld();
                quern.setHandstoneFromOutsideWorld();
            });
            access.getBlockEntity(new BlockPos(0, 2, 0), TFCBlockEntities.AXLE.get()).ifPresent(axle -> axle.getRotationNode().setRotationFromOutsideWorld());
        });

        return multiblock;
    }

    private static void registerMultiblock(String name, Function<PatchouliAPI.IPatchouliAPI, IMultiblock> factory)
    {
        final PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        api.registerMultiblock(Helpers.identifier(name), factory.apply(api));
    }

    /**
     * Non-API
     */
    private static Optional<BlockGetter> sneakIntoMultiblock(IMultiblock multiblock)
    {
        if (multiblock instanceof BlockGetter access)
        {
            return Optional.of(access);
        }
        LOGGER.warn("Multiblock of concrete type {} is not a {}, multiblock will be disfigured!", multiblock.getClass().getName(), BlockGetter.class.getName());
        return Optional.empty();
    }
}
