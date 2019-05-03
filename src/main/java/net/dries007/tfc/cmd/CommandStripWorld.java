/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.cmd;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;

public class CommandStripWorld extends CommandBase
{
    @Override
    public String getName()
    {
        return "stripworld";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/stripworld <radius> -> [DANGER] Strips all stone variant blocks + water.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) throw new WrongUsageException("1 argument required.");
        int radius = parseInt(args[0], 1, 250);

        if (sender.getCommandSenderEntity() == null) return;

        final World world = sender.getEntityWorld();
        final BlockPos center = new BlockPos(sender.getCommandSenderEntity());

        final IBlockState fluidReplacement = Blocks.GLASS.getDefaultState();
        final IBlockState terrainReplacement = Blocks.AIR.getDefaultState();

        for (int x = -radius; x < radius; x++)
        {
            for (int z = -radius; z < radius; z++)
            {
                for (int y = 255 - center.getY(); y > -center.getY(); y--)
                {
                    final BlockPos pos = center.add(x, y, z);
                    final Block current = world.getBlockState(pos).getBlock();
                    if (current instanceof BlockFluidBase)
                        world.setBlockState(pos, fluidReplacement, 2);
                    else if (current instanceof BlockDynamicLiquid || current instanceof BlockStaticLiquid)
                        world.setBlockState(pos, fluidReplacement, 2);
                    else if (current instanceof BlockRockVariant)
                        world.setBlockState(pos, terrainReplacement, 2);
                }
            }
        }


        sender.sendMessage(new TextComponentString("Done."));
    }
}
