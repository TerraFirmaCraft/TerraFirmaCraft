/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.items.itemblock;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;

public class ItemBlockTorchTFC extends ItemBlockTFC
{

    public ItemBlockTorchTFC(Block b)
    {
        super(b);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        // noinspection ConstantConditions
        if (entityItem.getEntityWorld().getBlockState(entityItem.getPosition().down()).getBlock() == BlocksTFC.LOG_PILE)
        {
            int count = entityItem.getEntityData().getInteger("torchCount");
            if (count > 160)
            {
                TELogPile te = Helpers.getTE(entityItem.getEntityWorld(), entityItem.getPosition().down(), TELogPile.class);
                if (te != null)
                {
                    te.light();
                }
                entityItem.getEntityWorld().setBlockState(entityItem.getPosition(), Blocks.FIRE.getDefaultState());
                entityItem.setDead();
            }
            else
            {
                if (entityItem.getEntityWorld().rand.nextInt(10) == 0)
                    entityItem.getEntityWorld().spawnParticle(EnumParticleTypes.LAVA, entityItem.posX, entityItem.posY, entityItem.posZ,
                        -0.5F + Math.random(), -0.5F + Math.random(), -0.5F + Math.random());
                entityItem.getEntityData().setInteger("torchCount", count + 1);
            }
        }

        return super.onEntityItemUpdate(entityItem);
    }
}
