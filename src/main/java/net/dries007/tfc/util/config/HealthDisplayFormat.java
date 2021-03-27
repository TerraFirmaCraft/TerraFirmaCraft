package net.dries007.tfc.util.config;

public enum HealthDisplayFormat
{
    TFC((curr, max) -> String.format("%.0f / %.0f", curr, max)),
    VANILLA((curr, max) -> String.format("%.1f / %.1f", curr, max)),
    TFC_CURRENT((curr, max) -> String.format("%.0f", curr)),
    VANILLA_CURRENT((curr, max) -> String.format("%.1f", curr));

    private final Function formatFunction;

    HealthDisplayFormat(Function formatFunction)
    {
        this.formatFunction = formatFunction;
    }

    public String format(float currentHealth, float maxHealth)
    {
        return formatFunction.apply(currentHealth, maxHealth);
    }

    @FunctionalInterface
    public interface Function
    {
        String apply(float currentHealth, float maxHealth);
    }
}
