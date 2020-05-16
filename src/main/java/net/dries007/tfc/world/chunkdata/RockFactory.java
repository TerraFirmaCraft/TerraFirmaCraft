package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.types.RockManager;

public class RockFactory
{
    private final LazyArea area;

    public RockFactory(IAreaFactory<LazyArea> factoryLayer)
    {
        this.area = factoryLayer.make();
    }

    public Rock get(int x, int z)
    {
        return RockManager.INSTANCE.get(area.getValue(x, z));
    }
}
