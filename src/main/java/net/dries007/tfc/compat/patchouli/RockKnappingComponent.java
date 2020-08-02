/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.rock.ItemRock;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class RockKnappingComponent extends KnappingComponent
{
    @VariableHolder
    @SerializedName("rock")
    public String rockName;

    private transient ResourceLocation[] textures;
    private transient ItemStack[] stacks;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);
        Collection<Rock> rocks = TFCRegistries.ROCKS.getValuesCollection();
        textures = new ResourceLocation[rocks.size()];
        stacks = new ItemStack[rocks.size()];
        int i = 0;
        for (Rock rock : rocks)
        {
            textures[i] = rock.getTexture();
            stacks[i] = ItemRock.get(rock, 1);
            i++;
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getSquareLow(int ticks)
    {
        return null;
    }

    @Nullable
    @Override
    protected ResourceLocation getSquareHigh(int ticks)
    {
        return textures[(ticks / 20) % textures.length];
    }

    @Nonnull
    @Override
    protected ItemStack getInputItem(int ticks)
    {
        return stacks[(ticks / 20) % stacks.length];
    }
}
