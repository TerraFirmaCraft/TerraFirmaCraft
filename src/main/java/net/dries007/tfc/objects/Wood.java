/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.dries007.tfc.objects.trees.ITreeGenerator;
import net.dries007.tfc.objects.trees.TreeGenNormal;
import net.dries007.tfc.objects.trees.TreeGenVariants;
import net.dries007.tfc.objects.trees.TreeGenWillow;

public enum Wood
{
    ACACIA(0, 12620, 7060, false, false), //Acacia Koa
    ASH(1, 12600, 5970, false, true),//Black Ash
    ASPEN(2, 9230, 5220, false, true),//Black Poplar
    BIRCH(3, 12300, 5690, false, true),//Paper Birch
    BLACKWOOD(4, 15020, 7770, false, false),//Australian Blackwood
    CHESTNUT(5, 8600, 5320, false, true),//Sweet Chestnut
    DOUGLAS_FIR(6, 12500, 6950, true, false),//Douglas Fir
    HICKORY(7, 17100, 9040, false, false),//Bitternut Hickory
    MAPLE(8, 13400, 6540, false, false),//Red Maple
    OAK(9, 14830, 7370, false, true),//White Oak
    PALM(10, 12970, 9590, false, false),//Red Palm
    PINE(11, 14500, 8470, true, false),//Longleaf Pine
    ROSEWOOD(12, 19570, 9740, false, false),//Brazilian Rosewood
    SEQUOIA(13, 8950, 5690, true, false),//Redwood
    SPRUCE(14, 8640, 4730, true, true),//White Spruce
    SYCAMORE(15, 10000, 5380, false, false),//American Sycamore
    WHITE_CEDAR(16, 6500, 3960, true, false),//Northern White Cedar
    WILLOW(17, 8150, 3900, false, true),//White Willow
    KAPOK(18, 14320, 6690, false, true);//Chakte Kok - No data on kapok so went with the brazilian Chakte Kok(Redheart Tree)

    public static Wood get(int i)
    {
        for (Wood wood : Wood.values())
        {
            if (wood.index == i)
                return wood;
        }
        throw new IndexOutOfBoundsException("No wood found matching " + i);
    }

    private static ITreeGenerator genNormal = new TreeGenNormal(1, 3); // Short trees. For oak style
    private static ITreeGenerator genFixed = new TreeGenNormal(0, 2); // Short height range For douglas fir style
    private static ITreeGenerator genTall = new TreeGenNormal(3, 3); // Tall trees. For oak style
    private static ITreeGenerator genVariant7 = new TreeGenVariants(false, "1", "2", "3", "4", "5", "6", "7"); // 7 Variants. Conifer
    private static ITreeGenerator genVariant7WithRotation = new TreeGenVariants(true, "1", "2", "3", "4", "5", "6", "7"); // 7 Variants. Tropical
    private static ITreeGenerator genWillow = new TreeGenWillow(); // Special builder for willow trees

    public int getRadiusForGrowth() { return 2; } // todo: make this a property of each tree

    public ITreeGenerator getTreeGenerator()
    {
        switch(this)
        {
            case DOUGLAS_FIR:
            case WHITE_CEDAR:
            case BLACKWOOD:
                return genFixed;
            case ASH:
            case ASPEN:
            case HICKORY:
            case BIRCH:
                return genTall;
            case PINE:
            case SPRUCE:
                return genVariant7;
            case PALM:
                return genVariant7WithRotation;
            case WILLOW:
                return genWillow;
            default:
                return genNormal;
        }
    }

    public final int bend;
    public final int compression;
    public final boolean isEverGreen, isSwapTree;
    public final int index;

    Wood(int index, int bend, int compression, boolean isEverGreen, boolean isSwapTree)
    {
        this.index = index;
        this.bend = bend;
        this.compression = compression;
        this.isEverGreen = isEverGreen;
        this.isSwapTree = isSwapTree;
    }
}
