/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
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
    }
}
