/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import javax.imageio.ImageIO;
import net.minecraft.util.Mth;

/**
 * Drawing utility.
 */
@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
public abstract class Artist<T, A extends Artist<T, A>>
{
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

    public static <V> Artist.Custom<V> custom(BiConsumer<V, Graphics2D> drawing)
    {
        return new Custom<>(drawing);
    }

    public static int clamp(int value, int min, int max)
    {
        return value < min ? min : Math.min(value, max);
    }

    public static int floor(double f)
    {
        return f >= 0 ? (int) f : (int) f - 1;
    }

    public static int clampedLerp(double value, int min, int max)
    {
        return clamp((int) (min + value * (max - min)), Math.min(min, max), Math.max(min, max));
    }

    protected int width = 1000, height = 1000;
    protected int minX = 0, minY = 0, maxX = width, maxY = height;

    protected Artist() {}

    public A size(int size)
    {
        return size(size, size);
    }

    public A size(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (A) this;
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

    public A centerSized(int radius)
    {
        return center(radius).size(radius * 2);
    }

    public A dimensionsSized(int size)
    {
        return dimensions(size).size(size);
    }

    public A dimensionsSized(int width, int height)
    {
        return dimensions(width, height).size(width, height);
    }

    public void draw(String name)
    {
        draw(name, null);
    }

    public void draw(String name, T instance)
    {
        try
        {
            new File("artist").mkdirs();

            final File outFile = new File("artist/" + name + ".png");
            final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics = ((Graphics2D) image.getGraphics());

            drawInternal(name, instance, graphics);
            ImageIO.write(image, "PNG", outFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void draw(T instance, Graphics graphics)
    {
        drawInternal("<internal call>", instance, graphics);
    }

    protected abstract void drawInternal(String name, T instance, Graphics graphics);

    protected A copy(Artist<?, ?> other)
    {
        dimensions(other.minX, other.minY, other.maxX, other.maxY).size(other.width);
        return (A) this;
    }

    protected final <S> Stream<Local<S>> stream(Pixel<S> step)
    {
        return IntStream.range(0, width * height)
            .mapToObj(i -> {
                int posX = (i / height);
                int posY = (i % height);
                double x = minX + (posX + 0.5) * (double) (maxX - minX) / width;
                double y = minY + (posY + 0.5) * (double) (maxY - minY) / height;
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
            return (x, y) -> pixel.apply(floor(x), floor(y));
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
            return (x, y) -> pixel.apply((int) (x + 0.5), (int) (y + 0.5));
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
            int x = clamp((int) (255 * value), 0, 255);
            return new Color(x, x, x);
        };
        public static final DoubleFunction<Color> LINEAR_BLUE_RED = value -> {
            int x = clamp((int) (255 * value), 0, 255);
            return new Color(x, 0, 255 - x);
        };
        public static final DoubleFunction<Color> LINEAR_GREEN_YELLOW = value -> {
            int x = clamp((int) (255 * value), 0, 255);
            return new Color(x, 255, 0);
        };
        public static final DoubleFunction<Color> RANDOM_NEAREST_INT = value -> {
            int x = (int) Math.round(value);
            return Artist.Colors.COLORS[Math.floorMod(x, Artist.Colors.COLORS.length)];
        };
        public static final Function<Integer, Color> RANDOM_INT = value -> Colors.COLORS[Math.floorMod(value, Colors.COLORS.length)];

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

        public static DoubleFunction<Color> linearGradient(Color from, Color to)
        {
            return value -> new Color(
                clampedLerp(value, from.getRed(), to.getRed()),
                clampedLerp(value, from.getGreen(), to.getGreen()),
                clampedLerp(value, from.getBlue(), to.getBlue())
            );
        }

        public static DoubleFunction<Color> multiLinearGradient(Color... colors)
        {
            Preconditions.checkArgument(colors.length > 2, "Must have at least three colors for multi-linear gradient");
            final DoubleFunction<Color>[] parts = IntStream.range(0, colors.length - 1)
                .mapToObj(i -> linearGradient(colors[i], colors[i + 1]))
                .toArray(DoubleFunction[]::new);
            return value -> parts[Mth.floor(value * parts.length)].apply((value * parts.length) % 1);
        }
    }

    public static final class Scales
    {
        public static final ScaleTransformer EXACT = (value, min, max) -> value;
        public static final ScaleTransformer NEAREST_INT = (value, min, max) -> (int) (value + 0.5);
        public static final ScaleTransformer DYNAMIC_RANGE = (value, min, max) -> max == min ? min : (value - min) / (max - min);

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
                }).toList().stream() // Block after min/max calculations
                .peek(loc -> {
                    if (histogram)
                    {
                        final double scaled = Scales.DYNAMIC_RANGE.apply(loc.value, sourceMinMax[0], sourceMinMax[1]);
                        distribution[clamp((int) (scaled * histogramBins), 0, histogramBins - 1)]++;
                    }
                })
                .map(Local.map(value -> color.apply(scaleTransformer.apply(value, sourceMinMax[0], sourceMinMax[1]))))
            );
            System.out.printf("Range for %s: %e - %e\n", name, sourceMinMax[0], sourceMinMax[1]);
            if (histogram)
            {
                System.out.printf("Histogram for %s\n", name);
                System.out.printf("Min = %e, Max = %e Distribution =\n%s\n", sourceMinMax[0], sourceMinMax[1], Arrays.stream(distribution).mapToObj(i -> "" + i).collect(Collectors.joining("\n")));
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
        private Function<V, Color> colorTransformer = k -> Color.BLACK;

        private Typed(Function<K, Pixel<V>> transformer)
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

    public static class Custom<T> extends Artist<T, Custom<T>>
    {
        private final BiConsumer<T, Graphics2D> drawing;

        private Custom(BiConsumer<T, Graphics2D> drawing)
        {
            this.drawing = drawing;
        }

        public Custom<T> before(BiConsumer<T, Graphics2D> pre)
        {
            return new Custom<>(pre.andThen(drawing));
        }

        public Custom<T> after(BiConsumer<T, Graphics2D> post)
        {
            return new Custom<>(drawing.andThen(post));
        }

        @Override
        protected void drawInternal(String name, T instance, Graphics graphics)
        {
            drawing.accept(instance, (Graphics2D) graphics);
        }
    }

    private record Local<T>(int x, int y, T value)
    {
        public static <A, B> Function<Local<A>, Local<B>> map(Function<A, B> transformer)
        {
            return loc -> new Local<>(loc.x, loc.y, transformer.apply(loc.value));
        }
    }
}
