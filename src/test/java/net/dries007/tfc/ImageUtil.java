/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

import net.minecraft.util.math.MathHelper;

/**
 * Generic imaging util with various options for constructing images from specific classes.
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "UnusedReturnValue"})
public abstract class ImageUtil<T>
{
    public static ImageUtil<DoubleBinaryOperator> simple()
    {
        return noise(x -> x);
    }

    public static <V> ImageUtil<V> noise(Function<V, DoubleBinaryOperator> transformer)
    {
        return noise(transformer, x -> {});
    }

    public static <V> ImageUtil<V> noise(Function<V, DoubleBinaryOperator> transformer, Consumer<ImageUtil.Noise<V>> builder)
    {
        ImageUtil.Noise<V> images = new ImageUtil.Noise<>(transformer);
        builder.accept(images);
        return images;
    }

    public static <V> ImageUtil<V> colored(Function<V, Double2ObjectBiFunction<Color>> transformer)
    {
        return colored(transformer, x -> {});
    }

    public static <V> ImageUtil<V> colored(Function<V, Double2ObjectBiFunction<Color>> transformer, Consumer<ImageUtil.Colored<V>> builder)
    {
        ImageUtil.Colored<V> images = new ImageUtil.Colored<>(transformer);
        builder.accept(images);
        return images;
    }

    private static int clamp(int num, int min, int max)
    {
        return num < min ? min : Math.min(num, max);
    }

    // All these fields are set by builders
    protected Double2ObjectFunction<Color> color = Colors.LINEAR_GRAY;
    protected int size = 1000;
    protected int minX = 0, minY = 0, maxX = size, maxY = size;

    public ImageUtil<T> color(Double2ObjectFunction<Color> color)
    {
        this.color = color;
        return this;
    }

    public ImageUtil<T> dimensions(int side)
    {
        return dimensions(side, side);
    }

    public ImageUtil<T> dimensions(int width, int height)
    {
        return dimensions(0, 0, width, height);
    }

    public ImageUtil<T> dimensions(int minX, int minY, int maxX, int maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }

    /**
     * The size of the drawn image
     */
    public ImageUtil<T> size(int size)
    {
        this.size = size;
        return this;
    }

    public ImageUtil<T> draw(String name, T instance)
    {
        File outFile = new File(name + ".png");
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = ((Graphics2D) image.getGraphics());
        draw(instance, graphics);
        try
        {
            ImageIO.write(image, "PNG", outFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return this;
    }

    protected abstract void draw(T instance, Graphics graphics);

    @FunctionalInterface
    public interface Double2ObjectBiFunction<T>
    {
        T apply(double x, double y);
    }

    @FunctionalInterface
    public interface Double2ObjectFunction<T>
    {
        T apply(double value);
    }

    @FunctionalInterface
    public interface DoubleTernaryOperator
    {
        double apply(double first, double second, double third);
    }

    public static final class Colors
    {
        public static final Double2ObjectFunction<Color> LINEAR_GRAY = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, x, x);
        };
        public static final Double2ObjectFunction<Color> LINEAR_BLUE_RED = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, 0, 255 - x);
        };
        public static final Double2ObjectFunction<Color> LINEAR_GREEN_YELLOW = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, 255, 0);
        };

        private static final Color[] COLORS_20 = new Color[] {
            new Color(0xFFB300),
            new Color(0x803E75),
            new Color(0xFF6800),
            new Color(0xA6BDD7),
            new Color(0xC10020),
            new Color(0xCEA262),
            new Color(0x817066),
            new Color(0x007D34),
            new Color(0xF6768E),
            new Color(0x00538A),
            new Color(0xFF7A5C),
            new Color(0x53377A),
            new Color(0xFF8E00),
            new Color(0xB32851),
            new Color(0xF4C800),
            new Color(0x7F180D),
            new Color(0x93AA00),
            new Color(0x593315),
            new Color(0xF13A13),
            new Color(0x232C16),
        };

        public static final Double2ObjectFunction<Color> DISCRETE_20 = value -> COLORS_20[MathHelper.clamp((int) value * COLORS_20.length, 0, COLORS_20.length - 1)];
    }

    public static final class Scales
    {
        public static final DoubleTernaryOperator EXACT = (value, min, max) -> value;
        public static final DoubleTernaryOperator NEAREST_INT = (value, min, max) -> (int) (value + 0.5);
        public static final DoubleTernaryOperator DYNAMIC_RANGE = (value, min, max) -> max == min ? value : (value - min) / (max - min);

        public static DoubleTernaryOperator fixedRange(double fixedMin, double fixedMax)
        {
            return (value, min, max) -> DYNAMIC_RANGE.apply(value, fixedMin, fixedMax);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static class Noise<T> extends ImageUtil<T>
    {
        protected final Function<T, DoubleBinaryOperator> noiseTransformer;
        private DoubleTernaryOperator scaleTransformer = Scales.DYNAMIC_RANGE;

        private Noise(Function<T, DoubleBinaryOperator> noiseTransformer)
        {
            this.noiseTransformer = noiseTransformer;
        }

        public ImageUtil<T> scale(DoubleTernaryOperator scale)
        {
            this.scaleTransformer = scale;
            return this;
        }

        @Override
        protected void draw(T instance, Graphics graphics)
        {
            final double[] sourceMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
            DoubleBinaryOperator source = noiseTransformer.apply(instance);
            IntStream.range(0, size * size)
                .mapToObj(i -> {
                    int posX = (i / size);
                    int posY = (i % size);
                    int x = minX + (i / size) * (maxX - minX) / size;
                    int y = minY + (i % size) * (maxY - minY) / size;
                    double value = source.applyAsDouble(x, y);
                    sourceMinMax[0] = Math.min(sourceMinMax[0], value);
                    sourceMinMax[1] = Math.max(sourceMinMax[1], value);
                    return new Local<>(posX, posY, value);
                })
                .map(Local.map(value -> scaleTransformer.apply(value, sourceMinMax[0], sourceMinMax[1])))
                .map(Local.map(value -> color.apply(value)))
                .forEach(loc -> {
                    graphics.setColor(loc.value);
                    graphics.drawRect(loc.x, loc.y, 1, 1);
                });
        }
    }

    public static class Colored<T> extends ImageUtil<T>
    {
        protected final Function<T, Double2ObjectBiFunction<Color>> colorTransformer;

        private Colored(Function<T, Double2ObjectBiFunction<Color>> colorTransformer)
        {
            this.colorTransformer = colorTransformer;
        }

        @Override
        protected void draw(T instance, Graphics graphics)
        {
            Double2ObjectBiFunction<Color> colorSource = colorTransformer.apply(instance);
            IntStream.range(0, size * size)
                .mapToObj(i -> {
                    int posX = (i / size);
                    int posY = (i % size);
                    int x = minX + (i / size) * (maxX - minX) / size;
                    int y = minY + (i % size) * (maxY - minY) / size;
                    Color color = colorSource.apply(x, y);
                    return new Local<>(posX, posY, color);
                })
                .forEach(loc -> {
                    graphics.setColor(loc.value);
                    graphics.drawRect(loc.x, loc.y, 1, 1);
                });
        }
    }

    private static final class Local<T>
    {
        public static <A, B> Function<Local<A>, Local<B>> map(Function<A, B> transformer)
        {
            return loc -> new Local<>(loc.x, loc.y, transformer.apply(loc.value));
        }

        final int x, y;
        final T value;

        private Local(int x, int y, T value)
        {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
}
