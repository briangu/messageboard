package org.ops5.messageboard;


import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.DELETE;
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
      ResultSet resultSet = conn.getMetaData().getTables(conn.getCatalog(), null, null, null);

      conn.close();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private class JobList
  {
    public String sessionId;

    public List<Job> Jobs = new ArrayList<Job>();
  }

  private class Job
  {
    public String SessionId;
    public String JobId;
    public String Status;
    public Date LastAccessTime;
    public String Data;
    public String Result;
  }

  @GET
  @Produces("text/javascript")
  @Path("/jobs/next")
  public String getNextJobAsJson(@QueryParam("session") String sessionId)
  {
    try
    {
      Job job = getNextJob(sessionId);

      if (job == null)
      {
        return "[]";
      }

      JSONObject jsonJob = new JSONObject();
      jsonJob.put("id", job.JobId);
      jsonJob.put("status", job.Status);
      jsonJob.put("lastAccessTime", job.LastAccessTime.getTime());
      jsonJob.put("data", job.Data);
      jsonJob.put("result", job.Result);

      return jsonJob.toString(2);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }

    return "[]";
  }

  @GET
  @Produces("text/javascript")
  @Path("/jobs")
  public String getJobListAsJson(@QueryParam("session") String sessionId)
  {
    try
    {
      List<Job> jobList = getJobList(sessionId);

      JSONArray array = new JSONArray();

      for (Job job : jobList)
      {
        JSONObject jsonJob = new JSONObject();
        jsonJob.put("id", job.JobId);
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

    return "[]";
  }

  @PUT
  @Produces("text/javascript")
  @Path("/jobs")
  public String putJobResult(
      @QueryParam("session") String sessionId,
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
      @FormParam("jobs") String jobsRaw)
  {
    try
    {
      JSONArray jobList = new JSONArray(jobsRaw);

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

  @GET
  @Produces("text/javascript")
  @Path("/sessions")
  public String getSessionList()
  {
    try
    {
      JSONArray sessionList = new JSONArray();

      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");

      conn.close();

      return sessionList.toString(2);
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

    return "[]";
  }

  @DELETE
  @Produces("text/javascript")
  @Path("/sessions")
  public String deleteJobList(@FormParam("session") String sessionId)
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

  private Job getNextJob(String sessionId)
  {
    try
    {
      Job job = new Job();

      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./test", "sa", "");

      //

      conn.close();

      return job;
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return null;
  }

  private List<Job> getJobList(String sessionId)
  {
    List<Job> jobList = new ArrayList<Job>();

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

