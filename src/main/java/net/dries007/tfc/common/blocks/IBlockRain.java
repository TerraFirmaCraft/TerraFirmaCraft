/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

/**
 * Marker interface for blocks that have no collision, but should block rain. This is done via,
 * <ul>
 *     <li>{@link net.dries007.tfc.mixin.HeightmapMixin} makes this block appear opaque to the {@code MOTION_BLOCKING} heightmap</li>
 *     <li>{@link net.dries007.tfc.client.OverworldWeatherEffects} handles making rain particles appear on top of the block,
 *     rather than underneath, by querying a unique collision shape, and the same collision shape replacement code is done in
 *     {@link net.dries007.tfc.mixin.client.WaterDropParticleMixin}.</li>
 * </ul>
 */
public interface IBlockRain
{
}
