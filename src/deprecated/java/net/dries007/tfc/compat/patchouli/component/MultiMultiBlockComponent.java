/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IVariable;

public class MultiMultiBlockComponent extends CustomComponent
{
    private @SerializedName("multiblocks") String multiblocks;

    private transient @Nullable List<Supplier<IMultiblock>> resolvedMultiblocks;
    private transient @Nullable List<MultiblockRenderer> renderers;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup)
    {
        resolvedMultiblocks = lookup.apply(IVariable.wrap(multiblocks))
            .asStream()
            .map(this::asMultiblock)
            .flatMap(Optional::stream)
            .toList();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        renderers = Optional.ofNullable(resolvedMultiblocks)
            .map(r -> r.stream()
                .map(this::asMultiblockRenderer)
                .flatMap(Optional::stream)
                .toList())
            .orElse(null);
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (renderers != null && !renderers.isEmpty())
        {
            renderers.get((context.getTicksInBook() / 20) % renderers.size()).render(context, graphics, mouseX, mouseY, partialTicks);
        }
    }
}
