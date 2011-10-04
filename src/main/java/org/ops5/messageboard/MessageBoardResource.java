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

  private class Job
  {
    public String SessionId;
    public String SessionSubKey;
    public String JobId;
    public String Status;
    public Date LastAccessTime;
    public String Data;
    public String Result;

    public JSONObject toJSON()
        throws JSONException
    {
      JSONObject jsonJob = new JSONObject();
      jsonJob.put("SessionId", SessionId);
      jsonJob.put("SessionSubKey", SessionSubKey);
      jsonJob.put("JobId", JobId);
      jsonJob.put("Status", Status);
      jsonJob.put("LastAccessTime", LastAccessTime.getTime());
      jsonJob.put("Data", Data);
      jsonJob.put("Result", Result);
      return jsonJob;
    }

    public String toString()
    {
      try
      {
        return toJSON().toString(2);
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }

      return "{ failed to parse }";
    }
  }

  @GET
  @Produces("text/javascript")
  @Path("/jobs/next")
  public String getNextJobAsJson(@QueryParam("SessionId") String sessionId)
  {
    Job job = getNextJob(sessionId);
    return job == null ? "[]" : job.toString();
  }

  @GET
  @Produces("text/javascript")
  @Path("/jobs")
  public String getJobListAsJson(@QueryParam("SessionId") String sessionId)
  {
    try
    {
      List<Job> jobList = getJobList(sessionId);

      JSONArray array = new JSONArray();

      for (Job job : jobList)
      {
        array.put(job.toJSON());
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
      @FormParam("SessionId") String sessionId,
      @FormParam("SessionSubKey") String sessionSubKey,
      @FormParam("JobId") String jobId,
      @FormParam("Result") String result
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
      @FormParam("SessionId") String sessionId,
      @FormParam("JobList") String jobsRaw)
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
  public String deleteJobList(
      @QueryParam("SessionId") String sessionId
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

  @DELETE
  @Produces("text/javascript")
  @Path("/jobs")
  public String deleteJobList(
      @QueryParam("SessionId") String sessionId,
      @QueryParam("SessionSubKey") String sessionSubKey
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

