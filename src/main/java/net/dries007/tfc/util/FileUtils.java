/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import org.apache.logging.log4j.core.util.Loader;

import java.io.*;

public class FileUtils {

    public static void copyFile(String source, File dest) throws IOException
    {

        InputStream is = Loader.getResource(source, null).openStream();
        OutputStream os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0)
        {
            os.write(buffer, 0, length);
        }
    }
}
