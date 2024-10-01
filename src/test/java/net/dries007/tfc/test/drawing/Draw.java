/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.drawing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

import net.dries007.tfc.util.Helpers;

public final class Draw
{
    public static final String OUTPUT = "../../artist";

    public static void draw(String name, int sizeX, int sizeZ, Pixel pixel)
    {
        draw(name, sizeX, sizeZ, image -> {
            for (int x = 0; x < sizeX; x++)
                for (int z = 0; z < sizeZ; z++)
                    image.setRGB(x, z, pixel.get(x, z));
        });
    }

    public static void draw(String name, int sizeX, int sizeZ, Consumer<BufferedImage> draw)
    {
        Helpers.uncheck(() -> {
            new File(OUTPUT).mkdirs();
            final File outFile = new File(OUTPUT + "/" + name + ".png");
            final BufferedImage image = new BufferedImage(sizeX, sizeZ, BufferedImage.TYPE_INT_RGB);
            draw.accept(image);
            ImageIO.write(image, "PNG", outFile);
        });
    }

    interface Pixel
    {
        int get(int x, int z);
    }
}
