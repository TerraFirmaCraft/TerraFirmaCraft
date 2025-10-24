/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.stack;

import java.util.Arrays;
import java.util.List;
import com.google.common.collect.Lists;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.compat.emi.EmiHelpers;

/**
 * This exists because using {@link EmiIngredient.of} on lists of {@link EmiStack}
 * will sometimes return an EMI tag ingredient, which does not retain components for whatever reason.
 *
 * If you can find a way around that feel free to replace.
 */
public class EmiSizedIngredient implements EmiIngredient
{
    private final List<EmiStack> stacks;
    private long amount;
    private float chance = 1;

    public EmiSizedIngredient(SizedIngredient ingredient)
    {
        amount = ingredient.count();
        stacks = Arrays.stream(ingredient.getItems()).map(EmiHelpers::nonDecayStack).toList();
    }

    public EmiSizedIngredient(List<EmiStack> stack, long amount)
    {
        this.amount = amount;
        this.stacks = List.copyOf(stack);
    }

    @Override
    public List<EmiStack> getEmiStacks()
    {
        return stacks;
    }

    @Override
    public EmiIngredient copy()
    {
        return new EmiSizedIngredient(stacks, amount).setChance(chance);
    }

    @Override
    public long getAmount()
    {
        return amount;
    }

    @Override
    public EmiIngredient setAmount(long amount)
    {
        this.amount = amount;
        return this;
    }

    @Override
    public float getChance()
    {
        return chance;
    }

    @Override
    public EmiIngredient setChance(float chance)
    {
        this.chance = chance;
        return this;
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags)
    {
        if (stacks.isEmpty())
        {
            return;
        }
        int item = (int) (System.currentTimeMillis() / 1000 % stacks.size());
        stacks.get(item).render(draw, x, y, delta, flags);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip()
    {
        List<ClientTooltipComponent> tooltip = Lists.newArrayList();
        int item = (int) (System.currentTimeMillis() / 1000 % stacks.size());
        tooltip.addAll(stacks.get(item).copy().setAmount(amount).getTooltip());
        return tooltip;
    }
}
