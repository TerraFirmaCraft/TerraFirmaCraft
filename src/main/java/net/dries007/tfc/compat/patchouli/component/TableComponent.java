/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public class TableComponent extends CustomComponent
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private static int convert(int color)
    {
        return FastColor.ARGB32.color(255, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
    }

    @SerializedName("strings") JsonElement jsonStrings;
    @SerializedName("columns") String columnsString;
    @SerializedName("first_column_width") String firstColumnWidthString;
    @SerializedName("column_width") String columnWidthString;
    @SerializedName("row_height") String rowHeightString;
    @SerializedName("left_buffer") String leftBufferString;
    @SerializedName("top_buffer") String topBufferString;
    @SerializedName("title") JsonElement titleString;
    @SerializedName("legend") JsonElement legendString;
    @SerializedName("draw_background") String drawBackgroundString;

    @Nullable protected transient List<TableEntry> entries;
    protected transient int columns;
    protected transient int firstColumnWidth;
    protected transient int columnWidth;
    protected transient int rowHeight;
    protected transient int leftBuffer;
    protected transient int topBuffer;
    protected transient boolean drawBackground;
    @Nullable protected transient Component title;
    @Nullable protected transient List<TableEntry> legend;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider provider)
    {
        super.onVariablesAvailable(lookup, provider);

        columns = lookup.apply(IVariable.wrap(columnsString, provider)).asNumber().intValue();
        firstColumnWidth = lookup.apply(IVariable.wrap(firstColumnWidthString, provider)).asNumber().intValue();
        columnWidth = lookup.apply(IVariable.wrap(columnWidthString, provider)).asNumber().intValue();
        rowHeight = lookup.apply(IVariable.wrap(rowHeightString, provider)).asNumber().intValue();
        leftBuffer = lookup.apply(IVariable.wrap(leftBufferString, provider)).asNumber().intValue();
        topBuffer = lookup.apply(IVariable.wrap(topBufferString, provider)).asNumber().intValue();
        drawBackground = lookup.apply(IVariable.wrap(drawBackgroundString, provider)).asBoolean();
        jsonStrings = lookup.apply(IVariable.wrap(jsonStrings, provider)).unwrap();
        titleString = lookup.apply(IVariable.wrap(titleString, provider)).unwrap();
        legendString = lookup.apply(IVariable.wrap(legendString, provider)).unwrap();

        entries = new ArrayList<>();
        legend = new ArrayList<>();

        try
        {
            title = Component.Serializer.fromJson(titleString, provider);

            for (JsonElement element : jsonStrings.getAsJsonArray())
            {
                final JsonObject json = element.getAsJsonObject();
                if (json.has("fill"))
                {
                    final int color = Integer.decode(GsonHelper.getAsString(json, "fill"));
                    entries.add(new TableEntry(null, convert(color)));
                }
                else
                {
                    asTextComponent(element)
                        .map(text -> new TableEntry(text.copy().withStyle(style -> style.withFont(Minecraft.UNIFORM_FONT)), 0))
                        .ifPresent(entries::add);
                }
            }

            for (JsonElement entry : legendString.getAsJsonArray())
            {
                final JsonObject json = entry.getAsJsonObject();

                asTextComponent(json.get("text"))
                    .map(text -> {
                        final int color = Integer.decode(GsonHelper.getAsString(json, "color"));
                        return new TableEntry(text.copy().withStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT)), convert(color));
                    })
                    .ifPresent(legend::add);
            }
        }
        catch (JsonSyntaxException e)
        {
            LOGGER.error("Cannot parse table entries", e);
            entries.clear();
            legend.clear();
        }
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (entries != null && !entries.isEmpty())
        {
            renderSetup(graphics);

            final Font font = Minecraft.getInstance().font;
            final int leftStart = leftBuffer;
            final int regularWidth = columnWidth;
            final int totalWidth = (columns) * regularWidth + firstColumnWidth;
            final int totalHeight = (Mth.ceil(((float) entries.size()) / columns) - 1) * rowHeight;

            int xo = leftStart;
            int yo = topBuffer;

            // Draw over the central book-binding graphic with a small bit of texture
            // (0, 0) is at (15, 18) on the patchouli base book texture
            graphics.blit(PatchouliIntegration.TEXTURE, 86, -8, 186, 0, 70, 162);

            if (drawBackground)
            {
                graphics.fill(xo + firstColumnWidth, yo + rowHeight, xo + totalWidth + 1, yo + totalHeight + 1, 0xff343330); // table background
            }
            if (title != null)
            {
                graphics.drawString(font, title, 122 - (font.width(title) / 2), 2, 0, false);
            }

            int index = 0;
            for (TableEntry entry : entries)
            {
                final int width = index == 0 ? firstColumnWidth : regularWidth;
                if (entry.text != null)
                {
                    if (!drawBackground)
                        graphics.renderOutline(xo, yo, width + 1, rowHeight + 1, 0xff343330);
                    graphics.drawString(font, entry.text, xo + 2, yo, entry.color, false);
                }
                else
                {
                    final int color = entry.color;
                    if (color != 0)
                    {
                        graphics.fill(xo + 1, yo + 1, xo + width, yo + rowHeight, color);
                    }
                }
                index++;
                xo += width;
                if (index > columns)
                {
                    index = 0;
                    xo = leftStart;
                    yo += rowHeight;
                }
            }

            if (legend != null && !legend.isEmpty())
            {
                final int legendX = 130;
                int legendY = totalHeight + 14 + 2;
                graphics.drawString(font, Component.translatable("tfc.tooltip.legend").withStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT).withBold(true)), legendX, legendY, 0,  false);
                legendY += 9;
                for (TableEntry entry : legend)
                {
                    graphics.fill(legendX + 1, legendY + 1, legendX + 10, legendY + 8, entry.color);
                    if (entry.text != null) graphics.drawString(font, entry.text, legendX + 12, legendY, 0,  false);
                    legendY += 9;
                }
            }

            graphics.pose().popPose();
        }
    }

    public record TableEntry(@Nullable Component text, int color) {}
}
