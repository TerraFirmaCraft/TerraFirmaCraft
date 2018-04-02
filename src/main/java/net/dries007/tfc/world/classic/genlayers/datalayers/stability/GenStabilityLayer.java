package net.dries007.tfc.world.classic.genlayers.datalayers.stability;

import net.dries007.tfc.world.classic.genlayers.*;

public abstract class GenStabilityLayer extends GenLayerTFC
{
    public GenStabilityLayer(long par1)
    {
        super(par1);
    }

    public static GenLayerTFC initialize(long seed)
    {
        GenLayerTFC continent = genContinent(seed);
        drawImage(512, continent, "Stability 0");
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
        drawImage(512, continent, "Stability 1");
        for (int zoomLevel = 0; zoomLevel < 4; ++zoomLevel)
        {
            continent = new GenLayerZoomTFC(1000 + zoomLevel, continent);
            drawImage(512, continent, "Stability 2-" + zoomLevel);
        }

        GenLayerSmoothTFC finalCont = new GenLayerSmoothTFC(1000L, continent);
        drawImage(512, finalCont, "Stability 3");
        GenLayerVoronoiZoomTFC voronoiZoom = new GenLayerVoronoiZoomTFC(10L, finalCont);
        voronoiZoom.initWorldGenSeed(seed);
        return voronoiZoom;
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continent = new GenLayerStabilityInit(1L + seed);
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        //continent = new GenLayerAddIslandTFC(1L, continent);
        continent = new GenLayerZoomTFC(2001L, continent);
        //continent = new GenLayerAddIslandTFC(2L, continent);
        continent = new GenLayerZoomTFC(2002L, continent);
        //continent = new GenLayerAddIslandTFC(3L, continent);
        continent = new GenLayerZoomTFC(2003L, continent);
        //continent = new GenLayerAddIslandTFC(4L, continent);
        return continent;
    }
}
