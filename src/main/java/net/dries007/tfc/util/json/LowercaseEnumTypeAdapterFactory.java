/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Thanks https://stackoverflow.com/questions/9064433/gson-non-case-sensitive-enum-deserialization
 */
public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory
{
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
    {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (!rawType.isEnum())
        {
            return null;
        }

        final Map<String, T> lowercaseToConstant = new HashMap<>();
        for (T constant : rawType.getEnumConstants())
        {
            lowercaseToConstant.put(toLowercase(constant), constant);
        }

        return new TypeAdapter<T>()
        {
            @Override
            public void write(JsonWriter out, T value) throws IOException
            {
                if (value == null)
                {
                    out.nullValue();
                }
                else
                {
                    out.value(toLowercase(value));
                }
            }

            @Override
            public T read(JsonReader reader) throws IOException
            {
                if (reader.peek() == JsonToken.NULL)
                {
                    reader.nextNull();
                    return null;
                }
                else
                {
                    return lowercaseToConstant.get(reader.nextString());
                }
            }
        };
    }

    private String toLowercase(Object o)
    {
        return o.toString().toLowerCase(Locale.US);
    }
}