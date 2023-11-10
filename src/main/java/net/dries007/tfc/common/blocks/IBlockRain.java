package net.dries007.tfc.common.blocks;

/**
 * Marker interface for blocks that have no collision, but should block rain
 * Mixin makes it opaque to the MOTION_BLOCKING heightmap
 * Mixin makes rain 'splash' particles spawn on the block rather than under it
 * Mixin prevents rain particles from falling through it
 */
public interface IBlockRain
{
}
