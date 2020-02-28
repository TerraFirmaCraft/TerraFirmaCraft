/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.inventory.IContainerListener;

/**
 * For containers that need to be given a separate sync handler, for capability only changes
 */
public interface ICapabilityUpdateContainer
{
    void setCapabilityListener(IContainerListener listener);
}
