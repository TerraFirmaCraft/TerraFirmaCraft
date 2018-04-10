package net.dries007.tfc.objects;

public enum Rock
{
    GRANITE(Category.IGNEOUS_INTRUSIVE),
    DIORITE(Category.IGNEOUS_INTRUSIVE),
    GABBRO(Category.IGNEOUS_INTRUSIVE),

    SHALE(Category.SEDIMENTARY),
    CLAYSTONE(Category.SEDIMENTARY),
    ROCKSALT(Category.SEDIMENTARY),
    LIMESTONE(Category.SEDIMENTARY),
    CONGLOMERATE(Category.SEDIMENTARY),
    DOLOMITE(Category.SEDIMENTARY),
    CHERT(Category.SEDIMENTARY),
    CHALK(Category.SEDIMENTARY),

    RHYOLITE(Category.IGNEOUS_EXTRUSIVE),
    BASALT(Category.IGNEOUS_EXTRUSIVE),
    ANDESITE(Category.IGNEOUS_EXTRUSIVE),
    DACITE(Category.IGNEOUS_EXTRUSIVE),

    QUARTZITE(Category.METAMORPHIC),
    SLATE(Category.METAMORPHIC),
    PHYLLITE(Category.METAMORPHIC),
    SCHIST(Category.METAMORPHIC),
    GNEISS(Category.METAMORPHIC),
    MARBLE(Category.METAMORPHIC);

    public final Category category;

    Rock(Category category)
    {
        this.category = category;
    }

    public enum Category
    {
        SEDIMENTARY, METAMORPHIC, IGNEOUS_INTRUSIVE, IGNEOUS_EXTRUSIVE
    }
}
