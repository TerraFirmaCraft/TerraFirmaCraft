/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import net.minecraft.util.math.MathHelper;

/**
 * Artist for drawing images of specific types.
 * Explicitly handles noise, types, and direct color per pixel.
 */
@SuppressWarnings("unchecked")
public abstract class Artist<T, A extends Artist<T, A>>
{
    public static Artist.Raw empty()
    {
        return new Raw(); // An empty artist, for drawing direct pixel -> color objects
    }

    public static <K, V> Artist.Typed<K, V> map(Function<V, Pixel<K>> transformer)
    {
        return new Artist.Typed<>(transformer); // An artist that handles pixel -> K objects
    }

    public static <V> Artist.Noise<V> mapNoise(Function<V, DPixel> transformer)
    {
        return new Artist.Noise<>(transformer); // An artist that handles pixel -> double objects
    }

    public static <V> Artist.Colored<V> mapColor(Function<V, Pixel<Color>> transformer)
    {
        return new Artist.Colored<>(transformer); // An artist that handler pixel -> color objects with predefined colors
    }

    protected int size = 1000;
    protected int minX = 0, minY = 0, maxX = size, maxY = size;

    public A size(int size)
    {
        this.size = size;
        return (A) this;
    }

    public A center(int radius)
    {
        return center(0, 0, radius, radius);
    }

    public A center(int x, int y)
    {
        return center(x, y, maxX - minX, maxY - minY);
    }

    public A center(int x, int y, int radius)
    {
        return center(x, y, radius, radius);
    }

    public A center(int x, int y, int width, int height)
    {
        return dimensions(x - width / 2, x + width / 2, y - height / 2, y + height / 2);
    }

    public A dimensions(int side)
    {
        return dimensions(side, side);
    }

    public A dimensions(int width, int height)
    {
        return dimensions(0, 0, width, height);
    }

    public A dimensions(int minX, int minY, int maxX, int maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return (A) this;
    }

    public void draw(String name, T instance)
    {
        try
        {
            File outFile = new File(name + ".png");
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = ((Graphics2D) image.getGraphics());
            drawInternal(name, instance, graphics);
            ImageIO.write(image, "PNG", outFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected abstract void drawInternal(String name, T instance, Graphics graphics);

    protected final <S> Stream<Local<S>> stream(Pixel<S> step)
    {
        return IntStream.range(0, size * size)
            .mapToObj(i -> {
                int posX = (i / size);
                int posY = (i % size);
                double x = minX + (posX + 0.5) * (double) (maxX - minX) / size;
                double y = minY + (posY + 0.5) * (double) (maxY - minY) / size;
                S s = step.apply(x, y);
                return new Local<>(posX, posY, s);
            });
    }

    protected final <S> Stream<S> blocking(Stream<S> stream)
    {
        return stream.collect(Collectors.toList()).stream();
    }

    protected final void drawStream(Graphics graphics, Stream<Local<Color>> stream)
    {
        stream.forEach(loc -> {
            graphics.setColor(loc.value);
            graphics.fillRect(loc.x, loc.y, 1, 1);
        });
    }

    @FunctionalInterface
    public interface Pixel<T>
    {
        T apply(double x, double y);
    }

    @FunctionalInterface
    public interface DPixel
    {
        double apply(double x, double y);
    }

    @FunctionalInterface
    public interface ScaleTransformer
    {
        double apply(double value, double min, double max);
    }

    public static final class Colors
    {
        public static final DoubleFunction<Color> LINEAR_GRAY = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, x, x);
        };
        public static final DoubleFunction<Color> LINEAR_BLUE_RED = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, 0, 255 - x);
        };
        public static final DoubleFunction<Color> LINEAR_GREEN_YELLOW = value -> {
            int x = MathHelper.clamp((int) (255 * value), 0, 255);
            return new Color(x, 255, 0);
        };

        public static final Color[] COLORS = new Color[] {
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
    }

    public static final class Scales
    {
        public static final ScaleTransformer EXACT = (value, min, max) -> value;
        public static final ScaleTransformer NEAREST_INT = (value, min, max) -> (int) (value + 0.5);
        public static final ScaleTransformer DYNAMIC_RANGE = (value, min, max) -> max == min ? value : (value - min) / (max - min);

        public static ScaleTransformer fixedRange(double fixedMin, double fixedMax)
        {
            return (value, min, max) -> DYNAMIC_RANGE.apply(value, fixedMin, fixedMax);
        }

        public static ScaleTransformer dynamicRangeRounded(int increments)
        {
            return (value, min, max) -> {
                double dynamic = DYNAMIC_RANGE.apply(value, min, max);
                return (double) ((int) (dynamic * increments)) / increments;
            };
        }
    }

    public static final class Noise<T> extends Artist<T, Noise<T>>
    {
        private final Function<T, DPixel> noiseTransformer;
        private DoubleFunction<Color> color = Colors.LINEAR_GRAY;
        private ScaleTransformer scaleTransformer = Scales.DYNAMIC_RANGE;

        private Noise(Function<T, DPixel> noiseTransformer)
        {
            this.noiseTransformer = noiseTransformer;
        }

        public Noise<T> scale(ScaleTransformer scale)
        {
            this.scaleTransformer = scale;
            return this;
        }

        public Noise<T> color(DoubleFunction<Color> color)
        {
            this.color = color;
            return this;
        }

        @Override
        protected void drawInternal(String name, T instance, Graphics graphics)
        {
            final double[] sourceMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
            DPixel source = noiseTransformer.apply(instance);
            drawStream(graphics, blocking(stream((x, y) -> {
                double value = source.apply(x, y);
                sourceMinMax[0] = Math.min(sourceMinMax[0], value);
                sourceMinMax[1] = Math.max(sourceMinMax[1], value);
                return value;
            })).map(Local.map(value -> color.apply(scaleTransformer.apply(value, sourceMinMax[0], sourceMinMax[1])))));
        }
    }

    public static final class Raw extends Artist<Pixel<Color>, Raw>
    {
        @Override
        protected void drawInternal(String name, Pixel<Color> instance, Graphics graphics)
        {
            drawStream(graphics, stream(instance));
        }
    }

    public static final class Colored<T> extends Artist<T, Colored<T>>
    {
        private final Function<T, Pixel<Color>> colorTransformer;

        private Colored(Function<T, Pixel<Color>> colorTransformer)
        {
            this.colorTransformer = colorTransformer;
        }

        @Override
        protected void drawInternal(String name, T instance, Graphics graphics)
        {
            drawStream(graphics, stream(colorTransformer.apply(instance)));
        }
    }

    public static final class Typed<K, V> extends Artist<V, Typed<K, V>>
    {
        private final Function<V, Pixel<K>> transformer;
        private Function<K, Color> colorTransformer;

        public Typed(Function<V, Pixel<K>> transformer)
        {
            this.transformer = transformer;
        }

        public Typed<K, V> color(Function<K, Color> colorTransformer)
        {
            this.colorTransformer = colorTransformer;
            return this;
        }

        @Override
        protected void drawInternal(String name, V instance, Graphics graphics)
        {
            final Pixel<K> pixel = transformer.apply(instance);
            drawStream(graphics, stream((x, y) -> colorTransformer.apply(pixel.apply(x, y))));
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
