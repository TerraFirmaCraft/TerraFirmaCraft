/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
public class CommandSkill extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "skill";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/skill [add|set] <skill> <level> -> adds/sets the skill to <level>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("3 arguments required: " + getUsage(sender));
        }

        double level = parseDouble(args[2], 0, 4);
        SkillType<Skill> inputSkill = SkillType.get(args[1].toLowerCase(), Skill.class);

        if (inputSkill == null)
        {
            throw new WrongUsageException("No skill with name '" + args[1] + "' has been found");
        }

        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            Skill skill = CapabilityPlayerData.getSkill(player, inputSkill);
            if (skill != null)
            {
                if (args[0].equals("set"))
                {
                    skill.setTotalLevel(level / 4.0D);
                    sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.skill_set", inputSkill.getName(), level));
                }
                else if (args[0].equals("add"))
                {
                    skill.addTotalLevel(level / 4.0D);
                    sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.skill_add", level, inputSkill.getName()));
                }
                else
                {
                    throw new WrongUsageException("first argument must be set or add.");
                }
            }
        }
        else
        {
            throw new WrongUsageException("Can only be used by a player");
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}
