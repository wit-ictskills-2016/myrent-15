package org.wit.android.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class FileIOHelper
{
  public static boolean write(Context context, String filename, byte[] data)
  {
    FileOutputStream outputStream = null;
    boolean success = true;
    try
    {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      outputStream.write(data);
    }
    catch (Exception e)
    {
      LogHelpers.info(context,"Error writing to file " + filename + " " + e.getMessage());
      success = false;
    }
    finally
    {
      try
      {
        if (outputStream != null)
          outputStream.close();
      }
      catch (Exception e)
      {
        LogHelpers.info(context, "Error closing file " + filename + " " + e.getMessage());
        success = false;
      }
    }
    return success;
  }

  public static byte[] byteArray(Bitmap bmp)
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
    return stream.toByteArray();
  }

  public static boolean writeBitmap(Context context, String filename, Bitmap bmp)
  {
    return write(context, filename, byteArray(bmp));
  }
}
