/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeSource;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.dries007.tfc.world.biome.*;

public class TFCLocateCommand
{
    private static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType(id -> new TranslatableComponent("tfc.commands.locate.invalid_biome", id));
    private static final SimpleCommandExceptionType ERROR_INVALID_BIOME_SOURCE = new SimpleCommandExceptionType(new TranslatableComponent("tfc.commands.locate.invalid_biome_source"));
    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(id -> new TranslatableComponent("tfc.commands.locate.not_found", id));
    private static final SimpleCommandExceptionType ERROR_VOLCANO_NOT_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("tfc.commands.locate.volcano_not_found"));

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("locate")
            .requires(source -> source.hasPermission(2))
                .then(Commands.literal("biome")
                    .then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(TFCCommands.TFC_BIOMES.get())
                        .executes(context -> locateBiome(context.getSource(), context.getArgument("biome", ResourceLocation.class)))
                    )
                )
                .then(Commands.literal("volcano")
                    .executes(context -> locateVolcano(context.getSource()))
                );
    }

    private static int locateBiome(CommandSourceStack source, ResourceLocation id) throws CommandSyntaxException
    {
        final BiomeSource biomeSource = source.getLevel().getChunkSource().getGenerator().getBiomeSource();
        if (!(biomeSource instanceof final BiomeSourceExtension biomeSourceExtension))
        {
            throw ERROR_INVALID_BIOME_SOURCE.create();
        }

        final BiomeVariants variants = TFCBiomes.getById(id);
        if (variants == null)
        {
            throw ERROR_INVALID_BIOME.create(id);
        }

        final BlockPos center = new BlockPos(source.getPosition());
        final BlockPos result = radialSearch(QuartPos.fromBlock(center.getX()), QuartPos.fromBlock(center.getZ()), 1024, 16, (x, z) -> {
            final BiomeVariants found = biomeSourceExtension.getNoiseBiomeVariants(x, z);
            if (found == variants)
            {
                return new BlockPos(QuartPos.fromSection(x), 0, QuartPos.fromSection(z));
            }
            return null;
        });

        if (result == null)
        {
            throw ERROR_NOT_FOUND.create(id);
        }
        return showRawLocateResult(source, id.toString(), center, result, "commands.locate.success");
    }

    private static int locateVolcano(CommandSourceStack source) throws CommandSyntaxException
    {
        final BiomeSource biomeSource = source.getLevel().getChunkSource().getGenerator().getBiomeSource();
        if (!(biomeSource instanceof final BiomeSourceExtension biomeSourceExtension))
        {
            throw ERROR_INVALID_BIOME_SOURCE.create();
        }

        final VolcanoNoise volcanoNoise = new VolcanoNoise(source.getLevel().getSeed());
        final BlockPos center = new BlockPos(source.getPosition());
        final BlockPos result = radialSearch(center.getX(), center.getZ(), 1024, 16, (x, z) -> {
            final BlockPos volcanoPos = volcanoNoise.calculateCenter(x, 0, z, 1); // Sample with rarity 1 first, to always include the cell
            if (volcanoPos != null)
            {
                // Sample the biome at that volcano position and verify the center exists
                final BiomeVariants found = biomeSourceExtension.getNoiseBiomeVariants(QuartPos.fromBlock(volcanoPos.getX()), QuartPos.fromBlock(volcanoPos.getZ()));
                final BlockPos newFound = volcanoNoise.calculateCenter(x, 0, z, found.getVolcanoRarity());
                if (found.isVolcanic() && newFound != null)
                {
                    return newFound;
                }
            }
            return null;
        });

        if (result == null)
        {
            throw ERROR_VOLCANO_NOT_FOUND.create();
        }
        return showRawLocateResult(source, "volcano", center, result, "commands.locate.success");
    }

    private static int showRawLocateResult(CommandSourceStack source, String name, BlockPos original, BlockPos found, String key)
    {
        int distance = Mth.floor(dist(original.getX(), original.getZ(), found.getX(), found.getZ()));
        Component component = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", found.getX(), "~", found.getZ())).withStyle((p_207527_) -> {
            return p_207527_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + found.getX() + " ~ " + found.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip")));
        });
        source.sendSuccess(new TranslatableComponent(key, name, component, distance), false);
        return distance;
    }

    private static float dist(int x1, int z1, int x2, int z2)
    {
        final int dx = x2 - x1;
        final int dz = z2 - z1;
        return Mth.sqrt((float)(dx * dx + dz * dz));
    }

    @Nullable
    private static BlockPos radialSearch(int x, int z, int radius, int step, SearchFunction function)
    {
        // r = [1, radius)
        // d = [0, 2r)
        // Example at r = 2 (d = [0, 3]):
        // a a a a b  +x ->
        // c . . . b  +z
        // c . x . b   |
        // c . . . b   v
        // c d d d d

        BlockPos pos;
        for (int r = 1; r < radius; r++)
        {
            for (int d = 0; d < 2 * r; d++)
            {
                // a, b, c, d
                pos = function.find(x + step * (d - r), z + step * -r);
                if (pos != null)
                {
                    return pos;
                }
                pos = function.find(x + step * r, z + step * (d - r));
                if (pos != null)
                {
                    return pos;
                }
                pos = function.find(x + step * -r, z + step * (d + 1 - r));
                if (pos != null)
                {
                    return pos;
                }
                pos = function.find(x + step * (d + 1 - r), z + step * r);
                if (pos != null)
                {
                    return pos;
                }
            }
        }
        return null;
    }

    @FunctionalInterface
    interface SearchFunction
    {
        @Nullable
        BlockPos find(int x, int z);
    }
}
