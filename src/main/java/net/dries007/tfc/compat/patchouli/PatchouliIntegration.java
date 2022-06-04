/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliIntegration
{
    public static final ResourceLocation BOOK_ID = Helpers.identifier("field_guide");
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/book/icons.png");

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

    public static void registerMultiBlocks()
    {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();

        api.registerMultiblock(Helpers.identifier("pitkiln"), api.makeMultiblock(new String[][] {
                {"   ", " 0 ", "   "},
                {" S ", "SPS", " S "},
                {"   ", " S ", "   "}
            },
            '0', api.displayOnlyMatcher(Blocks.FIRE),
            'S', api.predicateMatcher(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.SANDY_LOAM).get(), state -> state.canOcclude() && state.getMaterial() != Material.WOOD),
            'P', api.stateMatcher(TFCBlocks.PIT_KILN.get().defaultBlockState().setValue(PitKilnBlock.STAGE, 15)),
            ' ', api.anyMatcher()
        ).setSymmetrical(true));

        final IStateMatcher bloomeryInsulation = api.predicateMatcher(TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.BRICKS).get(), BloomeryBlock::isBloomeryInsulationBlock);

        api.registerMultiblock(Helpers.identifier("bloomery"), api.makeMultiblock(new String[][] {
                {" S ", "SAS", " S "},
                {"SBS", "SAS", " S "},
                {" S ", " 0 ", "   "}
            },
            '0', bloomeryInsulation,
            'A', api.airMatcher(),
            'S', bloomeryInsulation,
            'B', api.predicateMatcher(TFCBlocks.BLOOMERY.get().defaultBlockState().setValue(BloomeryBlock.FACING, Direction.NORTH), state -> Helpers.isBlock(state, TFCBlocks.BLOOMERY.get())),
            ' ', api.anyMatcher()
        ));

        final Block sheetPile = TFCBlocks.SHEET_PILE.get();

        api.registerMultiblock(Helpers.identifier("blast_furnace"), api.makeMultiblock(new String[][] {
                {"  1  ", " 2S2 ", "1SAS1", " 2S2 ", "  1  "},
                {"     ", "     ", "  0  ", "     ", "     "},
            },
            '0', api.looseBlockMatcher(TFCBlocks.BLAST_FURNACE.get()),
            'A', api.airMatcher(),
            'S', api.predicateMatcher(TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.BRICKS).get(), BlastFurnaceBlock::isBlastFurnaceInsulationBlock),
            '1', api.predicateMatcher(sheetPile, state -> Helpers.isBlock(state, sheetPile) && SheetPileBlock.countSheets(state, Direction.Plane.HORIZONTAL) >= 1),
            '2', api.predicateMatcher(sheetPile, state -> Helpers.isBlock(state, sheetPile) && SheetPileBlock.countSheets(state, Direction.Plane.HORIZONTAL) >= 2)
        ));
    }
}
