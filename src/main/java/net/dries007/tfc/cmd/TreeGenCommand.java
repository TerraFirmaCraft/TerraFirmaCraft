/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.cmd;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.TemplateManager;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.trees.ITreeGenerator;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TreeGenCommand extends CommandBase
{

    @Override
    public String getName()
    {
        return "maketree";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/maketree [wood] -> Grows a tree of the type specified";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1)
            throw new WrongUsageException("1 argument required.");

        Wood tree = Wood.valueOf(args[0].toUpperCase());

        if (sender.getCommandSenderEntity() == null) return;

        final World world = sender.getEntityWorld();
        final BlockPos center = new BlockPos(sender.getCommandSenderEntity());
        final TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();

        ITreeGenerator gen = tree.getTreeGenerator();
        gen.generateTree(manager, world, center, tree, world.rand);

    }
}
