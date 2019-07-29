/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

/**
 * An interface for {@link net.minecraft.client.gui.GuiButton}'s that have a tooltip when hovered over
 */
public interface IButtonTooltip
{

    String getTooltip();

    boolean hasTooltip();
}
