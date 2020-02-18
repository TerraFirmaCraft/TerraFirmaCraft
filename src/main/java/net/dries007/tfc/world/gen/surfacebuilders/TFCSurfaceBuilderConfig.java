package net.dries007.tfc.world.gen.surfacebuilders;

public class TFCSurfaceBuilderConfig
{
    private final ISurfacePart top;
    private final ISurfacePart under;
    private final ISurfacePart underWater;
    private final ISurfacePart deepUnder;

    public TFCSurfaceBuilderConfig(ISurfacePart top, ISurfacePart under, ISurfacePart underWater)
    {
        this(top, under, underWater, ISurfaceBuilder.RAW);
    }

    public TFCSurfaceBuilderConfig(ISurfacePart top, ISurfacePart under, ISurfacePart underWater, ISurfacePart deepUnder)
    {
        this.top = top;
        this.under = under;
        this.underWater = underWater;
        this.deepUnder = deepUnder;
    }

    public ISurfacePart getTop()
    {
        return top;
    }

    public ISurfacePart getUnder()
    {
        return under;
    }

    public ISurfacePart getUnderWater()
    {
        return underWater;
    }

    public ISurfacePart getDeepUnder()
    {
        return deepUnder;
    }
}
