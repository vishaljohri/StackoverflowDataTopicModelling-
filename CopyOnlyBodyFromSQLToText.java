import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CopyOnlyBodyFromSQLToText {

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException, IOException {

		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		Statement st = con.createStatement();
		ResultSet rs = st
				.executeQuery("select Id, Body from posts"
						+ " where DATE(CreationDate) between '2015-12-1' and '2016-1-31'");
		System.out.println(rs);
		int count = 0;
		while (rs.next()) {
			System.out.println("Sequence No = " + count++ + " and id = "
					+ rs.getString("Id"));
			try {
				FileWriter fout = new FileWriter("src\\output_"
						+ rs.getString("Id") + ".txt", true);

				BufferedWriter brout = new BufferedWriter(fout);
				brout.write(rs.getString("Body"));
				brout.close();
			} catch (NullPointerException ex) {
				System.out.println("Body is null for this is");
			}
		}

		st.close();
		con.close();

	}

}
