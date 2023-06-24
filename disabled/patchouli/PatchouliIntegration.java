/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.logging.LogUtils;
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
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import org.slf4j.Logger;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliIntegration
{
    public static final ResourceLocation BOOK_ID = Helpers.identifier("field_guide");
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/book/icons.png");

    private static final Logger LOGGER = LogUtils.getLogger();

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
    }

    private static IMultiblock blastFurnace(PatchouliAPI.IPatchouliAPI api, boolean fullSize)
    {
        final Block sheetPile = TFCBlocks.SHEET_PILE.get();
        final Function<Direction, IStateMatcher> oneSheet = face -> api.predicateMatcher(sheetPile.defaultBlockState().setValue(DirectionPropertyBlock.getProperty(face), true), state -> Helpers.isBlock(state, sheetPile) && SheetPileBlock.countSheets(state, Direction.Plane.HORIZONTAL) >= 1);
        final BiFunction<Direction, Direction, IStateMatcher> twoSheets = (face1, face2) -> api.predicateMatcher(sheetPile.defaultBlockState().setValue(DirectionPropertyBlock.getProperty(face1), true).setValue(DirectionPropertyBlock.getProperty(face2), true), state -> Helpers.isBlock(state, sheetPile) && SheetPileBlock.countSheets(state, Direction.Plane.HORIZONTAL) >= 2);

        //        ^ W
        //   1    |
        //  2.3   +-> S
        // 4...5
        //  6.7
        //   8
        final String[][] pattern = fullSize ?
            new String[][] {
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"     ", "     ", "  0B ", "     ", "     "},
                {"     ", "     ", "  C  ", "     ", "     "},
            } :
            new String[][] {
                {"  1  ", " 2S3 ", "4SAS5", " 6S7 ", "  8  "},
                {"     ", "     ", "  0  ", "     ", "     "},
            };

        final IMultiblock multiblock = api.makeMultiblock(pattern,
            '0', api.looseBlockMatcher(TFCBlocks.BLAST_FURNACE.get()),
            ' ', api.anyMatcher(),
            'A', api.airMatcher(),
            'S', api.predicateMatcher(TFCBlocks.FIRE_BRICKS.get(), BlastFurnaceBlock::isBlastFurnaceInsulationBlock),
            '1', oneSheet.apply(Direction.EAST),
            '2', twoSheets.apply(Direction.EAST, Direction.SOUTH),
            '3', twoSheets.apply(Direction.EAST, Direction.NORTH),
            '4', oneSheet.apply(Direction.SOUTH),
            '5', oneSheet.apply(Direction.NORTH),
            '6', twoSheets.apply(Direction.WEST, Direction.SOUTH),
            '7', twoSheets.apply(Direction.WEST, Direction.NORTH),
            '8', oneSheet.apply(Direction.WEST),
            'B', api.looseBlockMatcher(TFCBlocks.BELLOWS.get()),
            'C', api.looseBlockMatcher(TFCBlocks.CRUCIBLE.get())
        );

        sneakIntoMultiblock(multiblock).ifPresent(access -> {
            final Metal wroughtIron = new Metal(Metal.WROUGHT_IRON_ID);
            for (int x = 0; x < 5; x++)
            {
                for (int z = 0; z < 5; z++)
                {
                    if (fullSize)
                    {
                        for (int y = 2; y <= 6; y++)
                        {
                            access.getBlockEntity(new BlockPos(x, y, z), TFCBlockEntities.SHEET_PILE.get()).ifPresent(pile -> pile.setAllMetalsFromOutsideWorld(wroughtIron));
                        }
                    }
                    else
                    {
                        access.getBlockEntity(new BlockPos(x, 1, z), TFCBlockEntities.SHEET_PILE.get()).ifPresent(pile -> pile.setAllMetalsFromOutsideWorld(wroughtIron));
                    }
                }
            }
        });

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

        sneakIntoMultiblock(multiblock).ifPresent(access -> {
            for (BlockPos pos : new BlockPos[] {new BlockPos(1, 0, 0), new BlockPos(0, 0, 1)})
            {
                access.getBlockEntity(pos, TFCBlockEntities.ANVIL.get()).ifPresent(anvil -> anvil.setInventoryFromOutsideWorld(
                    new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.COPPER).get(Metal.ItemType.INGOT).get()),
                    new ItemStack(TFCItems.ROCK_TOOLS.get(RockCategory.IGNEOUS_EXTRUSIVE).get(RockCategory.ItemType.HAMMER).get()),
                    new ItemStack(TFCItems.POWDERS.get(Powder.FLUX).get())
                ));
            }
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
