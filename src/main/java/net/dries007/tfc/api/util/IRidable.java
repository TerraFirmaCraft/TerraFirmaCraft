/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.api.types.IAnimalTFC.Age;
import net.dries007.tfc.network.PacketSimpleMessage;
import net.dries007.tfc.network.PacketSimpleMessage.MessageCategory;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRidable
{
    default <A extends EntityAnimal & IAnimalTFC> boolean attemptApplyHalter(A animal, World world, EntityPlayer player, ItemStack stack)
    {
        if (animal.getAge() != Age.CHILD && animal.getFamiliarity() > 0.15f)
        {
            if (!world.isRemote)
            {
                // Can't use EntityAnimal#consumeItemFromStack since thats protected
                if (!player.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }
                setHalter(true);
            }
            return true;
        }
        else
        {
            // Show tooltips
            if (!world.isRemote)
            {
                if (animal.getAge() == Age.CHILD)
                {
                    TerraFirmaCraft.getNetwork().sendTo(PacketSimpleMessage.translateMessage(MessageCategory.ANIMAL, MOD_ID + ".tooltip.animal.product.young", animal.getAnimalName()), (EntityPlayerMP) player);
                }
                else
                {
                    TerraFirmaCraft.getNetwork().sendTo(PacketSimpleMessage.translateMessage(MessageCategory.ANIMAL, MOD_ID + ".tooltip.animal.product.low_familiarity", animal.getAnimalName()), (EntityPlayerMP) player);
                }
            }
            return false;
        }
    }

    /**
     * @return true if itemstack is in 'halter' oredict and the animal does not have a halter
     */
    default boolean canAcceptHalter(ItemStack stack)
    {
        return !isHalter() && OreDictionaryHelper.doesStackMatchOre(stack, "halter");
    }

    void setHalter(boolean state);

    boolean isHalter();
}
