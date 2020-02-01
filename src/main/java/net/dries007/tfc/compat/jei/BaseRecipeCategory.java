/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.dries007.tfc.TerraFirmaCraft;

@MethodsReturnNonnullByDefault
public abstract class BaseRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T>
{
    private final IDrawable background;
    private final String localizedName;
    private final String Uid;

    public BaseRecipeCategory(IDrawable background, String Uid)
    {
        this.background = background;
        this.localizedName = Translator.translateToLocal("jei.category." + Uid);
        this.Uid = Uid;
    }

    @Override
    public String getUid()
    {
        return Uid;
    }

    @Override
    public String getTitle()
    {
        return localizedName;
    }

    @Override
    public String getModName()
    {
        return TerraFirmaCraft.MOD_NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }
}
