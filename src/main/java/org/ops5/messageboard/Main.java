package org.ops5.messageboard;


import io.viper.net.server.Util;
import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONException;


public class Main
{
  public static void main(String[] args)
  {

    try
    {
      String staticFileRoot = String.format("%s/src/main/resources/public", Util.getCurrentWorkingDirectory());

      Server server = Server.create("bguarrac-ld.linkedin.biz", 3000, staticFileRoot);
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
