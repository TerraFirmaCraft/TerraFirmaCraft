package net.dries007.tfc.objects;

public enum Gem
{
    AGATE,
    AMETHYST,
    BERYL,
    DIAMOND,
    EMERALD,
    GARNET,
    JADE,
    JASPER,
    OPAL,
    RUBY,
    SAPPHIRE,
    TOPAZ,
    TOURMALINE;

    public enum Grade
    {
        NORMAL,
        FLAWED,
        FLAWLESS,
        CHIPPED,
        EXQUISITE;

        public int getMeta()
        {
            return this.ordinal();
        }

        public static Grade fromMeta(int meta)
        {
            return values()[meta];
        }
    }
}
