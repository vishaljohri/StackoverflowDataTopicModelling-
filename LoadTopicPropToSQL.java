import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadTopicPropToSQL {
	int noTopics;

	public LoadTopicPropToSQL(int noTopics) {
		this.noTopics = noTopics;
	}

	String formQueryForTopics(String query) {
		for (int i = 1; i <= noTopics; i++) {
			query += "Topic" + i + " VARCHAR(35) NOT NULL, ";
		}
		query += "PRIMARY KEY (Document_Id))";
		return query;
	}

	String formCompleteInsertQuery(String s, String str[]) {
		for (int i = 2; i < str.length - 1; i++) {
			// System.out.println(str[i]);
			s += str[i] + ", ";
		}
		s += str[str.length - 1] + ")";
		return s;
	}

	void loadTopicPropSQL(String fileName) throws ClassNotFoundException,
			SQLException, IOException {

		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");
		Statement st = con.createStatement();

		// delete table if already exist
		st.executeUpdate("DROP TABLE IF EXISTS QuestionTopic");
		st.executeUpdate("DROP TABLE IF EXISTS AnswerTopic");

		// Create table for Question Topics
		String queryQuestion = "CREATE TABLE QuestionTopic("
				+ "Document_Id INT NOT NULL, "
				+ "CreationMonth VARCHAR(10) NOT NULL, ";
		String fullQueryQuestion = formQueryForTopics(queryQuestion);
		st.executeUpdate(fullQueryQuestion);

		// Create table for Answer Topics
		String queryAnswer = "CREATE TABLE AnswerTopic("
				+ "Document_Id INT NOT NULL, " + "Parent_Id INT, "
				+ "CreationMonth VARCHAR(10) NOT NULL, ";
		String fullQueryAnswer = formQueryForTopics(queryAnswer);
		st.executeUpdate(fullQueryAnswer);

		// process the topic-docs file and load result to db
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String thisLine = null;
		long count = 0;
		while ((thisLine = bufferedReader.readLine()) != null) {
			System.out.println("File no = " + ++count);
			String str[] = thisLine.split("\\s+");

			// check for answer or question post
			String fullName[] = str[1].split("/");
			String name = fullName[fullName.length - 1];
			name = name.substring(0, name.length() - 4);
			String s[] = name.split("_");
			// question post
			if (s.length == 2) {
				String insertQueryQuestionPrefix = "INSERT INTO QuestionTopic "
						+ "VALUES ( " + Integer.parseInt(s[0]) + ", " + "'"
						+ s[1] + "'" + ", ";
				String fullInsertQueryQuestion = formCompleteInsertQuery(
						insertQueryQuestionPrefix, str);
				st.executeUpdate(fullInsertQueryQuestion);
			}
			// Answer post
			else {
				String insertQueryAnswerPrefix = "INSERT INTO AnswerTopic "
						+ "VALUES ( " + Integer.parseInt(s[0]) + ", "
						+ Integer.parseInt(s[1]) + ", " + "'" + s[2] + "'"
						+ ", ";
				String fullInsertQueryAnswer = formCompleteInsertQuery(
						insertQueryAnswerPrefix, str);
				st.executeUpdate(fullInsertQueryAnswer);
			}
		}
		bufferedReader.close();
		st.close();
		con.close();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		LoadTopicPropToSQL lp = new LoadTopicPropToSQL(40);
		lp.loadTopicPropSQL("topic-docs-May-26");
	}
}
