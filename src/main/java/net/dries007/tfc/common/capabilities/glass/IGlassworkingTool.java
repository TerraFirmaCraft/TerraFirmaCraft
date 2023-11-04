/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.glass;

import java.util.List;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.util.Helpers;

/**
 * Marker interface for tools which are used in glassworking in the off-hand.
 */
public interface IGlassworkingTool
{
    /**
     * @return The operation that this tool can be used to perform, by holding it while holding the blowpipe with glass in the main hand.
     */
    GlassOperation getOperation();

    default void addToolTooltip(List<Component> tooltip)
    {
        tooltip.add(Component.translatable("tfc.tooltip.glass.tool_description", Helpers.translateEnum(getOperation())));
    }
}
