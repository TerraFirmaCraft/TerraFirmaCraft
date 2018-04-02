package net.dries007.tfc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Constants
{
    private Constants() {}

    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";
    public static final String GUI_FACTORY = "net.dries007.tfc.client.TFCModGuiFactory";

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final Gson GSON_PP = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
}
