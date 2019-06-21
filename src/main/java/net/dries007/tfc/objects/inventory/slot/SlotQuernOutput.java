/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.recipes.QuernRecipeManager;

@ParametersAreNonnullByDefault
public class SlotQuernOutput extends SlotOutput
{
    private final EntityPlayer player;
    private int removeCount;

    public SlotQuernOutput(EntityPlayer player, IItemHandler inventory, int slotIndex, int xPosition, int yPosition)
    {
        super(inventory, slotIndex, xPosition, yPosition);
        this.player = player;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.removeCount += Math.min(amount, this.getStack().getCount());
        }

        return super.decrStackSize(amount);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.removeCount += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.player.world, this.player, this.removeCount);

        if (!this.player.world.isRemote)
        {
            int i = this.removeCount;
            float f = QuernRecipeManager.getInstance().getGrindingExperience(stack);

            if (f == 0.0F)
            {
                i = 0;
            }
            else if (f < 1.0F)
            {
                int j = MathHelper.floor((float) i * f);

                if (j < MathHelper.ceil((float) i * f) && Math.random() < (double) ((float) i * f - (float) j))
                {
                    ++j;
                }

                i = j;
            }

            while (i > 0)
            {
                int k = EntityXPOrb.getXPSplit(i);
                i -= k;
                this.player.world.spawnEntity(new EntityXPOrb(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
            }
        }

        this.removeCount = 0;
    }

    @Nonnull
    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        this.onCrafting(stack);
        super.onTake(thePlayer, stack);
        return stack;
    }
}