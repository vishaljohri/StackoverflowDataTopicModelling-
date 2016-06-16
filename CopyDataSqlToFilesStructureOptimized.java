import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WriteData implements Runnable {
	long postNo;
	String id;
	String creationDate;
	String postTypeId;
	String body;
	String tags;
	String parentId;

	public WriteData(long postNo, String id, String creationDate,
			String postTypeId, String body, String tags, String parentId) {
		this.postNo = postNo;
		this.id = id;
		this.creationDate = creationDate;
		this.postTypeId = postTypeId;
		this.body = body;
		this.tags = tags;
		this.parentId = parentId;
	}

	@Override
	public void run() {

		try {
			/*
			 * System.out.println("Sequence No = " + postNo + " and id = " +
			 * this.id + " executed by " + Thread.currentThread().getName());
			 */
			String creationDateFull[] = this.creationDate.split("\\s+");
			String fullDate[] = creationDateFull[0].split("-");
			String monthWithYear = fullDate[0] + "-" + fullDate[1];
			// question posts
			if (this.postTypeId.equals("1")) {
				String dirNameQuestion = "G:\\Mallet\\Analysis_Optimized\\DataWithFileStructure\\TextualData\\Questions\\"
						+ monthWithYear;
				File dirQuestion = new File(dirNameQuestion);
				if (!dirQuestion.exists()) {
					dirQuestion.mkdir();
				}
				try {
					FileWriter foutQuestion = new FileWriter(dirNameQuestion
							+ "\\" + this.id + "_" + monthWithYear + ".txt");
					BufferedWriter broutQuestion = new BufferedWriter(
							foutQuestion);
					broutQuestion.write(this.body);
					broutQuestion.close();
				} catch (NullPointerException ex) {
					System.out.println("Body is null for this id : " + this.id);
				}

				// for tags
				String tagDir = "G:\\Mallet\\Analysis_Optimized\\DataWithFileStructure\\Tags\\";
				String tagsList[] = this.tags.split("<|><|>");
				for (int i = 1; i < tagsList.length; i++) {
					try {
						FileWriter foutTag = new FileWriter(tagDir
								+ tagsList[i], true);
						BufferedWriter broutTag = new BufferedWriter(foutTag);
						broutTag.append(this.id);
						broutTag.newLine();
						broutTag.close();
					} catch (NullPointerException ex) {
						System.out.println("Tag is null for this id : " + this.id);
					}
				}
			}
			// answer posts
			else {
				String dirNameAnswer = "G:\\Mallet\\Analysis_Optimized\\DataWithFileStructure\\TextualData\\Answers\\"
						+ monthWithYear;
				File dirAnswer = new File(dirNameAnswer);
				if (!dirAnswer.exists()) {
					dirAnswer.mkdir();
				}
				try {
					FileWriter foutAnswer = new FileWriter(dirNameAnswer + "\\"
							+ this.id + "_" + this.parentId + "_"
							+ monthWithYear + ".txt");
					BufferedWriter broutAnswer = new BufferedWriter(foutAnswer);
					broutAnswer.write(this.body);
					broutAnswer.close();
				} catch (NullPointerException ex) {
					System.out.println("Body is null for this id : " + this.id);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

public class CopyDataSqlToFilesStructureOptimized {

	void loadData() throws ClassNotFoundException, SQLException, IOException,
			InterruptedException {

		String url = "jdbc:mysql://localhost:3306/stackoverflow?useSSL=false";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		int noRows = 50000;
		long start = 0;
		long count = 0;
		long end = -1;
		while (true) {
			long startTime = System.currentTimeMillis();
			ExecutorService es = Executors.newFixedThreadPool(1000);
			Statement st = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			st.setFetchSize(5000);

			start = end + 1;
			end = start + noRows - 1;

			ResultSet rs = st
					.executeQuery("select Id, PostTypeId, ParentId, Body, Title, Tags, CreationDate from posts WHERE Id BETWEEN "
							+ start + " AND " + end);

			if (!rs.next()) {
				System.out.println("done with all the data");
				st.close();
				rs.close();
				break;
			}
			rs.beforeFirst();

			while (rs.next()) {
				count++;
				WriteData writeData = new WriteData(count, rs.getString("Id"),
						rs.getString("CreationDate"),
						rs.getString("PostTypeId"), rs.getString("Body"),
						rs.getString("Tags"), rs.getString("ParentId"));
				es.execute(writeData);
			}
			es.shutdown();
			while (!es.isTerminated()) {
			}

			st.close();
			rs.close();
			System.out.println("Document Ids and no completed : " + end + ", "
					+ count + " Time taken : "
					+ (System.currentTimeMillis() - startTime));
		}

		System.out.println("Finished all threads");
		con.close();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException, InterruptedException {
		CopyDataSqlToFilesStructureOptimized c = new CopyDataSqlToFilesStructureOptimized();
		c.loadData();
	}
}
