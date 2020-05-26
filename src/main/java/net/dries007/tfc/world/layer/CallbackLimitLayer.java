/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

public class CallbackLimitLayer
{
    protected int limit;

    public CallbackLimitLayer(int limit)
    {
        setLimit(limit);
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }
}
