/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

/**
 * Generic image construction from noise
 * Used for various testing purposes
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "UnusedReturnValue"})
public class ImageUtil<T>
{
    public static ImageUtil<NoiseSource> get()
    {
        return get(x -> x);
    }

    public static <V> ImageUtil<V> get(NoiseTransformer<V> transformer)
    {
        return new ImageUtil<V>().handle(transformer);
    }

    private static int clamp(int num, int min, int max)
    {
        return num < min ? min : Math.min(num, max);
    }

    // All these fields are set by builders
    protected NoiseTransformer<T> noiseTransformer = target -> (x, y) -> 0;
    protected ScaleTransformer scaleTransformer = Scales.DYNAMIC_RANGE;
    protected ColorTransformer color = Colors.LINEAR_GRAY;
    protected int size = 1000;
    protected int minX = 0, minY = 0, maxX = size, maxY = size;
    protected double minValue = 0, maxValue = 1;

    public ImageUtil<T> handle(NoiseTransformer<T> noiseTransformer)
    {
        this.noiseTransformer = noiseTransformer;
        return this;
    }

    public ImageUtil<T> scale(ScaleTransformer scale)
    {
        this.scaleTransformer = scale;
        return this;
    }

    public ImageUtil<T> color(ColorTransformer color)
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

    public ImageUtil<T> range(double minValue, double maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    public ImageUtil<T> size(int size)
    {
        this.size = size;
        return this;
    }

    public <V> ImageUtil<V> delegate(Function<V, T> mappingFunction)
    {
        return new ImageUtil<V>().handle(target -> noiseTransformer.apply(mappingFunction.apply(target)));
    }

    public ImageUtil<T> draw(String name, T instance)
    {
        File outFile = new File(name + ".png");
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = ((Graphics2D) image.getGraphics());
        NoiseSource source = noiseTransformer.apply(instance);

        final double[] sourceMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
        IntStream.range(0, size * size)
            .mapToObj(i -> {
                int x = minX + (i / size) * (maxX - minX) / size;
                int y = minY + (i % size) * (maxY - minY) / size;
                double value = source.apply(x, y);
                sourceMinMax[0] = Math.min(sourceMinMax[0], value);
                sourceMinMax[1] = Math.max(sourceMinMax[1], value);
                return new Local<>(x, y, value);
            })
            .map(Local.map(value -> scaleTransformer.scale(value, sourceMinMax[0], sourceMinMax[1])))
            .map(Local.map(color::apply))
            .forEach(loc -> {
                graphics.setColor(loc.value);
                graphics.drawRect(loc.x, loc.y, 1, 1);
            });
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
