/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public class TFCInventoryGuiHandler<T extends AbstractContainerScreen<? extends AbstractContainerMenu>> implements IGuiContainerHandler<T>
{
    @Override
    public List<Rect2i> getGuiExtraAreas(T guiContainer)
    {
        List<Rect2i> areas = new ArrayList<>();

        int xPosition = guiContainer.getGuiLeft() + 176;
        int yPosition = guiContainer.getGuiTop() + 4; // +23 each button
        int w = 20;
        int h = 22;
        for (int i = 0; i < 4; i++)
        {
            Rect2i rectangle = new Rect2i(xPosition, yPosition + i * 23, w, h);
            areas.add(rectangle);
        }
        // Only add the patchouli button if enabled
        PatchouliIntegration.ifEnabled(() -> areas.add(new Rect2i(xPosition, yPosition + 4 * 23, w, h)));

        return areas;
    }
}