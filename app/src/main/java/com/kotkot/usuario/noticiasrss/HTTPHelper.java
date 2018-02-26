package com.kotkot.usuario.noticiasrss;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by usuario on 15/02/18.
 */

public class HTTPHelper
{
    static String flujo = new String();

    public HTTPHelper(){}

    public String getFlujoHTTP(String direccion)
    {
        flujo = new String();
        try
        {
            URL url = new URL(direccion);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();

            if (connexion.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                InputStream iStream = new BufferedInputStream(connexion.getInputStream());
                BufferedReader bR = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder stringBuilder = new StringBuilder();
                String linea = new String();
                while ((linea = bR.readLine()) != null)
                    stringBuilder.append(linea);
                flujo = stringBuilder.toString();
                connexion.disconnect();
            }
        }
        catch(Exception e)
        {
            return "";
        }
        return flujo;
    }
}
