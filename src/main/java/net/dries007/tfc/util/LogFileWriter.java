/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.dries007.tfc.TerraFirmaCraft;

/**
 * Handles writing text to a log file
 * Makes code clean :D
 */
public class LogFileWriter
{
    private static File file = null;
    private static BufferedWriter writer = null;

    /**
     * Open a file for writing
     *
     * @param fileName the file path to open
     * @return true if file could be open, false if there is already an open file or if error-ed.
     */
    public static boolean open(String fileName)
    {
        if (!isOpen())
        {
            try
            {
                file = new File(fileName);
                writer = new BufferedWriter(new FileWriter(file));
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Check if there is already an opened file
     *
     * @return true if there is one file already open for writing logs
     */
    public static boolean isOpen()
    {
        return file != null;
    }

    /**
     * Close and save the current loaded log file
     *
     * @return true if file was closed
     */
    public static boolean close()
    {
        if (isOpen())
        {
            try
            {
                writer.close();
                file = null;
                writer = null;
                return true;
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Gets the absolute path of the curren open file
     *
     * @return the file path, if open
     */
    public static String getFilePath()
    {
        if (isOpen())
        {
            return file.getAbsolutePath();
        }
        return "";
    }

    /**
     * Write text to file
     *
     * @param text String to write
     */
    public static void write(String text)
    {
        if (isOpen())
        {
            try
            {
                writer.write(text);
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
    }

    /**
     * Breaks text to the next line
     */
    public static void newLine()
    {
        if (isOpen())
        {
            try
            {
                writer.newLine();
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
    }

    /**
     * Writes text and breaks to the next line
     *
     * @param line String to write
     */
    public static void writeLine(String line)
    {
        if (isOpen())
        {
            try
            {
                writer.write(line);
                writer.newLine();
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
    }

    private LogFileWriter()
    {
    }
}