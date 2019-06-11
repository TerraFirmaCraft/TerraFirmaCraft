/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nuturient;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.agriculture.Nutrient;
import net.dries007.tfc.world.classic.CalendarTFC;

public interface IFood extends INBTSerializable<NBTTagCompound>
{
    float getNutrient(ItemStack stack, Nutrient nutrient);

    long getCreationDate();

    void setCreationDate(long creationDate);

    default long getRottenDate()
    {
        return CapabilityNutrients.DEFAULT_ROT_TICKS;
    }

    default boolean isRotten()
    {
        return getRottenDate() < CalendarTFC.getCalendarTime();
    }

    default void onConsumedByPlayer(@Nonnull EntityPlayer player, @Nonnull ItemStack stack)
    {
        TerraFirmaCraft.getLog().debug("onConsumedByPlayer {} {}", player, stack);
        IPlayerNutrients playerCap = player.getCapability(CapabilityNutrients.CAPABILITY_PLAYER_NUTRIENTS, null);
        if (playerCap != null)
        {
            if (isRotten() && !player.world.isRemote)
            {
                // todo: high chance of hunger, nausea, poison, weakness, slowness
                for (Supplier<PotionEffect> effectSupplier : CapabilityNutrients.getRottenFoodEffects())
                {
                    if (Constants.RNG.nextFloat() < 0.8)
                    {
                        player.addPotionEffect(effectSupplier.get());
                    }
                }
            }
            else
            {
                for (Nutrient nutrient : Nutrient.values())
                {
                    playerCap.addNutrient(nutrient, getNutrient(stack, nutrient));
                }
                ((PlayerNutrientsHandler) playerCap).debug();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    default void addNutrientInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        if (isRotten())
        {
            text.add(I18n.format("tfc.tooltip.food_rotten"));
        }
        else
        {
            text.add(I18n.format("tfc.tooltip.food_expiry_date", CalendarTFC.getTimeAndDate(getRottenDate())));
            // Show nutrient values if not rotten
            for (Nutrient nutrient : Nutrient.values())
            {
                text.add(nutrient.name().toLowerCase() + ": " + getNutrient(stack, nutrient));
            }
        }
        // todo: make this respect skills tiers
        // i.e. lowest level shows category, i.e. "Category: Grain"
        // next level shows which nutrients it has, i.e. "Category: Grain, Nutrients: Carbohydrates, Fat"
        // final level shows exact values, i.e. "Category: Grain, Nutrients: Carbohydrates (1.5), Fat (3.0)"
        text.add("DEBUG: Creation date: " + getCreationDate());
    }
}
