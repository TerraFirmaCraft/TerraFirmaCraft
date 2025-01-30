/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * A class wrapping both the original, level seed, and exposes a method to generate sequential seeds
 * from it. Use the original {@code seed} when stability across multiple different noises is needed,
 * and {@link #next()} otherwise.
 * <p>
 * Note in some situations, the only supported access will be via {@link #seed()}, as {@link #next()}
 * would be unstable, for instance whenever accessed during world generation (after all structures
 * are initially built, in a predictable sequence).
 */
public final class Seed
{
    /**
     * @return A new {@code Seed} that is capable of generating sequential {@code next()} values.
     */
    public static Seed of(long levelSeed)
    {
        return new Seed(levelSeed, new XoroshiroRandomSource(levelSeed));
    }

    /**
     * @return A new {@code Seed} that is only capable of generating level-seed dependent structures,
     * typically for use during world generation.
     */
    public static Seed unsafeOf(long levelSeed)
    {
        return new Seed(levelSeed, null);
    }

    private final long seed;
    private final @Nullable XoroshiroRandomSource next;

    private Seed(long seed, @Nullable XoroshiroRandomSource next)
    {
        this.seed = seed;
        this.next = next;
    }

    /**
     * @return The original level seed.
     */
    public long seed()
    {
        return seed;
    }

    /**
     * @return A new, sequentially generated seed.
     */
    public long next()
    {
        assert next != null : "Unsafe to use next() in this context";
        return next.nextLong();
    }

    /**
     * @return A new {@link RandomSource}, forked from the current sequentially generated seed.
     */
    public RandomSource fork()
    {
        return new XoroshiroRandomSource(next(), next());
    }

    /**
     * @return A new {@link Seed}, which is populated exactly as this one was initially.
     */
    public Seed forkStable()
    {
        return of(seed);
    }
}
