import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CopyPostsFromSQLToText {

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException, IOException {

		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		Statement st = con.createStatement();
		ResultSet rs = st
				.executeQuery("select Id, PostTypeId, AcceptedAnswerId, ParentId, Body, Title, Tags, CreationDate from posts"
						+ " where DATE(CreationDate) between '2015-12-1' and '2016-1-31'");
		System.out.println(rs);
		while (rs.next()) {
			FileWriter fout = new FileWriter("src\\output_"
					+ rs.getString("id") + ".txt", true);
			BufferedWriter brout = new BufferedWriter(fout);
			brout.append("Title = " + rs.getString("Title"));
			brout.newLine();
			brout.append("Body = " + rs.getString("Body"));
			brout.newLine();
			brout.append("Id = " + rs.getString("Id"));
			brout.newLine();
			brout.append("PostTypeId = " + rs.getString("PostTypeId"));
			brout.newLine();
			brout.append("AcceptedAnswerId = "
					+ rs.getString("AcceptedAnswerId"));
			brout.newLine();
			brout.append("ParentId = " + rs.getString("ParentId"));
			brout.newLine();
			brout.append("Tags = " + rs.getString("Tags"));
			brout.newLine();
			brout.append("CreationDate = " + rs.getString("CreationDate"));
			brout.newLine();
			brout.close();
		}

		st.close();
		con.close();

	}

}
