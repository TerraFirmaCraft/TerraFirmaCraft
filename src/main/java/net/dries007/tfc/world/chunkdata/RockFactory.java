/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;

public class RockFactory
{
    private final LazyArea area;

    public RockFactory(IAreaFactory<LazyArea> factoryLayer)
    {
        this.area = factoryLayer.make();
    }

    public Rock get(int x, int z)
    {
        return RockManager.INSTANCE.get(area.get(x, z));
    }
}