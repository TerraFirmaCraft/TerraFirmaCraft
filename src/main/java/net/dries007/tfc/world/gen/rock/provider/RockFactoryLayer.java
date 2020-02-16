package net.dries007.tfc.world.gen.rock.provider;

import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.world.gen.rock.RockData;

public class RockFactoryLayer
{
    private final LazyArea lazyArea;

    public RockFactoryLayer(IAreaFactory<LazyArea> lazyAreaFactoryIn)
    {
        this.lazyArea = lazyAreaFactoryIn.make();
    }

    public RockData get(int chunkX, int chunkZ)
    {
        return null;
    }

}
