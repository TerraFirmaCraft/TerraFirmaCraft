package net.dries007.tfc.objects;

public enum Wood
{
    ASH(12600, 5970, false, true),//Black Ash
    ASPEN(9230, 5220, false, true),//Black Poplar
    BIRCH(12300, 5690, false, true),//Paper Birch
    CHESTNUT(8600, 5320,false, true),//Sweet Chestnut
    DOUGLAS_FIR(12500, 6950, true, false),//Douglas Fir
    HICKORY(17100, 9040, false, false),//Bitternut Hickory
    MAPLE(13400, 6540, false, false),//Red Maple
    OAK(14830, 7370, false, true),//White Oak
    PINE(14500, 8470, true, false),//Longleaf Pine
    SEQUOIA(8950, 5690, true, false),//Redwood
    SPRUCE(8640, 4730, true, true),//White Spruce
    SYCAMORE(10000, 5380, false, false),//American Sycamore
    WHITE_CEDAR(6500, 3960, true, false),//Northern White Cedar
    WILLOW(8150, 3900, false, true),//White Willow
    KAPOK(14320, 6690, false, true),//Chakte Kok - No data on kapok so went with the brazilian Chakte Kok(Redheart Tree)
    ACACIA(12620, 7060, false, false), //Acacia Koa
    ROSEWOOD(19570, 9740, false, false),//Brazilian Rosewood
    BLACKWOOD(15020, 7770, false, false),//Australian Blackwood
    PALM(12970, 9590, false, false);//Red Palm

    public final int bend;
    public final int compression;
    public final boolean isEverGreen, isSwapTree;

    Wood(int bend, int compression, boolean isEverGreen, boolean isSwapTree)
    {
        this.bend = bend;
        this.compression = compression;
        this.isEverGreen = isEverGreen;
        this.isSwapTree = isSwapTree;
    }
}
