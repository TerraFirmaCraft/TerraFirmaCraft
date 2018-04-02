package net.dries007.tfc.world.classic.genlayers.datalayers.ph;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerFuzzyZoomTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerSmoothTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerZoomTFC;

public abstract class GenPHLayer extends GenLayerTFC
{
    public static final int MIN = DataLayer.PH_ACID_HIGH.layerID;
    public static final int MAX = DataLayer.PH_ALKALINE_HIGH.layerID;

    public GenPHLayer(long par1)
    {
        super(par1);
    }

    public static GenLayerTFC initialize(long seed)
    {
        GenLayerTFC continent = genContinent(0);
        drawImage(512, continent, "PH 0");
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
        drawImage(512, continent, "PH 1");
        for (int zoomLevel = 0; zoomLevel < 2; ++zoomLevel)
        {
            if (zoomLevel == 0)
            {
                continent = new GenLayerPHMix(1000 + zoomLevel, continent);
                drawImage(512, continent, "PH 2-" + zoomLevel + " Mix");
            }
            continent = new GenLayerZoomTFC(1000 + zoomLevel, continent);
            drawImage(512, continent, "PH 2-" + zoomLevel + " Smoothed");
        }

        GenLayerSmoothTFC finalCont = new GenLayerSmoothTFC(1000L, continent);
        //GenLayerTFC voronoiZoom = new GenLayerVoronoiZoomTFC(10L, finalCont);
        drawImage(512, finalCont, "PH Final");
        finalCont.initWorldGenSeed(seed);
        return finalCont;
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continent = new GenLayerPHInit(1L + seed);
        drawImage(512, continent, "PH Init 0");
        continent = new GenLayerAddPH(1L, continent);
        drawImage(512, continent, "PH Init 0b Add PH");
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        drawImage(512, continent, "PH Init 1");
        continent = new GenLayerAddPH(1L, continent);
        drawImage(512, continent, "PH Init 2 Add PH");
        continent = new GenLayerZoomTFC(2001L, continent);
        drawImage(512, continent, "PH Init 3 Zoom");
        continent = new GenLayerAddPH(2L, continent);
        drawImage(512, continent, "PH Init 4 Add PH");
        continent = new GenLayerPHMix(88L, continent);
        drawImage(512, continent, "PH Init 4b Mix");
        continent = new GenLayerZoomTFC(2002L, continent);
        drawImage(512, continent, "PH Init 5 Zoom");
        continent = new GenLayerAddPH(3L, continent);
        drawImage(512, continent, "PH Init 6 Add PH");
        continent = new GenLayerZoomTFC(2003L, continent);
        drawImage(512, continent, "PH Init 7 Zoom");
        continent = new GenLayerAddPH(4L, continent);
        drawImage(512, continent, "PH Init 8 Add PH");
        return continent;
    }
}
