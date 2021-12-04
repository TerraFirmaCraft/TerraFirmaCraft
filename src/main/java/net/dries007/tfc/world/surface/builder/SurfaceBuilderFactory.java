package net.dries007.tfc.world.surface.builder;

/**
 * A surface builder template.
 *
 * @see Invariant for surface builders that have no internal state
 */
public interface SurfaceBuilderFactory
{
    SurfaceBuilder apply(long seed);

    interface Invariant extends SurfaceBuilder, SurfaceBuilderFactory
    {
        @Override
        default SurfaceBuilder apply(long seed)
        {
            return this;
        }
    }
}
