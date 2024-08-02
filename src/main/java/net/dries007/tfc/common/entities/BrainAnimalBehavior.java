/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.dries007.tfc.common.entities.livestock.CommonAnimalBehavior;

public interface BrainAnimalBehavior
{
    boolean isMale();

    /**
     * @see CommonAnimalBehavior#setLastMatedNow
     */
    void setLastMatedNow();
}
