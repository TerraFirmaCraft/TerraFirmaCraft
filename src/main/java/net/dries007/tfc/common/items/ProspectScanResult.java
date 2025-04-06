/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.state.BlockState;

public class ProspectScanResult {
    public final Object2IntMap<BlockState> counts;
    public final Object2IntMap<BlockState> dists;

    public ProspectScanResult(Object2IntMap<BlockState> counts, Object2IntMap<BlockState> dists)
    {
        this.counts = counts;
        this.dists = dists;
    }
}
