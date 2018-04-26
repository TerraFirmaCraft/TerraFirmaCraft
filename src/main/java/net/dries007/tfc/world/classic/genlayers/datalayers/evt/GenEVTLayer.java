/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.evt;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.*;

public abstract class GenEVTLayer extends GenLayerTFC
{
    public static final int LOW = DataLayer.EVT_0_25.layerID;
    public static final int HIGH = DataLayer.EVT_8.layerID;

    public static GenLayerTFC initialize(long seed)
    {
        GenLayerTFC continent = genContinent(0);
//        drawImage(512, continent, "EVT 0");
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
//        drawImage(512, continent, "EVT 1");
        for (int zoomLevel = 0; zoomLevel < 4; ++zoomLevel)
        {
            if (zoomLevel == 0)
            {
                continent = new GenLayerEVTMix(1000 + zoomLevel, continent);
//                drawImage(512, continent, "EVT 2-" + zoomLevel + " Mix");
            }
            continent = new GenLayerZoomTFC(1000 + zoomLevel, continent);
//            drawImage(512, continent, "EVT 2-" + zoomLevel + " Smoothed");
        }

        GenLayerSmoothTFC finalCont = new GenLayerSmoothTFC(1000L, continent);
        GenLayerTFC voronoiZoom = new GenLayerVoronoiZoomTFC(10L, finalCont);
//        drawImage(512, voronoiZoom, "EVT 4 Voronoi EVT");
        voronoiZoom.initWorldGenSeed(seed);
        return voronoiZoom;
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continent = new GenLayerEVTInit(1L + seed);
//        drawImage(512, continent, "EVT Init 0");
        continent = new GenLayerAddEVT(1L, continent);
//        drawImage(512, continent, "EVT Init 0b Add EVT");
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
//        drawImage(512, continent, "EVT Init 1");
        continent = new GenLayerAddEVT(1L, continent);
//        drawImage(512, continent, "EVT Init 2 Add EVT");
        continent = new GenLayerZoomTFC(2001L, continent);
//        drawImage(512, continent, "EVT Init 3 Zoom");
        continent = new GenLayerAddEVT(2L, continent);
//        drawImage(512, continent, "EVT Init 4 Add EVT");
        continent = new GenLayerEVTMix(88L, continent);
//        drawImage(512, continent, "EVT Init 4b Mix");
        continent = new GenLayerZoomTFC(2002L, continent);
//        drawImage(512, continent, "EVT Init 5 Zoom");
        continent = new GenLayerAddEVT(3L, continent);
//        drawImage(512, continent, "EVT Init 6 Add EVT");
        continent = new GenLayerZoomTFC(2003L, continent);
//        drawImage(512, continent, "EVT Init 7 Zoom");
        continent = new GenLayerAddEVT(4L, continent);
//        drawImage(512, continent, "EVT Init 8 Add EVT");
        return continent;
    }

    public GenEVTLayer(long par1)
    {
        super(par1);
    }
}
