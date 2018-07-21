/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.tree;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.genlayers.*;

public abstract class GenTreeLayer extends GenLayerTFC
{
    public static GenLayerTFC initialize(long par0, Tree[] trees)
    {
        GenLayerTFC layer = new GenLayerTreeInit(1L, trees);
//        drawImage(512, layer, "Tree 0");
        layer = new GenLayerFuzzyZoomTFC(2000L, layer);
//        drawImage(512, layer, "Tree 1");
        //layer = new GenLayerAddTree(1L, layer);
//        drawImage(512, layer, "Tree 2");
        layer = new GenLayerZoomTFC(2001L, layer);
        //layer = new GenLayerAddTree(2L, layer);
//        drawImage(512, layer, "Tree 3");
        layer = new GenLayerZoomTFC(2002L, layer);
        //layer = new GenLayerAddTree(3L, layer);
//        drawImage(512, layer, "Tree 4");
        layer = new GenLayerZoomTFC(2003L, layer);
        //layer = new GenLayerAddTree(4L, layer);
//        drawImage(512, layer, "Tree 5");
        layer = new GenLayerSmoothTFC(1000L, layer);
//        drawImage(512, layer, "Tree 6");
        for (int zoomLevel = 0; zoomLevel < 5; ++zoomLevel)
        {
            layer = new GenLayerZoomTFC(1000 + zoomLevel, layer);
//            drawImage(512, layer, "Tree " + (7 + zoomLevel));
        }

        GenLayerSmoothTFC smoothedLayer = new GenLayerSmoothTFC(1000L, layer);
        GenLayerVoronoiZoomTFC voronoiLayer = new GenLayerVoronoiZoomTFC(10L, smoothedLayer);
        drawImage(512, layer, "Tree Final");

        voronoiLayer.initWorldGenSeed(par0);
        return voronoiLayer;
    }

    public GenTreeLayer(long par1)
    {
        super(par1);
    }
}
