import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CopyFromSqlToFilesMaintainStructure {

	public static void main(String[] args) throws ClassNotFoundException,
	SQLException, IOException {

		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");
		
		int noRows = 10000;
		int iter = 0;
		long start = 0;
		long count = 0;
		while(true) {
			Statement st = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
			st.setFetchSize(1000);
			/*ResultSet rs = st
					.executeQuery("select Id, PostTypeId, ParentId, Body, Title, Tags, LastActivityDate from posts"
							+ " where DATE(LastActivityDate) between '2015-12-1' and '2016-1-31'");*/
			
			//test optimization
			start = iter * noRows;
			ResultSet rs = st
					.executeQuery("select Id, PostTypeId, ParentId, Body, Title, Tags, CreationDate from posts LIMIT "
							+ start + "," + noRows);
			if (!rs.next()) {
			    System.out.println("done with all the data");
			    st.close();
				rs.close();
			    break;
			}
			System.out.println(rs);
			//rs.setFetchSize(100);
			
			while (rs.next()) {
				System.out.println("Sequence No = " + count++ + " and id = "
						+ rs.getString("Id"));
				String lastActivity[] = rs.getString("CreationDate").split(
						"\\s+");
				String fullDate[] = lastActivity[0].split("-");
				String monthWithYear = fullDate[0] + "-" + fullDate[1];
				// question posts
				if (rs.getString("PostTypeId").equals("1")) {
					String dirNameQuestion = "G:\\Mallet\\DataWithFileStructure\\TextualData\\Questions\\"
							+ monthWithYear;
					File dirQuestion = new File(dirNameQuestion);
					if (!dirQuestion.exists()) {
						dirQuestion.mkdir();
					}
					try {
						FileWriter foutQuestion = new FileWriter(dirNameQuestion
								+ "\\" + rs.getString("Id") + ".txt", true);
						BufferedWriter broutQuestion = new BufferedWriter(
								foutQuestion);
						broutQuestion.write(rs.getString("Body"));
						broutQuestion.close();
					} catch (NullPointerException ex) {
						System.out.println("Body is null for this id");
					}

					// for tags
					String tagDir = "G:\\Mallet\\DataWithFileStructure\\Tags\\";
					String tags[] = rs.getString("Tags").split("<|><|>");
					for (int i = 1; i < tags.length; i++) {
						try {
							FileWriter foutTag = new FileWriter(tagDir + tags[i],
									true);
							BufferedWriter broutTag = new BufferedWriter(foutTag);
							broutTag.append(rs.getString("Id"));
							broutTag.newLine();
							broutTag.close();
						} catch (NullPointerException ex) {
							System.out.println("Tag is null for this id");
						}
					}
				}
				// answer posts
				else {
					String dirNameAnswer = "G:\\Mallet\\DataWithFileStructure\\TextualData\\Answers\\"
							+ monthWithYear;
					File dirAnswer = new File(dirNameAnswer);
					if (!dirAnswer.exists()) {
						dirAnswer.mkdir();
					}
					try {
						FileWriter foutAnswer = new FileWriter(dirNameAnswer
								+ "\\" + rs.getString("Id") + "_"
								+ rs.getString("ParentId") + ".txt", true);
						BufferedWriter broutAnswer = new BufferedWriter(foutAnswer);
						broutAnswer.write(rs.getString("Body"));
						broutAnswer.close();
					} catch (NullPointerException ex) {
						System.out.println("Body is null for this id");
					}
				}
			}
			st.close();
			rs.close();
			//con.close();
			iter++;
		}
		con.close();
	}
}
