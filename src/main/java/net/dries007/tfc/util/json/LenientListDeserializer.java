package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.google.gson.*;

public class LenientListDeserializer<T, E extends Collection<T>> implements JsonDeserializer<E>
{
    private final Class<T> elementClass;
    private final Function<T, E> singletonSupplier;
    private final Function<Collection<T>, E> listSupplier;

    public LenientListDeserializer(Class<T> elementClass, Function<T, E> singletonSupplier, Function<Collection<T>, E> listSupplier)
    {
        this.elementClass = elementClass;
        this.singletonSupplier = singletonSupplier;
        this.listSupplier = listSupplier;
    }

    @Override
    public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonArray())
        {
            JsonArray array = json.getAsJsonArray();
            List<T> elements = new ArrayList<>(array.size());
            for (JsonElement element : array)
            {
                elements.add(context.deserialize(element, elementClass));
            }
            return listSupplier.apply(elements);
        }
        else
        {
            return singletonSupplier.apply(context.deserialize(json, elementClass));
        }
    }
}
