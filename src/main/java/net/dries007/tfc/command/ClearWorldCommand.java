/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.world.vein.VeinType;
import net.dries007.tfc.world.vein.VeinTypeManager;

public final class ClearWorldCommand
{
    public static final String STARTING = "tfc.command.clear_world.starting";
    public static final String DONE = "tfc.command.clear_world.done";

    private static final Set<BlockState> VEIN_STATES = new HashSet<>();

    public static void resetVeinStates()
    {
        VeinTypeManager.INSTANCE.getValues().stream().map(VeinType::getOreStates).forEach(VEIN_STATES::addAll);
    }

    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("clearworld")
            .requires(source -> source.hasPermissionLevel(2))
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250)).executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius")))
            );
    }

    private static int clearWorld(CommandSource source, int radius)
    {
        source.sendFeedback(new TranslationTextComponent(STARTING), true);
        final World world = source.getWorld();
        final BlockPos center = new BlockPos(source.getPos());
        final BlockState air = Blocks.AIR.getDefaultState();

        for (BlockPos pos : BlockPos.Mutable.getAllInBoxMutable(center.add(-radius, 255 - center.getY(), -radius), center.add(radius, -center.getY(), radius)))
        {
            if (!VEIN_STATES.contains(world.getBlockState(pos)))
            {
                world.setBlockState(pos, air, 2 | 16);
            }
        }

        source.sendFeedback(new TranslationTextComponent(DONE), true);
        return Command.SINGLE_SUCCESS;
    }
}
