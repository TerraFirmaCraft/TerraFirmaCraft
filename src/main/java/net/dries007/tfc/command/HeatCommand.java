/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dries007.tfc.api.capabilities.heat.CapabilityHeat;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class HeatCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("heat").requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                    .executes(cmd -> heatItem(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "value")))));
    }

    private static int heatItem(CommandSource source, int value) throws CommandSyntaxException
    {
        final ServerPlayerEntity player = source.asPlayer();
        final ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty())
        {
            stack.getCapability(CapabilityHeat.CAPABILITY).ifPresent(heat ->
            {
                heat.setTemperature(value);
                source.sendFeedback(new TranslationTextComponent(MOD_ID + ".command.heat", value), true);
                stack.setCount(2);
            });
        }
        return 1;
    }
}
