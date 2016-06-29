import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadTopicPropToSQLOptimized {
	int noTopics;

	LoadTopicPropToSQLOptimized(int noTopics) {
		this.noTopics = noTopics;
	}

	void createSeparateFilesToLoad(String fileName) throws IOException {

		// create separate files for answers and questions
		// answer posts
		FileWriter fwAnswer = new FileWriter("Answer_Topic_Prob_Document_AnalysisFinal.txt");
		BufferedWriter bwAnswer = new BufferedWriter(fwAnswer);
		// question posts
		FileWriter fwQuestion = new FileWriter(
				"Question_Topic_Prob_Document_AnalysisFinal.txt");
		BufferedWriter bwQuestion = new BufferedWriter(fwQuestion);

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
			// Question post
			if (s.length == 2) {
				bwQuestion.newLine();
				bwQuestion.write(s[0] + ", " + s[1] + ", ");
				for (int i = 2; i < str.length - 1; i++) {
					bwQuestion.write(str[i] + ", ");
				}
				bwQuestion.write(str[str.length - 1]);
			}
			// Answer post
			else {
				bwAnswer.newLine();
				bwAnswer.write(s[0] + ", " + s[1] + ", " + s[2] + ", ");
				for (int i = 2; i < str.length - 1; i++) {
					bwAnswer.write(str[i] + ", ");
				}
				bwAnswer.write(str[str.length - 1]);
			}
		}
		bwQuestion.close();
		bwAnswer.close();
		bufferedReader.close();
	}

	String formQueryForTopics(String query) {
		for (int i = 1; i <= noTopics; i++) {
			query += "Topic" + i + " VARCHAR(35) NOT NULL, ";
		}
		query += "PRIMARY KEY (Document_Id))";
		return query;
	}

	void createSchemaOfTables() throws ClassNotFoundException, SQLException {
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

		st.close();
		con.close();
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {
		LoadTopicPropToSQLOptimized lt = new LoadTopicPropToSQLOptimized(40);
		lt.createSeparateFilesToLoad("G:\\Mallet\\Analysis_Final\\topic-docs-analysis-final");
		lt.createSchemaOfTables();
	}
}
