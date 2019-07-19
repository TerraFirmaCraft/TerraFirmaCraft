/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.categories;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.dries007.tfc.api.util.TFCConstants;

@MethodsReturnNonnullByDefault
public abstract class TFCRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T>
{
    private final IDrawable background;
    private final String localizedName;

    public TFCRecipeCategory(IDrawable background, String categoryName)
    {
        this.background = background;
        this.localizedName = Translator.translateToLocal(TFCConstants.MOD_ID + ".jei.category." + categoryName);
    }

    @Override
    public String getTitle()
    {
        return localizedName;
    }

    @Override
    public String getModName()
    {
        return TFCConstants.MOD_NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }
}
