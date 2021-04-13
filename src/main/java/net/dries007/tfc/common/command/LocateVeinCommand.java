/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.dries007.tfc.world.feature.vein.Vein;
import net.dries007.tfc.world.feature.vein.VeinConfig;
import net.dries007.tfc.world.feature.vein.VeinFeature;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LocateVeinCommand
{
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_VEIN = new DynamicCommandExceptionType(args -> new TranslationTextComponent(MOD_ID + ".commands.locatevein.unknown_vein", args));
    public static final DynamicCommandExceptionType ERROR_VEIN_NOT_FOUND = new DynamicCommandExceptionType(args -> new TranslationTextComponent(MOD_ID + ".commands.locatevein.vein_not_found", args));

    private static Map<ResourceLocation, ConfiguredFeature<?, ? extends VeinFeature<?, ?>>> VEINS_CACHE;

    @SuppressWarnings("unchecked")
    public static Map<ResourceLocation, ConfiguredFeature<?, ? extends VeinFeature<?, ?>>> getVeins()
    {
        if (VEINS_CACHE == null)
        {
            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null)
            {
                final Registry<ConfiguredFeature<?, ?>> registry = server.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
                VEINS_CACHE = registry.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().feature instanceof VeinFeature)
                    .collect(Collectors.toMap(entry -> entry.getKey().location(), entry -> (ConfiguredFeature<?, ? extends VeinFeature<?, ?>>) entry.getValue()));
            }
            return Collections.emptyMap();
        }
        return VEINS_CACHE;
    }

    public static void clearCache()
    {
        VEINS_CACHE = null;
    }

    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("locatevein").requires(source -> source.hasPermission(2))
            .then(Commands.argument("vein", new VeinArgumentType())
                .executes(context -> locateVein(context, context.getArgument("vein", ResourceLocation.class)))
            );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int locateVein(CommandContext<CommandSource> context, ResourceLocation veinName) throws CommandSyntaxException
    {
        final ServerWorld world = context.getSource().getLevel();
        final BlockPos sourcePos = new BlockPos(context.getSource().getPosition());
        final ChunkPos pos = new ChunkPos(sourcePos);
        final ConfiguredFeature<?, ? extends VeinFeature<?, ?>> vein = LocateVeinCommand.getVeins().get(veinName);
        final ArrayList<? extends Vein> veins = new ArrayList<>();
        final Random random = new Random();
        final BiomeManager biomeManager = world.getBiomeManager().withDifferentSource(world.getChunkSource().getGenerator().getBiomeSource());
        final Function<BlockPos, Biome> biomeQuery = biomeManager::getBiome;
        for (int radius = 0; radius <= 16; radius++)
        {
            for (int dz = -radius; dz <= radius; dz++)
            {
                final boolean zEdge = Math.abs(dz) == radius;
                for (int dx = -radius; dx <= radius; dx++)
                {
                    boolean xEdge = Math.abs(dx) == radius;
                    if (!xEdge && !zEdge)
                    {
                        continue;
                    }

                    ((VeinFeature) vein.feature()).getVeinsAtChunk(world, pos.x + dx, pos.z + dz, veins, (VeinConfig) vein.config(), random, biomeQuery);
                    if (!veins.isEmpty())
                    {
                        final BlockPos veinPos = veins.get(0).getPos();
                        return showLocateResult(context.getSource(), veinName.toString(), sourcePos, veinPos, "commands.locate.success");
                    }
                }
            }
        }
        throw ERROR_VEIN_NOT_FOUND.create(veinName.toString());
    }

    /**
     * Modified from {@link LocateCommand#showLocateResult(CommandSource, String, BlockPos, BlockPos, String)} in order to also show the y position
     */
    private static int showLocateResult(CommandSource context, String nameOfThing, BlockPos source, BlockPos dest, String translationKey)
    {
        final int distance = (int) Math.sqrt(source.distSqr(dest));
        final ITextComponent text = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", dest.getX(), dest.getY(), dest.getZ()))
            .withStyle(styleIn -> styleIn.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + dest.getX() + " " + dest.getY() + " " + dest.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip"))));
        context.sendSuccess(new TranslationTextComponent(translationKey, nameOfThing, text, distance), false);
        return distance;
    }
}
