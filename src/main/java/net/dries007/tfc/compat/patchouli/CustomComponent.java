/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import vazkii.patchouli.api.ICustomComponent;

public abstract class CustomComponent implements ICustomComponent
{
    protected transient int posX, posY;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.posX = componentX;
        this.posY = componentY;
    }
}
