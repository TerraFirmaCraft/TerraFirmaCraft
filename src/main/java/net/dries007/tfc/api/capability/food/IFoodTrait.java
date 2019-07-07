/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is an interface that represents a trait that can be applied to foods
 * They can affect the decay modifier of the food,
 */
public interface IFoodTrait
{
    default float getDecayModifier()
    {
        return 1f;
    }

    @Nonnull
    String getName();

    /**
     * Adds information about the trait to the food stack
     *
     * @param stack The stack
     * @param text  The tooltip strings
     */
    @SideOnly(Side.CLIENT)
    default void addTraitInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        text.add(I18n.format("tfc.food_traits." + getName()));
    }

    class Impl implements IFoodTrait
    {
        private final String name;
        private final float decayModifier;

        public Impl(@Nonnull String name, float decayModifier)
        {

            this.name = name;
            this.decayModifier = decayModifier;
        }

        @Override
        public float getDecayModifier()
        {
            return decayModifier;
        }

        @Nonnull
        @Override
        public String getName()
        {
            return name;
        }
    }
}
