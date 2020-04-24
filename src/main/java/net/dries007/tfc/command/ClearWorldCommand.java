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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dries007.tfc.world.vein.VeinType;
import net.dries007.tfc.world.vein.VeinTypeManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class ClearWorldCommand
{
    private static final Set<BlockState> VEIN_STATES = new HashSet<>();

    public static void resetVeinStates()
    {
        VeinTypeManager.INSTANCE.getValues().stream().map(VeinType::getOreStates).forEach(VEIN_STATES::addAll);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("clearworld").requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                    .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius")))));
    }

    private static int clearWorld(CommandSource source, int radius)
    {
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

        source.sendFeedback(new TranslationTextComponent(MOD_ID + ".command.clear_world_done"), true);
        return 1;
    }
}
