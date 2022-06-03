/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageMultiblock;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;

public class MultiMultiBlockComponent extends CustomComponent
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Field MULTIBLOCK_OBJ = Helpers.uncheck(() -> {
        final Field field = PageMultiblock.class.getDeclaredField("multiblockObj");
        field.setAccessible(true);
        return field;
    });

    private @SerializedName("multiblocks") String multiblocks;

    private transient @Nullable List<SerializedMultiblock> resolvedMultiblocks;
    private transient @Nullable List<Stub> renderStubs;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (resolvedMultiblocks == null) return;

        renderStubs = new ArrayList<>();
        for (SerializedMultiblock resolved : resolvedMultiblocks)
        {
            try
            {
                final AbstractMultiblock multiblock = resolved.toMultiblock();
                final Stub stub = new Stub();
                MULTIBLOCK_OBJ.set(stub, multiblock);
                renderStubs.add(stub);
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                LOGGER.error("Building multiblock: ", e);
            }
        }

        if (renderStubs.isEmpty())
        {
            LOGGER.error("No multiblocks loaded!");
        }
    }

    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (renderStubs == null || renderStubs.isEmpty()) return;

        if (context instanceof GuiBookEntry entry)
        {
            final Stub stub = renderStubs.get((context.getTicksInBook() / 20) % renderStubs.size());
            stub.mc = context.getGui().getMinecraft();
            stub.parent = entry;
            stub.book = entry.book;
            stub.render(poseStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup)
    {
        resolvedMultiblocks = lookup.apply(IVariable.wrap(multiblocks))
            .asStream()
            .flatMap(v -> {
                try
                {
                    return Stream.of(GSON.fromJson(v.unwrap(), SerializedMultiblock.class));
                }
                catch (JsonSyntaxException e)
                {
                    LOGGER.error("Deserializing multiblock: ", e);
                    return Stream.empty();
                }
            }).toList();
    }

    static class Stub extends PageMultiblock
    {
        @Override
        public boolean shouldRenderText()
        {
            return false;
        }
    }
}
