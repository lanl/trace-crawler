package gov.lanl.crawler.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.digitalpebble.stormcrawler.sql.Constants;

import gov.lanl.crawler.input.InputServer;

@Path("/delete")
public class DeleteResource {
	private Connection connection;
	private static Map<String, String> conf = InputServer.INSTANCE.prop;
	String reloaddir = (String) conf.get("reloaddir");

	@GET
	@Path("/hard/{id:.*}")
	// @Produces("application/json")
	public Response getNEvent(@javax.ws.rs.PathParam("id") String _id) {
		req_delete(_id);
		update_table("CANCEL", _id);
		try {
			// 5 sec to finish mysql transaction
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String filePath = "/data/web/tracer_demo/trace-crawler/"+"reloadcrawler.sh";
		String filePath = reloaddir + "reloadcrawler.sh";
		String[] cmd = { "sh", filePath };
		// ProcessBuilder probuilder = new
		// ProcessBuilder("/data/web/tracer_demo/trace-crawler/reloadcrawler.sh");
		ProcessBuilder probuilder = new ProcessBuilder(cmd);
		Process p;
		StringBuilder builder = new StringBuilder();
		try {

			// File OutputFile = new
			// File("/data/web/tracer_demo/trace-crawler/delete_log.txt");
			// probuilder.redirectErrorStream(true);
			// probuilder.directory(new File(warcdir));
			// probuilder.redirectOutput(OutputFile);
			p = probuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			p.waitFor(5, TimeUnit.SECONDS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = builder.toString();
		System.out.print(result);
		System.out.println("end of script execution");
		ResponseBuilder r = Response.ok("restart request submitted for " + _id);
		r.header("Content-Type", "text/html");
		return r.build();
	}

	@GET
	@Path("/{id:.*}")
	// @Produces("application/json")
	public Response getEvent(@javax.ws.rs.PathParam("id") String _id) {
		req_delete(_id);
		
		ResponseBuilder r = Response.ok("request submitted for " + _id);
		r.header("Content-Type", "text/html");
		return r.build();

	}

	public void prepare(Map stormConf) {// TopologyContext context,
		// tableName = (String) stormConf.get(Constants.MYSQL_TABLE_PARAM_NAME);
		// System.out.println("table:" + tableName);
		try {

			// SQL connection details
			String url = (String) stormConf.get(Constants.MYSQL_URL_PARAM_NAME);
			// "jdbc:mysql://localhost:3306/crawl");
			String user = (String) stormConf.get(Constants.MYSQL_USER_PARAM_NAME);
			String password = (String) stormConf.get(Constants.MYSQL_PASSWORD_PARAM_NAME);

			connection = DriverManager.getConnection(url, user, password);

		} catch (SQLException ex) {
			// LOG.error(ex.getMessage(), ex);
			ex.printStackTrace();
			// throw new RuntimeException(ex);
		}

	}

	public void update_table(String status, String ev) {
		// the mysql insert statement
		prepare(conf);
		System.out.println("update table");
		// String tableName="urls";
		// String query = tableName + " ( status)"
		// + " values (?, ?, ?, ?, ?, ?, md5(?), ?)";
		String sql = "Update urls set status=?   where event_id=?;";
		// System.out.println(query);

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(sql);
			preparedStmt.setString(1, status);

			preparedStmt.setString(2, ev);

			// long start = System.currentTimeMillis();
			System.out.println(preparedStmt);
			// execute the preparedstatement

			preparedStmt.execute();
			preparedStmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {

			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String check_delete(String ev) {
		prepare(conf);
		String sql = "Select status from input_jobs where event_id='" + ev + "'";
		SubmitResource sr = new SubmitResource();
		Statement st = null, st3 = null;
		ResultSet rs = null;
		String status = "D";
		try {
			st = this.connection.createStatement();
			rs = st.executeQuery(sql);

			// st3 = this.connection.createStatement();
			while (rs.next()) {

				status = rs.getString(1);

			}

		} catch (SQLException e) {
			System.out.println(e);
			// LOG.error("Exception while querying table", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing resultset", e);
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing statement", e);
			}

			try {
				if (st3 != null)
					st3.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing statement", e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}

	public void req_delete(String ev) {
		prepare(conf);
		String sql = "Select url,status from input_jobs where event_id='" + ev + "'";
		SubmitResource sr = new SubmitResource();
		Statement st = null, st3 = null;
		ResultSet rs = null;
		try {
			st = this.connection.createStatement();
			rs = st.executeQuery(sql);

			st3 = this.connection.createStatement();
			while (rs.next()) {
				String aurl = rs.getString(1);
				String status = rs.getString(2);

				String sql3 = "Update input_jobs set status='CANCEL'   where event_id='" + ev + "'";
				st3.execute(sql3);

			}

		} catch (SQLException e) {
			System.out.println(e);
			// LOG.error("Exception while querying table", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing resultset", e);
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing statement", e);
			}

			try {
				if (st3 != null)
					st3.close();
			} catch (SQLException e) {
				// LOG.error("Exception closing statement", e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
