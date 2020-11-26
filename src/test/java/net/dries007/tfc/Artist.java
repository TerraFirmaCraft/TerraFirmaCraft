/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.math.MathHelper;

/**
 * Artist for drawing images of specific types.
 * Explicitly handles noise, types, and direct color per pixel.
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class Artist<T, A extends Artist<T, A>>
{
    private static final Level LEVEL = Level.forName("UNITTEST", 50);
    private static final Logger LOGGER = LogManager.getLogger();

    public static Artist.Raw raw()
    {
        return new Raw(); // An empty artist, for drawing direct pixel -> color objects
    }

    public static <K, V> Artist.Typed<K, V> forMap(Function<K, Pixel<V>> transformer)
    {
        return new Artist.Typed<>(transformer); // An artist that handles pixel -> K objects
    }

    public static <V> Artist.Noise<V> forNoise(Function<V, NoisePixel> transformer)
    {
        return new Artist.Noise<>(transformer); // An artist that handles pixel -> double objects
    }

    public static <V> Artist.Colored<V> forColor(Function<V, Pixel<Color>> transformer)
    {
        return new Artist.Colored<>(transformer); // An artist that handler pixel -> color objects with predefined colors
    }

    protected int size = 1000;
    protected int minX = 0, minY = 0, maxX = size, maxY = size;

    protected Artist() {}

    public A size(int size)
    {
        this.size = size;
        return (A) this;
    }

    public A centerSized(int radius)
    {
        size(radius * 2);
        return center(radius);
    }

    public A center(int radius)
    {
        return center(0, 0, 2 * radius, 2 * radius);
    }

    public A center(int x, int y)
    {
        return center(x, y, maxX - minX, maxY - minY);
    }

    public A center(int x, int y, int radius)
    {
        return center(x, y, 2 * radius, 2 * radius);
    }

    public A center(int x, int y, int width, int height)
    {
        return dimensions(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
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

    protected A copy(Artist<?, ?> other)
    {
        dimensions(other.minX, other.minY, other.maxX, other.maxY).size(other.size);
        return (A) this;
    }

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

    protected final void drawStream(Graphics graphics, Stream<Local<Color>> stream)
    {
        stream.forEach(loc -> {
            graphics.setColor(loc.value);
            graphics.fillRect(loc.x, loc.y, 1, 1);
        });
    }

    /**
     * A mapping from (x, y) -> T. Includes variants for int and float coordinates
     */
    @FunctionalInterface
    public interface Pixel<T>
    {
        static <T> Pixel<T> coerceInt(IntPixel<T> pixel)
        {
            return (x, y) -> pixel.apply((int) x, (int) y);
        }

        static <T> Pixel<T> coerceFloat(FloatPixel<T> pixel)
        {
            return (x, y) -> pixel.apply((float) x, (float) y);
        }

        T apply(double x, double y);
    }

    public interface IntPixel<T>
    {
        T apply(int x, int y);
    }

    public interface FloatPixel<T>
    {
        T apply(float x, float y);
    }

    /**
     * A mapping from (x, y) -> value.
     */
    @FunctionalInterface
    public interface NoisePixel
    {
        static NoisePixel coerceInt(IntNoisePixel pixel)
        {
            return (x, y) -> pixel.apply((int) x, (int) y);
        }

        static NoisePixel coerceFloat(FloatNoisePixel pixel)
        {
            return (x, y) -> pixel.apply((float) x, (float) y);
        }

        double apply(double x, double y);
    }

    public interface IntNoisePixel
    {
        double apply(int x, int y);
    }

    public interface FloatNoisePixel
    {
        double apply(float x, float y);
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

        private static final Random RNG = new Random();

        public static Color random()
        {
            return new Color(RNG.nextInt(255), RNG.nextInt(255), RNG.nextInt(255));
        }
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

    @SuppressWarnings("UnusedReturnValue")
    public static final class Noise<T> extends Artist<T, Noise<T>>
    {
        private final Function<T, NoisePixel> noiseTransformer;
        private DoubleFunction<Color> color = Colors.LINEAR_GRAY;
        private ScaleTransformer scaleTransformer = Scales.DYNAMIC_RANGE;
        private boolean histogram = false;
        private int histogramBins = 0;

        private Noise(Function<T, NoisePixel> noiseTransformer)
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

        public Noise<T> distribution(int bins)
        {
            histogram = true;
            histogramBins = Math.max(1, bins);
            return this;
        }

        @Override
        protected void drawInternal(String name, T instance, Graphics graphics)
        {
            final double[] sourceMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
            final int[] distribution = new int[histogramBins];
            final NoisePixel source = noiseTransformer.apply(instance);
            drawStream(graphics, stream((x, y) -> {
                    double value = source.apply(x, y);
                    sourceMinMax[0] = Math.min(sourceMinMax[0], value);
                    sourceMinMax[1] = Math.max(sourceMinMax[1], value);
                    return value;
                })
                .collect(Collectors.toList()).stream() // Block after min/max calculations
                .peek(loc -> {
                    if (histogram)
                    {
                        final double scaled = Scales.DYNAMIC_RANGE.apply(loc.value, sourceMinMax[0], sourceMinMax[1]);
                        distribution[MathHelper.clamp((int) (scaled * histogramBins), 0, histogramBins - 1)]++;
                    }
                })
                .map(Local.map(value -> color.apply(scaleTransformer.apply(value, sourceMinMax[0], sourceMinMax[1]))))
            );
            LOGGER.log(LEVEL, "Range for {}: {} - {}", name, sourceMinMax[0], sourceMinMax[1]);
            if (histogram)
            {
                LOGGER.log(LEVEL, "Histogram for {}", name);
                LOGGER.log(LEVEL, "Min = {}, Max = {} Distribution =\n{}", sourceMinMax[0], sourceMinMax[1], Arrays.stream(distribution).mapToObj(i -> "" + i).collect(Collectors.joining("\n")));
            }
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

    public static final class Typed<K, V> extends Artist<K, Typed<K, V>>
    {
        private final Function<K, Pixel<V>> transformer;
        private Function<V, Color> colorTransformer = k -> Colors.random();

        public Typed(Function<K, Pixel<V>> transformer)
        {
            this.transformer = transformer;
        }

        public Typed<K, V> color(Function<V, Color> colorTransformer)
        {
            this.colorTransformer = colorTransformer;
            return this;
        }

        public Noise<K> mapNoise(ToDoubleFunction<V> noiseTransformer)
        {
            return Artist.<K>forNoise(v -> (x, y) -> noiseTransformer.applyAsDouble(transformer.apply(v).apply(x, y))).copy(this);
        }

        public <R> Typed<K, R> mapTo(Function<V, R> transformer)
        {
            return Artist.<K, R>forMap(k -> (x, y) -> transformer.apply(this.transformer.apply(k).apply(x, y))).copy(this);
        }

        @Override
        protected void drawInternal(String name, K instance, Graphics graphics)
        {
            final Pixel<V> pixel = transformer.apply(instance);
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
