/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Map;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.data.worldgen.BootstrapContext;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.world.settings.RockSettings;

import static net.dries007.tfc.common.blocks.rock.Rock.ANDESITE;
import static net.dries007.tfc.common.blocks.rock.Rock.BASALT;
import static net.dries007.tfc.common.blocks.rock.Rock.CHALK;
import static net.dries007.tfc.common.blocks.rock.Rock.CHERT;
import static net.dries007.tfc.common.blocks.rock.Rock.CLAYSTONE;
import static net.dries007.tfc.common.blocks.rock.Rock.CONGLOMERATE;
import static net.dries007.tfc.common.blocks.rock.Rock.DACITE;
import static net.dries007.tfc.common.blocks.rock.Rock.DIORITE;
import static net.dries007.tfc.common.blocks.rock.Rock.DOLOMITE;
import static net.dries007.tfc.common.blocks.rock.Rock.GABBRO;
import static net.dries007.tfc.common.blocks.rock.Rock.GNEISS;
import static net.dries007.tfc.common.blocks.rock.Rock.GRANITE;
import static net.dries007.tfc.common.blocks.rock.Rock.LIMESTONE;
import static net.dries007.tfc.common.blocks.rock.Rock.MARBLE;
import static net.dries007.tfc.common.blocks.rock.Rock.PHYLLITE;
import static net.dries007.tfc.common.blocks.rock.Rock.QUARTZITE;
import static net.dries007.tfc.common.blocks.rock.Rock.RHYOLITE;
import static net.dries007.tfc.common.blocks.rock.Rock.SCHIST;
import static net.dries007.tfc.common.blocks.rock.Rock.SHALE;
import static net.dries007.tfc.common.blocks.rock.Rock.SLATE;
import static net.dries007.tfc.common.blocks.rock.Rock.TUFF;

public class BuiltinRockSettings
{

    public BuiltinRockSettings(BootstrapContext<RockSettings> context)
    {
        for (Rock rock : Rock.VALUES)
        {
            context.register(BuiltinWorldPreset.rockKey(rock), makeSetting(rock));
        }
    }

    public static RockSettings makeSetting(Rock rock)
    {
        final var blocks = TFCBlocks.ROCK_BLOCKS.get(rock);
        final var color = ROCK_TO_SAND_COLOR.get(rock);
        return new RockSettings(
            blocks.get(Rock.BlockType.RAW).get(),
            blocks.get(Rock.BlockType.HARDENED).get(),
            blocks.get(Rock.BlockType.GRAVEL).get(),
            blocks.get(Rock.BlockType.COBBLE).get(),
            TFCBlocks.SAND.get(color).get(),
            TFCBlocks.SANDSTONE.get(color).get(SandstoneBlockType.RAW).get(),
            Optional.of(blocks.get(Rock.BlockType.SPIKE).get()),
            Optional.of(blocks.get(Rock.BlockType.LOOSE).get()),
            Optional.of(blocks.get(Rock.BlockType.MOSSY_LOOSE).get()),
            Optional.of(ROCK_SET_KARST.get(rock)),
            Optional.of(ROCK_SET_MAFIC.get(rock))
        );
    }

    private static final Map<Rock, SandBlockType> ROCK_TO_SAND_COLOR = ImmutableMap.<Rock, SandBlockType>builder()
        .put(GRANITE, SandBlockType.YELLOW)
        .put(DIORITE, SandBlockType.RED)
        .put(GABBRO, SandBlockType.BLACK)
        .put(RHYOLITE, SandBlockType.YELLOW)
        .put(DACITE, SandBlockType.RED)
        .put(ANDESITE, SandBlockType.RED)
        .put(BASALT, SandBlockType.BLACK)
        .put(SHALE, SandBlockType.BROWN)
        .put(CLAYSTONE, SandBlockType.BROWN)
        .put(LIMESTONE, SandBlockType.WHITE)
        .put(CONGLOMERATE, SandBlockType.BROWN)
        .put(DOLOMITE, SandBlockType.WHITE)
        .put(CHERT, SandBlockType.RED)
        .put(CHALK, SandBlockType.WHITE)
        .put(TUFF, SandBlockType.GREEN)
        .put(QUARTZITE, SandBlockType.WHITE)
        .put(SLATE, SandBlockType.YELLOW)
        .put(PHYLLITE, SandBlockType.YELLOW)
        .put(SCHIST, SandBlockType.YELLOW)
        .put(GNEISS, SandBlockType.YELLOW)
        .put(MARBLE, SandBlockType.WHITE)
        .build();

    private static final Map<Rock, Boolean> ROCK_SET_KARST = ImmutableMap.<Rock, Boolean>builder()
        .put(GRANITE, false)
        .put(DIORITE, false)
        .put(GABBRO, false)
        .put(SHALE, false)
        .put(CLAYSTONE, false)
        .put(LIMESTONE, true)
        .put(CONGLOMERATE, false)
        .put(DOLOMITE, true)
        .put(CHERT, false)
        .put(CHALK, true)
        .put(TUFF, false)
        .put(RHYOLITE, false)
        .put(BASALT, false)
        .put(ANDESITE, false)
        .put(DACITE, false)
        .put(QUARTZITE, false)
        .put(SLATE, false)
        .put(PHYLLITE, false)
        .put(SCHIST, false)
        .put(GNEISS, false)
        .put(MARBLE, true)
        .build();

    // Used by badlands to determine whether they should have black sand
    private static final Map<Rock, Boolean> ROCK_SET_MAFIC = ImmutableMap.<Rock, Boolean>builder()
        .put(GRANITE, false)
        .put(DIORITE, false)
        .put(GABBRO, true)
        .put(SHALE, false)
        .put(CLAYSTONE, false)
        .put(LIMESTONE, false)
        .put(CONGLOMERATE, false)
        .put(DOLOMITE, false)
        .put(CHERT, false)
        .put(CHALK, false)
        .put(TUFF, false)
        .put(RHYOLITE, false)
        .put(BASALT, true)
        .put(ANDESITE, false)
        .put(DACITE, false)
        .put(QUARTZITE, false)
        .put(SLATE, false)
        .put(PHYLLITE, false)
        .put(SCHIST, false)
        .put(GNEISS, false)
        .put(MARBLE, false)
        .build();
}
