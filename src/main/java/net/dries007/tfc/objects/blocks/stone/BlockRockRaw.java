/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.ICollapsableBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockRaw extends BlockRockVariant implements ICollapsableBlock
{

    public BlockRockRaw(Rock.Type type, Rock rock)
    {
        super(type, rock);
    }

    //todo: add collapsable mechanics
}
