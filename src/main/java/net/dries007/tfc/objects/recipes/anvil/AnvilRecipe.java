/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.anvil;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.forge.ForgeRule;

@ParametersAreNonnullByDefault
public class AnvilRecipe
{
    private static final Random RNG = new Random();

    public static AnvilRecipe deserialize(ByteBuf buffer)
    {
        Metal.Tier minTier = Metal.Tier.valueOf(buffer.readInt());
        long seed = buffer.readLong();

        ItemStack output = ByteBufUtils.readItemStack(buffer);

        int numRules = buffer.readInt();
        ForgeRule[] rules = new ForgeRule[numRules];
        for (int i = 0; i < numRules; i++)
        {
            rules[i] = ForgeRule.valueOf(buffer.readInt());
        }

        return new AnvilRecipe(output, minTier, rules).withSeed(seed);
    }

    public static void serialize(AnvilRecipe recipe, ByteBuf buffer)
    {
        // Numbers
        buffer.writeInt(recipe.minTier.ordinal());
        buffer.writeLong(recipe.workingSeed);

        // Output
        ByteBufUtils.writeItemStack(buffer, recipe.output);

        // Rules
        buffer.writeInt(recipe.rules.length);
        for (ForgeRule rule : recipe.rules)
            buffer.writeInt(ForgeRule.getID(rule));
    }

    static boolean assertValid(AnvilRecipe recipe)
    {
        if (StringUtils.isNullOrEmpty(recipe.getName()))
        {
            TerraFirmaCraft.getLog().warn("Recipe is invalid with empty name");
            return false;
        }
        if (recipe.getOutput().isEmpty())
        {
            TerraFirmaCraft.getLog().warn("Output is empty!");
            return false;
        }
        if (recipe.rules.length == 0 || recipe.rules.length > 3)
        {
            TerraFirmaCraft.getLog().warn("Rules are invalid length!");
            return false;
        }
        return true;
    }

    private final ForgeRule[] rules;
    private final ItemStack output;
    private final ItemStack input;
    private final Metal.Tier minTier;
    private long workingSeed;
    private String name;

    public AnvilRecipe(ItemStack input, ItemStack output, Metal.Tier minTier, ForgeRule... rules)
    {
        this.input = input;
        this.output = output;
        this.rules = rules;
        this.minTier = minTier;

        this.name = output.serializeNBT().toString();
    }

    private AnvilRecipe(ItemStack output, Metal.Tier minTier, ForgeRule... rules)
    {
        // Client-side only recipe constructor
        this.input = ItemStack.EMPTY;
        this.output = output;
        this.rules = rules;
        this.minTier = minTier;

        this.name = "client:" + output.serializeNBT().toString();
    }

    public boolean matches(ItemStack input)
    {
        return this.input.isItemEqual(input);
    }

    @Nonnull
    public ItemStack getOutput()
    {
        return output.copy();
    }

    public ForgeRule[] getRules()
    {
        return rules;
    }

    public String getName()
    {
        // todo: recipe names
        return name;
    }

    public int getTarget(long worldSeed)
    {
        RNG.setSeed(worldSeed + workingSeed);
        return RNG.nextInt(TEAnvilTFC.WORK_MAX + 1);
    }

    AnvilRecipe withSeed(long seed)
    {
        workingSeed = seed;
        return this;
    }


}
