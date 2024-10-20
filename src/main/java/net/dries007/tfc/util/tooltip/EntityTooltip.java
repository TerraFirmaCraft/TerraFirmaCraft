/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * A tooltip to display on an entity.
 */
@FunctionalInterface
public interface EntityTooltip
{
    void display(Level level, Entity entity, Consumer<Component> tooltip);
}
