/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.rain;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.*;

public abstract class GenRainLayerTFC extends GenLayerTFC
{
    public static final int WET = DataLayer.RAIN_4000.layerID;
    public static final int DRY = DataLayer.RAIN_125.layerID;

    public static GenLayerTFC initialize(long seed)
    {
        GenLayerTFC continent = genContinent(0);
//        drawImage(512, continent, "Rain 0");
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
//        drawImage(512, continent, "Rain 1");
        for (int zoomLevel = 0; zoomLevel < 4; ++zoomLevel)
        {
            if ((zoomLevel & 1) == 1)
            {
                continent = new GenLayerRainMix(1000 + zoomLevel, continent);
//                drawImage(512, continent, "Rain 2-" + zoomLevel + " Mix");
            }
            continent = new GenLayerZoomTFC(1000 + zoomLevel, continent);
//            drawImage(512, continent, "Rain 2-" + zoomLevel + " Smoothed");
        }

        GenLayerSmoothTFC finalCont = new GenLayerSmoothTFC(1000L, continent);
        GenLayerVoronoiZoomTFC voronoiZoom = new GenLayerVoronoiZoomTFC(10L, finalCont);
//        drawImage(512, finalCont, "Rain 4 Voronoi Rain");
        voronoiZoom.initWorldGenSeed(seed);
        return voronoiZoom;
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continent = new GenLayerRainInit(1L + seed);
//        drawImage(512, continent, "Rain Init 0");
        continent = new GenLayerAddRain(1L, continent);
//        drawImage(512, continent, "Rain Init 0b Add Rain");
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
//        drawImage(512, continent, "Rain Init 1");
        //continent = new GenLayerAddRain(1L, continent);
        //drawImage(512, continent, "Rain Init 2 Add Rain");
        continent = new GenLayerZoomTFC(2001L, continent);
//        drawImage(512, continent, "Rain Init 3 Zoom");
        //continent = new GenLayerAddRain(2L, continent);
        //drawImage(512, continent, "Rain Init 4 Add Rain");
        continent = new GenLayerRainMix(88L, continent);
//        drawImage(512, continent, "Rain Init 4b Mix");
        continent = new GenLayerZoomTFC(2002L, continent);
//        drawImage(512, continent, "Rain Init 5 Zoom");
        continent = new GenLayerRainMix(88L, continent);
//        drawImage(512, continent, "Rain Init 5b Mix");
        //continent = new GenLayerAddRain(3L, continent);
        //drawImage(512, continent, "Rain Init 6 Add Rain");
        continent = new GenLayerZoomTFC(2003L, continent);
//        drawImage(512, continent, "Rain Init 7 Zoom");
        return continent;
    }

    public GenRainLayerTFC(long par1)
    {
        super(par1);
    }
}
