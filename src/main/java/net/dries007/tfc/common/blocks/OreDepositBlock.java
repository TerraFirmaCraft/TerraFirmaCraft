/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.rock.Rock;

public class OreDepositBlock extends Block implements ItemPropertyProviderBlock
{
    private final int rockProperty;
    private final int oreProperty;

    public OreDepositBlock(Properties properties, Rock rock, OreDeposit ore)
    {
        this(properties, rock.ordinal(), ore.ordinal());
    }

    protected OreDepositBlock(Properties properties, int rockProperty, int oreProperty)
    {
        super(properties);

        this.rockProperty = rockProperty;
        this.oreProperty = oreProperty;
    }

    @Override
    public int getValue(Type type)
    {
        if (type == OreDeposit.ROCK_PROPERTY)
        {
            return rockProperty;
        }
        if (type == OreDeposit.ORE_PROPERTY)
        {
            return oreProperty;
        }
        return 0;
    }
}
