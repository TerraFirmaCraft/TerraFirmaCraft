/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
