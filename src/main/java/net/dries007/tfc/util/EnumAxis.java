package net.dries007.tfc.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * Created by raymondbh on 01.04.2018.
 */
public enum EnumAxis implements IStringSerializable
{
    X("x"),
    Y("y"),
    Z("z"),
    NONE("none");

    private final String name;

    EnumAxis(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    public static EnumAxis fromFacingAxis(EnumFacing.Axis axis)
    {
        switch (axis)
        {
            case X:
                return X;
            case Y:
                return Y;
            case Z:
                return Z;
            default:
                return NONE;
        }
    }

    public static EnumAxis getFacingfromMeta(int meta)
    {
        switch (meta) {
            case 1:
                return X;
            case 2:
                return Y;
            case 3:
                return Z;
            default:
                return NONE;
        }
    }
     public static int getMetafromAxis(EnumAxis axis)
     {
         switch (axis)
         {
             case X:
                 return 1;
             case Y:
                 return 2;
             case Z:
                 return 3;
             default:
                 return 0;
         }
     }

    public String getName()
    {
        return this.name;
    }
}

