/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.dries007.tfc.world.feature.vein.Vein;
import net.dries007.tfc.world.feature.vein.VeinConfig;
import net.dries007.tfc.world.feature.vein.VeinFeature;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

// todo: subcommand of new TFCLocate command?
public class LocateVeinCommand
{
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_VEIN = new DynamicCommandExceptionType(args -> new TranslatableComponent(MOD_ID + ".commands.locatevein.unknown_vein", args));
    public static final DynamicCommandExceptionType ERROR_VEIN_NOT_FOUND = new DynamicCommandExceptionType(args -> new TranslatableComponent(MOD_ID + ".commands.locatevein.vein_not_found", args));

    @Nullable private static Map<ResourceLocation, ConfiguredFeature<?, ? extends VeinFeature<?, ?>>> VEINS_CACHE = null;

    public static Map<ResourceLocation, ConfiguredFeature<?, ? extends VeinFeature<?, ?>>> getVeins()
    {
        return VEINS_CACHE == null ? Collections.emptyMap() : VEINS_CACHE;
    }

    @SuppressWarnings("unchecked")
    public static void reloadVeinsCache(MinecraftServer server)
    {
        final Registry<ConfiguredFeature<?, ?>> registry = server.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        VEINS_CACHE = registry.entrySet()
            .stream()
            .filter(entry -> entry.getValue().feature() instanceof VeinFeature)
            .collect(Collectors.toMap(entry -> entry.getKey().location(), entry -> (ConfiguredFeature<?, ? extends VeinFeature<?, ?>>) entry.getValue()));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("locatevein").requires(source -> source.hasPermission(2))
            .then(Commands.argument("vein", new VeinArgumentType())
                .executes(context -> locateVein(context, context.getArgument("vein", ResourceLocation.class)))
            );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int locateVein(CommandContext<CommandSourceStack> context, ResourceLocation veinName) throws CommandSyntaxException
    {
        final ServerLevel world = context.getSource().getLevel();
        final BlockPos sourcePos = new BlockPos(context.getSource().getPosition());
        final ChunkPos pos = new ChunkPos(sourcePos);
        final ConfiguredFeature<?, ? extends VeinFeature<?, ?>> vein = LocateVeinCommand.getVeins().get(veinName);
        final ArrayList<? extends Vein> veins = new ArrayList<>();
        final Random random = new Random();
        final BiomeSource source = world.getChunkSource().getGenerator().getBiomeSource();
        final Climate.Sampler sampler = world.getChunkSource().getGenerator().climateSampler();
        final BiomeManager biomeManager = world.getBiomeManager().withDifferentSource((x, y, z) -> source.getNoiseBiome(x, y, z, sampler));
        final WorldGenerationContext generationContext = new WorldGenerationContext(world.getChunkSource().getGenerator(), world);
        final Function<BlockPos, Holder<Biome>> biomeQuery = biomeManager::getBiome;
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

                    ((VeinFeature) vein.feature()).getVeinsAtChunk(world, generationContext, pos.x + dx, pos.z + dz, veins, (VeinConfig) vein.config(), random, biomeQuery);
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
     * Modified from {@link LocateCommand#showLocateResult(CommandSourceStack, String, BlockPos, BlockPos, String)} in order to also show the y position
     */
    private static int showLocateResult(CommandSourceStack context, String nameOfThing, BlockPos source, BlockPos dest, String translationKey)
    {
        final int distance = (int) Math.sqrt(source.distSqr(dest));
        final Component text = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", dest.getX(), dest.getY(), dest.getZ()))
            .withStyle(styleIn -> styleIn.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + dest.getX() + " " + dest.getY() + " " + dest.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
        context.sendSuccess(new TranslatableComponent(translationKey, nameOfThing, text, distance), false);
        return distance;
    }
}
