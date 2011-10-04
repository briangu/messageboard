package org.ops5.messageboard;


import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONException;
import java.sql.*;
import org.json.JSONObject;


@Path("/mb/")
public class MessageBoardResource
{
  static {
    try
    {
      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");
      // create db tables if not present
      conn.close();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    catch (SQLException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private class JobList
  {
    public String sessionId;
    public String key;

    public List<Job> Jobs = new ArrayList<Job>();
  }

  private class Job
  {
    public String Id;
    public String Status;
    public Date LastAccessTime;
    public String Data;
    public String Result;
  }

  @GET
  @Produces("text/javascript")
  @Path("/jobs")
  public String getJobListAsJson(
      @QueryParam("session") String sessionId,
      @QueryParam("key") String key
      )
  {
    try
    {
      JobList jobList = getJobList(sessionId, key);

      JSONArray array = new JSONArray();

      for (Job job : jobList.Jobs)
      {
        JSONObject jsonJob = new JSONObject();
        jsonJob.put("id", job.Id);
        jsonJob.put("status", job.Status);
        jsonJob.put("lastAccessTime", job.LastAccessTime.getTime());
        jsonJob.put("data", job.Data);
        jsonJob.put("result", job.Result);

        array.put(jsonJob);
      }

      return array.toString(2);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }

    return "{success: false}";
  }

  @PUT
  @Produces("text/javascript")
  @Path("/jobs")
  public String putJobResult(
      @QueryParam("session") String sessionId,
      @QueryParam("key") String key,
      @FormParam("id") String id,
      @FormParam("result") String result
      )
  {
    try
    {
      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");

      conn.close();

      return "{\"success\": true}";
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return "{\"success\": false}";
  }

  @POST
  @Produces("text/javascript")
  @Path("/jobs")
  public String postJobList(
      @FormParam("session") String sessionId,
      @FormParam("key") String key,
      @FormParam("jobs") String jobsRaw)
  {
    try
    {
      JSONObject jobs = new JSONObject(jobsRaw);

      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");

      conn.close();

      return "{\"success\": true}";
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return "{\"success\": false}";
  }

  private JobList getJobList(String sessionId, String key)
  {
    JobList jobList = new JobList();

    jobList.sessionId = sessionId;
    jobList.key = key;

    try
    {
      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");

      //

      conn.close();

      return jobList;
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return jobList;
  }
}

