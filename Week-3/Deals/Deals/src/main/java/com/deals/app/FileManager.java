/*
 * Project:		Deals
 *
 * Package:		Deals-Deals
 *
 * Author:		aaronburke
 *
 * Date:		 	1 21, 2014
 */

package com.deals.app;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    // Singleton Creation
    private static FileManager m_instance;
    private FileManager(){
        // Constructor empty for singleton
    }
    // Check if m_instance is null, if so create the singleton, otherwise it is created already
    public static FileManager getMinstance() {
        if (m_instance == null) {
            m_instance = new FileManager();
        }
        return m_instance;
    }


    public Boolean writeFile(Context context, String filename, String content) {
        Log.i("FILEMANAGER", "Starting writeFile");
        Log.i("FILEMANAGER", "filename: " + filename);

        Boolean result = false;
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
            result = true;
            Log.i("FILEMANAGER", "Write file success");
        } catch (Exception e) {
            Log.e("FILEMANAGER", "Write error: " + e.toString());
            e.printStackTrace();
        }


        return result;
    }

    public static String readFile(Context context, String filename) {
        Log.i("FILEMANAGER", "Starting readFile");
        Log.i("FILEMANAGER", "filename: " + filename);

        String result = "";

        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] contentBytes = new byte[1024];
            int bytesRead = 0;
            StringBuilder contentBuilder = new StringBuilder();

            while ((bytesRead = bis.read(contentBytes)) != -1) {
                result = new String(contentBytes, 0, bytesRead);
                contentBuilder.append(result);
            }
            result = contentBuilder.toString();

            Log.i("FILEMANAGER", "Success result= " + result);
        } catch (Exception e) {
            Log.e("FILEMANAGER", "Write file error: " + e.toString());
        } finally {

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return result;
    }


}
