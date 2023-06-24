/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.rock.Rock;

public class OreDepositBlock extends Block
{
    public OreDepositBlock(Properties properties, Rock rock, OreDeposit ore)
    {
        this(properties, rock.ordinal(), ore.ordinal());
    }

    protected OreDepositBlock(Properties properties, int rockProperty, int oreProperty)
    {
        super(properties);
    }

}
