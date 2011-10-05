package org.ops5.messageboard;


import java.util.*;
import java.util.Date;
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
  private static final String _connectionString = "jdbc:h2:./sessions";

  static {
    Connection conn = null;
    Statement statement = null;

    try
    {
      conn = getConnection();
      ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });

      boolean haveJobsTable = false;

      while (rs.next())
      {
        String tableName = rs.getString("TABLE_NAME");
        if (tableName.equals("JOBS"))
        {
          haveJobsTable = true;
          break;
        }
      }

      if (!haveJobsTable)
      {
        statement = conn.createStatement();
        statement.execute(
            "create table jobs (" +
                "id INTEGER not NULL," +
                "session_id VARCHAR(255)," +
                "session_sub_key VARCHAR(255)," +
                "last_access_time BIGINT," +
                "data CLOB," +
                "result CLOB," +
                "status VARCHAR(255)" +
            ")");
      }
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  private static class Job
  {
    public String SessionId;
    public String SessionSubKey;
    public Integer JobId;
    public String Status;
    public Long LastAccessTime;
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
      jsonJob.put("LastAccessTime", LastAccessTime);
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

    public static Job createFrom(JSONObject jsonObject)
        throws JSONException
    {
      Job job = new Job();

      job.SessionId = jsonObject.getString("SessionId");
      job.SessionSubKey = jsonObject.getString("SessionSubKey");
      job.JobId = jsonObject.has("JobId") ? jsonObject.getInt("JobId") : -1;
      job.Status = jsonObject.getString("Status");
      job.LastAccessTime = jsonObject.has("LastAccessTime") ? jsonObject.getLong("LastAccessTime") : new Date().getTime();
      job.Data = jsonObject.getString("Data");
      job.Result = jsonObject.has("Result") ? jsonObject.getString("Result") : null;

      return job;
    }
  }

  private static Connection getConnection()
      throws ClassNotFoundException, SQLException
  {
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection(_connectionString, "sa", "");
    return conn;
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
    Connection conn = null;
    PreparedStatement statement = null;

    try
    {
      conn = getConnection();

      statement = conn.prepareStatement("update jobs set result = ?, last_access_time = ? where session_id = ? and session_sub_key = ? and id = ?");
      statement.setString(0, result);
      statement.setLong(1, new Date().getTime());
      statement.setString(2, sessionId);
      statement.setString(3, sessionSubKey);
      statement.setString(4, jobId);
      statement.execute();

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }

    return "{\"success\": false}";
  }

  @POST
  @Produces("text/javascript")
  @Path("/jobs")
  public String postJobList(@FormParam("JobList") String jobsRaw)
  {
    Connection conn = null;
    PreparedStatement statement = null;

    try
    {
      conn = getConnection();
      statement = conn.prepareStatement("insert into jobs (session_id, session_sub_key, status, last_access_time, data, result) values (?, ?, ?, ?, ?, ?)");

      JSONArray jobList = new JSONArray(jobsRaw);

      for (int i = 0; i < jobList.length(); i++)
      {
        Job job = Job.createFrom(jobList.getJSONObject(i));

        statement.setString(0, job.SessionId);
        statement.setString(1, job.SessionSubKey);
        statement.setString(2, job.Status);
        statement.setLong(3, job.LastAccessTime);
        statement.setString(4, job.Data);
        statement.setString(5, job.Result);
        statement.addBatch();
      }

      statement.executeBatch();

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }

    return "{\"success\": false}";
  }

  @GET
  @Produces("text/javascript")
  @Path("/sessions")
  public String getSessionList()
  {
    Connection conn = null;
    Statement statement = null;

    try
    {
      JSONArray sessionList = new JSONArray();

      conn = getConnection();

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
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
    Connection conn = null;
    Statement statement = null;

    try
    {
      conn = getConnection();

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
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
    Connection conn = null;
    Statement statement = null;

    try
    {
      conn = getConnection();

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }

    return "{\"success\": false}";
  }

  private Job getNextJob(String sessionId)
  {
    Connection conn = null;
    Statement statement = null;

    try
    {
      conn = getConnection();

      conn.prepareStatement("update jobs set last_access_time = ?, status = 'working' output INSERTED.id where session_id = %s and result = null limit 1");

      statement = conn.createStatement();

      ResultSet resultSet = statement.executeQuery(
          String.format(
              "update jobs set last_access_time = %l, status = 'working' output INSERTED.id where session_id = %s and result = null limit 1",
              sessionId));

      Job job = null;

      while (resultSet.next())
      {
        job = new Job();

        job.SessionId = resultSet.getString("session_id");
        job.SessionSubKey = resultSet.getString("session_sub_key");
        job.JobId = resultSet.getInt("id");
        job.LastAccessTime = resultSet.getLong("last_access_time");
        job.Data = resultSet.getString("data");
        job.Result = resultSet.getString("result");
        job.Status = resultSet.getString("status");
      }

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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  private List<Job> getJobList(String sessionId)
  {
    Connection conn = null;
    Statement statement = null;

    List<Job> jobList = new ArrayList<Job>();

    try
    {
      conn = getConnection();


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
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      if (conn != null)
      {
        try
        {
          conn.close();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
    }

    return jobList;
  }
}

