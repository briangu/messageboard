package org.ops5.messageboard;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONException;


@Path("/mb/")
public class MessageBoardResource
{
  @GET
  @Produces("text/javascript")
  @Path("/jobs")
  public String getJobList(@QueryParam("id") String id)
  {
    try
    {
      return new JSONArray().toString(2);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return "{success: false}";
  }
}

