/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package imageutil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Generic image construction from noise
 * Used for various testing purposes
 *
 * @author AlcatrazEscapee
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "UnusedReturnValue"})
public class Images<T>
{
    private static final Images<ValueSource> INSTANCE = new Images<>(x -> x);

    public static Images<ValueSource> get()
    {
        return INSTANCE;
    }

    public static <T> Images<T> get(ValueTransformer<T> transformer)
    {
        return new Images<>(transformer);
    }

    private static int clamp(int num, int min, int max)
    {
        return num < min ? min : num > max ? max : num;
    }

    private final ValueTransformer<T> transformer;
    private int size = 1000;
    private ColorMap color = Colors.LINEAR_GRAY;
    private boolean disabled = false;

    private Images(ValueTransformer<T> transformer)
    {
        this.transformer = transformer;
    }

    public Images<T> size(int size)
    {
        this.size = size;
        return this;
    }

    public Images<T> color(ColorMap color)
    {
        this.color = color;
        return this;
    }

    public Images<T> disable()
    {
        disabled = true;
        return this;
    }

    public Images<T> enable()
    {
        disabled = false;
        return this;
    }

    public Images<T> drawF(String name, ValueSourceF source, double minValue, double maxValue, double minX, double minY, double maxX, double maxY)
    {
        return draw(name, (x, y) -> color.apply(source.apply((float) x, (float) y), minValue, maxValue), minX, minY, maxX, maxY);
    }

    public Images<T> draw(String name, T source, double minValue, double maxValue, double minX, double minY, double maxX, double maxY)
    {
        return draw(name, (double x, double y) -> color.apply(transformer.apply(source).apply(x, y), minValue, maxValue), minX, minY, maxX, maxY);
    }

    public Images<T> draw(String name, ColorSource source, double minX, double minY, double maxX, double maxY)
    {
        if (disabled) return this;
        long nanos = System.nanoTime();
        try
        {
            File outFile = new File(name + ".png");
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = ((Graphics2D) image.getGraphics());

            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    Color color = source.apply(minX + x * (maxX - minX) / size, minY + y * (maxY - minY) / size);
                    graphics.setColor(color);
                    graphics.drawRect(x, y, 1, 1);
                }
            }

            ImageIO.write(image, "PNG", outFile);
        }
        catch (IOException e)
        {
            System.out.println("Problems: " + e);
        }
        nanos = System.nanoTime() - nanos;
        System.out.printf("Image '%s' took %2.2f \u03BCs / sample = total %2.2f ms\n", name, nanos / 1E9, nanos / 1E6);
        return this;
    }

    @FunctionalInterface
    public interface ColorMap
    {
        Color apply(double value, double min, double max);

        default void set()
        {
            Images.INSTANCE.color(this);
        }
    }

    @FunctionalInterface
    public interface ValueTransformer<T>
    {
        ValueSource apply(T target);
    }

    @FunctionalInterface
    public interface ValueSource
    {
        double apply(double x, double y);
    }

    @FunctionalInterface
    public interface ValueSourceF
    {
        float apply(float x, float y);
    }

    /* IColorNoise */
    @FunctionalInterface
    public interface ColorSource
    {
        Color apply(double x, double y);
    }

    public static final class Colors
    {
        public static final ColorMap LINEAR_GRAY = (val, min, max) -> {
            int x = clamp((int) (255 * (val - min) / (max - min)), 0, 255);
            return new Color(x, x, x);
        };
        public static final ColorMap LINEAR_BLUE_RED = (val, min, max) -> {
            int x = clamp((int) (255 * (val - min) / (max - min)), 0, 255);
            return new Color(x, 0, 255 - x);
        };
        public static final ColorMap LINEAR_GREEN_YELLOW = (val, min, max) -> {
            int x = clamp((int) (255 * (val - min) / (max - min)), 0, 255);
            return new Color(x, 255, 0);
        };
        public static final ColorMap LAND_MASS = (val, min, max) -> {
            double step = max - min;
            if (val < min + step * 0.4) return Color.BLUE;
            if (val < min + step * 0.5) return new Color(0, 100, 200);
            if (val < min + step * 0.7) return new Color(0, 155, 0);
            if (val < min + step * 0.8) return Color.DARK_GRAY;
            return Color.WHITE;
        };
        private static final Color[] COLORS_20 = new Color[] {
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
        public static final ColorMap DISCRETE_20 = (val, min, max) -> {
            double rounded = (val - min) * COLORS_20.length / (max - min);
            return COLORS_20[clamp((int) rounded, 0, COLORS_20.length - 1)];
        };

        public static ColorMap discrete20Range(int low)
        {
            return (val, min, max) -> {
                int index = (int) Math.round(val) - low;
                if (index < 0 || index >= COLORS_20.length)
                {
                    return Color.BLACK;
                }
                return COLORS_20[index];
            };
        }

        public static ColorMap threshold(double min, double max)
        {
            return (val, min1, max1) -> {
                double p = (val - min1) / (max1 - min1);
                return min < p && p < max ? Color.WHITE : Color.BLACK;
            };
        }

        private Colors() {}
    }
}
