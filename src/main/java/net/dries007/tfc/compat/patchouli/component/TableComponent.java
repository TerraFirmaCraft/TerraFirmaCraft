/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import net.dries007.tfc.util.JsonHelpers;

public class TableComponent extends CustomComponent
{
    @SerializedName("strings") JsonElement jsonStrings;
    @SerializedName("columns") String columnsString;
    @SerializedName("first_column_width") String headerColumnWidthString;
    @SerializedName("column_width") String columnWidthString;
    @SerializedName("row_height") String rowHeightString;
    @SerializedName("left_buffer") String leftBufferString;
    @SerializedName("top_buffer") String topBufferString;
    @SerializedName("title") JsonElement titleString;
    @SerializedName("legend") JsonElement legendString;
    @SerializedName("draw_background") String drawBackgroundString;

    @Nullable protected transient List<TableEntry> entries;
    protected transient int columns;
    protected transient int headerColumnWidth;
    protected transient int columnWidth;
    protected transient int rowHeight;
    protected transient int leftBuffer;
    protected transient int topBuffer;
    protected transient boolean drawBackground;
    @Nullable protected transient Component title;
    @Nullable protected transient List<TableEntry> legend;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup)
    {
        jsonStrings = lookup.apply(IVariable.wrap(jsonStrings)).unwrap();
        columnsString = lookup.apply(IVariable.wrap(columnsString)).asString();
        headerColumnWidthString = lookup.apply(IVariable.wrap(headerColumnWidthString)).asString();
        columnWidthString = lookup.apply(IVariable.wrap(columnWidthString)).asString();
        rowHeightString = lookup.apply(IVariable.wrap(rowHeightString)).asString();
        leftBufferString = lookup.apply(IVariable.wrap(leftBufferString)).asString();
        topBufferString = lookup.apply(IVariable.wrap(topBufferString)).asString();
        titleString = lookup.apply(IVariable.wrap(titleString)).unwrap();
        legendString = lookup.apply(IVariable.wrap(legendString)).unwrap();
        drawBackgroundString = lookup.apply(IVariable.wrap(drawBackgroundString)).asString();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);
        columns = Integer.parseInt(columnsString);
        headerColumnWidth = Integer.parseInt(headerColumnWidthString);
        columnWidth = Integer.parseInt(columnWidthString);
        rowHeight = Integer.parseInt(rowHeightString);
        leftBuffer = Integer.parseInt(leftBufferString);
        topBuffer = Integer.parseInt(topBufferString);
        title = Component.Serializer.fromJson(titleString);
        drawBackground = Boolean.parseBoolean(drawBackgroundString);
        entries = new ArrayList<>();
        if (jsonStrings.isJsonArray())
        {
            for (JsonElement element : jsonStrings.getAsJsonArray())
            {
                final JsonObject json = element.getAsJsonObject();
                if (json.has("fill"))
                {
                    final int color = Integer.decode(JsonHelpers.getAsString(json, "fill"));
                    entries.add(new TableEntry(Component.empty(), convert(color)));
                }
                else
                {
                    final Component component = Component.Serializer.fromJson(element);
                    if (component != null)
                    {
                        entries.add(new TableEntry(component.copy().withStyle(component.getStyle().withFont(Minecraft.UNIFORM_FONT)), 0));
                    }
                    else
                    {
                        throw new JsonParseException("Failed to parse component: " + element);
                    }
                }
            }
        }
        if (legendString.isJsonArray())
        {
            legend = new ArrayList<>();
            for (JsonElement entry : legendString.getAsJsonArray())
            {
                final JsonObject json = entry.getAsJsonObject();
                final Component text = Component.Serializer.fromJson(json.get("text"));
                if (text != null)
                {
                    final int color = Integer.decode(JsonHelpers.getAsString(json, "color"));
                    legend.add(new TableEntry(text.copy().withStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT)), convert(color)));
                }
                else
                {
                    throw new JsonParseException("Failed to parse component: " + json.get("text"));
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (entries != null && !entries.isEmpty())
        {
            final Font font = Minecraft.getInstance().font;
            final int cols = columns;
            final int height = rowHeight;
            final int leftStart = leftBuffer;
            final int firstColumnWidth = 45;
            final int regularWidth = columnWidth;
            final int totalWidth = (cols) * regularWidth + firstColumnWidth;
            final int totalHeight = (Mth.ceil(((float) entries.size()) / cols) - 1) * height;
            int xo = leftStart;
            int yo = topBuffer;

            graphics.fill(110, -10, 130, Math.min(totalHeight + 20, 130), 0xFFfff9ec); // page background
            if (drawBackground)
                graphics.fill(xo + firstColumnWidth, yo + height, xo + totalWidth + 1, yo + totalHeight + 1, 0xff343330); // table background

            if (title != null)
                graphics.drawString(font, title, 122 - (font.width(title) / 2), 2, 0, false);

            int index = 0;
            for (TableEntry entry : entries)
            {
                final int width = index == 0 ? firstColumnWidth : regularWidth;
                if (entry.text.getContents() != ComponentContents.EMPTY)
                {
                    if (!drawBackground)
                        graphics.renderOutline(xo, yo, width + 1, height + 1, 0xff343330);
                    graphics.drawString(font, entry.text, xo + 2, yo, entry.color, false);
                }
                else
                {
                    final int color = entry.color;
                    if (color != 0)
                    {
                        graphics.fill(xo + 1, yo + 1, xo + width, yo + height, color);
                    }
                }
                index++;
                xo += width;
                if (index > cols)
                {
                    index = 0;
                    xo = leftStart;
                    yo += height;
                }
            }

            if (legend != null && !legend.isEmpty())
            {
                final int legendX = 130;
                int legendY = totalHeight + 14;
                graphics.drawString(font, Component.translatable("tfc.tooltip.legend").withStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT).withBold(true)), legendX, legendY, 0,  false);
                legendY += 9;
                for (TableEntry entry : legend)
                {
                    graphics.fill(legendX + 1, legendY + 1, legendX + 10, legendY + 8, entry.color);
                    graphics.drawString(font, entry.text, legendX + 12, legendY, 0,  false);
                    legendY += 9;
                }
            }
        }
    }

    private static int convert(int color)
    {
        return FastColor.ARGB32.color(255, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
    }

    public record TableEntry(Component text, int color) {}
}
