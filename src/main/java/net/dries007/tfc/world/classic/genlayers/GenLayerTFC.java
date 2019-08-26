/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import net.minecraft.world.gen.layer.GenLayer;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.genlayers.biome.*;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverInitTFC;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverMixTFC;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverTFC;

public abstract class GenLayerTFC extends GenLayer
{
    // Distinct colors for debug map gen
    private static Color[] COLORS = new Color[] {
        new Color(0xFFB300),    // Vivid Yellow
        new Color(0x803E75),    // Strong Purple
        new Color(0xFF6800),    // Vivid Orange
        new Color(0xA6BDD7),    // Very Light Blue
        new Color(0xC10020),    // Vivid Red
        new Color(0xCEA262),    // Grayish Yellow
        new Color(0x817066),    // Medium Gray
        new Color(0x007D34),    // Vivid Green
        new Color(0xF6768E),    // Strong Purplish Pink
        new Color(0x00538A),    // Strong Blue
        new Color(0xFF7A5C),    // Strong Yellowish Pink
        new Color(0x53377A),    // Strong Violet
        new Color(0xFF8E00),    // Vivid Orange Yellow
        new Color(0xB32851),    // Strong Purplish Red
        new Color(0xF4C800),    // Vivid Greenish Yellow
        new Color(0x7F180D),    // Strong Reddish Brown
        new Color(0x93AA00),    // Vivid Yellowish Green
        new Color(0x593315),    // Deep Yellowish Brown
        new Color(0xF13A13),    // Vivid Reddish Orange
        new Color(0x232C16),    // Dark Olive Green
    };

    public static GenLayerTFC[] initialize2(long seed)
    {
        GenLayerTFC continent = genContinent(0);
        continent = new GenLayerDeepOcean(4L, continent);
//        drawImage(512, continent, "8b Continents Done Deep Ocean");
        byte var4 = 4;

        //Create BiomesTFC
        GenLayerTFC continentCopy2 = GenLayerZoomTFC.magnify(1000L, continent, 0);
//        drawImage(512, continentCopy2, "14 ContinentsZoom");
        GenLayerTFC var17 = new GenLayerBiomeTFC(200L, continentCopy2);
//        drawImage(512, var17, "15 Biome");
        GenLayerLakes lakes = new GenLayerLakes(200L, var17);
//        drawImage(512, var17, "15b Lakes");
        continentCopy2 = GenLayerZoomTFC.magnify(1000L, lakes, 2);
//        drawImage(512, continentCopy2, "16 ZoomBiome");
        GenLayerTFC var18 = new GenLayerBiomeEdge(1000L, continentCopy2);
//        drawImage(512, var18, "17 BiomeEdge");
        for (int var7 = 0; var7 < var4; ++var7)
        {
            var18 = new GenLayerZoomTFC(1000 + var7, var18);
//            drawImage(512, var18, "18-" + var7 + " Zoom");
            if (var7 == 0)
                var18 = new GenLayerAddIslandTFC(3L, var18);
            if (var7 == 1)
            {
                var18 = new GenLayerShoreTFC(1000L, var18);
//                drawImage(512, var18, "18z Shore");
            }
        }

        //Create Rivers
        GenLayerTFC riverCont = GenLayerZoomTFC.magnify(1000L, continent, 2);
//        drawImage(512, riverCont, "9 ContinentsZoom");
        riverCont = new GenLayerRiverInitTFC(100L, riverCont);
//        drawImage(512, riverCont, "10 RiverInit");
        riverCont = GenLayerZoomTFC.magnify(1000L, riverCont, 6);
//        drawImage(512, riverCont, "11 RiverInitZoom");
        riverCont = new GenLayerRiverTFC(1L, riverCont);
//        drawImage(512, riverCont, "12 River");
        riverCont = new GenLayerSmoothTFC(1000L, riverCont);
//        drawImage(512, riverCont, "13 SmoothRiver");

        GenLayerSmoothTFC smoothContinent = new GenLayerSmoothTFC(1000L, var18);
//        drawImage(512, smoothContinent, "Biome 19");
        GenLayerRiverMixTFC riverMix = new GenLayerRiverMixTFC(100L, smoothContinent, riverCont);
//        drawImage(512, riverMix, "Biome 20");
        GenLayerTFC finalCont = GenLayerZoomTFC.magnify(1000L, riverMix, 2);
//        drawImage(512, finalCont, "Biome 20-zoom");
        finalCont = new GenLayerSmoothTFC(1001L, finalCont);
//        drawImage(512, finalCont, "Biome 21");
        riverMix.initWorldGenSeed(seed);
        finalCont.initWorldGenSeed(seed);
        return new GenLayerTFC[] {riverMix, finalCont};
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continentStart = new GenLayerIslandTFC(1L + seed);
//        drawImage(512, continentStart, "0 ContinentsStart");
        GenLayerFuzzyZoomTFC continentFuzzyZoom = new GenLayerFuzzyZoomTFC(2000L, continentStart);
//        drawImage(512, continentFuzzyZoom, "1 ContinentsFuzzyZoom");
        GenLayerTFC var10 = new GenLayerAddIslandTFC(1L, continentFuzzyZoom);
//        drawImage(512, var10, "2 ContinentsAddIsland");
        GenLayerTFC var11 = new GenLayerZoomTFC(2001L, var10);
//        drawImage(512, var11, "3 ContinentsAddIslandZoom");
        var10 = new GenLayerAddIslandTFC(2L, var11);
//        drawImage(512, var10, "4 ContinentsAddIsland2");
        var11 = new GenLayerZoomTFC(2002L, var10);
//        drawImage(512, var11, "5 ContinentsAddIslandZoom2");
        var10 = new GenLayerAddIslandTFC(3L, var11);
//        drawImage(512, var10, "6 ContinentsAddIsland3");
        var11 = new GenLayerZoomTFC(2003L, var10);
//        drawImage(512, var11, "7 ContinentsAddIslandZoom3");
        GenLayerTFC continent = new GenLayerAddIslandTFC(4L, var11);
//        drawImage(512, continent, "8 ContinentsDone");
        return continent;
    }

    public static void drawImage(int size, GenLayerTFC genlayer, String name)
    {
        if (!ConfigTFC.WORLD.debugWorldGen) return;
        try
        {
            File outFile = new File(name + ".bmp");
            if (outFile.exists())
                return;
            int[] ints = genlayer.getInts(0, 0, size, size);
            BufferedImage outBitmap = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
            graphics.clearRect(0, 0, size, size);
            TerraFirmaCraft.getLog().info(name + ".bmp");
            for (int x = 0; x < size; x++)
            {
                for (int z = 0; z < size; z++)
                {
                    if (ints[x * size + z] != -1)
                    {
                        graphics.setColor(COLORS[ints[x * size + z] % COLORS.length]);
                        graphics.drawRect(x, z, 1, 1);
                    }
                }
            }
            TerraFirmaCraft.getLog().info(name + ".bmp");
            ImageIO.write(outBitmap, "BMP", outFile);
        }
        catch (Exception e)
        {
            TerraFirmaCraft.getLog().catching(e);
        }
    }

    public GenLayerTFC(long seed)
    {
        super(seed);
    }

}
