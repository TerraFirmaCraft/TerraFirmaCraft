package net.dries007.tfc.world.region;

public enum ChooseRocks implements RegionTask
{
    INSTANCE;

    public static final int OCEAN_FLOOR = 0;
    public static final int VOLCANIC = 1;
    public static final int LAND = 2;
    public static final int UPLIFT = 3;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null)
                {
                    // todo: choose a rock supertype based on land / volcanism / biome height
                    // oceans -> ocean floor
                    // most biomes -> land
                    // tall biomes -> uplift, or volcanic
                    // islands -> volcanic
                    if (point.island())
                    {
                        point.rock = VOLCANIC;
                    }
                    else if (!point.land() && point.distanceToOcean <= -3)
                    {
                        point.rock = OCEAN_FLOOR; // The ocean near a continent
                    }
                    else
                    {

                    }
                }
            }
        }
    }
}
