/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.Map;
import java.util.function.Supplier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.server.command.EnumArgument;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.data.Metal;


public class AddTrimCommand
{
    private static final String FAIL_NOT_APPLIED = "tfc.commands.trim.not_applied";
    private static final String FAIL_NOT_ARMOR = "tfc.commands.trim.not_armor";
    private static final String FAIL_BAD_MATERIAL = "tfc.commands.trim.bad_material";
    private static final String FAIL_BAD_TEMPLATE = "tfc.commands.trim.bad_template";

    public static LiteralArgumentBuilder<CommandSourceStack> create(CommandBuildContext buildContext)
    {
        return Commands.literal("trim").requires(source -> source.hasPermission(2))
            .then(Commands.argument("material", ItemArgument.item(buildContext))
                .then(Commands.argument("pattern", ItemArgument.item(buildContext))
                    .executes(context -> addTrim(context.getSource(), context.getArgument("material", ItemInput.class).getItem(), context.getArgument("pattern", ItemInput.class).getItem(), context.getSource().getPlayerOrException().getMainHandItem()))
                    .then(Commands.argument("metal", EnumArgument.enumArgument(Metal.Default.class))
                        .executes(context -> spawnSet(context.getSource(), context.getArgument("material", ItemInput.class).getItem(), context.getArgument("pattern", ItemInput.class).getItem(), context.getArgument("metal", Metal.Default.class)))
                    )
                )
            );
    }

    public static int spawnSet(CommandSourceStack source, Item materialItem, Item patternItem, Metal.Default metal) throws CommandSyntaxException
    {
        if (!metal.hasArmor())
        {
            source.sendFailure(Component.translatable(FAIL_NOT_ARMOR));
            return 0;
        }
        final Map<Metal.ItemType, ? extends Supplier<Item>> map = TFCItems.METAL_ITEMS.get(metal);
        final ServerPlayer player = source.getPlayerOrException();

        for (Metal.ItemType type : new Metal.ItemType[] {Metal.ItemType.HELMET, Metal.ItemType.CHESTPLATE, Metal.ItemType.GREAVES, Metal.ItemType.BOOTS})
        {
            final ItemStack stack = map.get(type).get().getDefaultInstance();
            addTrim(source, materialItem, patternItem, stack);
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int addTrim(CommandSourceStack source, Item materialItem, Item patternItem, ItemStack item)
    {
        final var material = TrimMaterials.getFromIngredient(source.registryAccess(), materialItem.getDefaultInstance());
        final var pattern = TrimPatterns.getFromTemplate(source.registryAccess(), patternItem.getDefaultInstance());
        if (material.isEmpty())
        {
            source.sendFailure(Component.translatable(FAIL_BAD_MATERIAL));
            return 0;
        }
        if (pattern.isEmpty())
        {
            source.sendFailure(Component.translatable(FAIL_BAD_TEMPLATE));
            return 0;
        }
        if (ArmorTrim.setTrim(source.registryAccess(), item, new ArmorTrim(material.get(), pattern.get())))
        {
            return Command.SINGLE_SUCCESS;
        }
        source.sendFailure(Component.translatable(FAIL_NOT_APPLIED));
        return 0;
    }

}
