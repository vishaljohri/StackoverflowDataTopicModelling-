import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TopicTrends {
	int noTopics;
	HashMap<String, ArrayList<Double>> hmAnswer;
	HashMap<String, ArrayList<Double>> hmQuestion;
	HashMap<String, Long> noDocsAnswerPerMonth;
	HashMap<String, Long> noDocsQuestionPerMonth;

	public TopicTrends(int noTopics) {
		this.noTopics = noTopics;
		hmAnswer = new HashMap<>();
		hmQuestion = new HashMap<>();
		noDocsAnswerPerMonth = new HashMap<>();
		noDocsQuestionPerMonth = new HashMap<>();
	}

	void calculateTrendsAnswerPosts() throws ClassNotFoundException,
			SQLException, IOException {
		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		int noRows = 10000;
		long iter = 0;
		long start = 0;
		long count = 1;
		while (true) {
			Statement st = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			st.setFetchSize(1000);
			start = iter * noRows;
			ResultSet rs = st.executeQuery("select * from answertopic LIMIT "
					+ start + "," + noRows);
			if (!rs.next()) {
				System.out.println("done with all the answer posts");
				st.close();
				rs.close();
				break;
			}
			rs.beforeFirst();

			while (rs.next()) {
				System.out.println("Answer Sequence No = " + count++
						+ " and id = " + rs.getInt("Document_Id"));
				String creationMonth = rs.getString("CreationMonth");
				if (!hmAnswer.containsKey(creationMonth)) {
					ArrayList<Double> list = new ArrayList<>();
					for (int i = 1; i <= noTopics; i++) {
						list.add(0.0);
					}
					hmAnswer.put(creationMonth, list);
				}
				if (!noDocsAnswerPerMonth.containsKey(creationMonth)) {
					noDocsAnswerPerMonth.put(creationMonth, (long) 0);
				}
				noDocsAnswerPerMonth.put(creationMonth,
						noDocsAnswerPerMonth.get(creationMonth) + 1);

				// add values of topics for the encountered month
				ArrayList<Double> listTopic = hmAnswer.get(creationMonth);
				for (int i = 4; i <= noTopics + 3; i++) {
					String topicValue = rs.getString(i);
					listTopic.set(
							i - 4,
							listTopic.get(i - 4)
									+ Double.parseDouble(topicValue));
				}
				hmAnswer.put(creationMonth, listTopic);

			}
			st.close();
			rs.close();
			iter++;
		}
		con.close();
	}

	void calculateTrendsQuestionPosts() throws ClassNotFoundException,
			SQLException, IOException {
		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		int noRows = 10000;
		long iter = 0;
		long start = 0;
		long count = 1;
		while (true) {
			Statement st = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			st.setFetchSize(1000);
			start = iter * noRows;
			ResultSet rs = st.executeQuery("select * from questiontopic LIMIT "
					+ start + "," + noRows);
			if (!rs.next()) {
				System.out.println("done with all the question posts");
				st.close();
				rs.close();
				break;
			}
			rs.beforeFirst();

			while (rs.next()) {
				System.out.println("Question Sequence No = " + count++
						+ " and id = " + rs.getInt("Document_Id"));
				String creationMonth = rs.getString("CreationMonth");
				if (!hmQuestion.containsKey(creationMonth)) {
					ArrayList<Double> list = new ArrayList<>();
					for (int i = 1; i <= noTopics; i++) {
						list.add(0.0);
					}
					hmQuestion.put(creationMonth, list);
				}
				if (!noDocsQuestionPerMonth.containsKey(creationMonth)) {
					noDocsQuestionPerMonth.put(creationMonth, (long) 0);
				}
				noDocsQuestionPerMonth.put(creationMonth,
						noDocsQuestionPerMonth.get(creationMonth) + 1);

				// add values of topics for the encountered month
				ArrayList<Double> listTopic = hmQuestion.get(creationMonth);
				for (int i = 3; i <= noTopics + 2; i++) {
					String topicValue = rs.getString(i);
					listTopic.set(
							i - 3,
							listTopic.get(i - 3)
									+ Double.parseDouble(topicValue));
				}
				hmQuestion.put(creationMonth, listTopic);

			}
			st.close();
			rs.close();
			iter++;
		}
		con.close();
	}

	void writeResultofTends() throws IOException {
		// answer trends
		FileWriter fwAnswer = new FileWriter("AnswerTopicTrends.txt");
		BufferedWriter bwAnswer = new BufferedWriter(fwAnswer);
		// logic to get year-month in ascending order
		ArrayList<String> alAnswer = new ArrayList<>(hmAnswer.keySet());
		Collections.sort(alAnswer);
		System.out.println("after sorting year and month of answer: "
				+ alAnswer);
		for (String str : alAnswer) {
			bwAnswer.write("YearMonth: " + str);
			bwAnswer.newLine();
			ArrayList<Double> list = hmAnswer.get(str);
			for (Double val : list) {
				double relvalue = val / noDocsAnswerPerMonth.get(str);
				bwAnswer.write(String.valueOf(relvalue) + "  ");
			}
			bwAnswer.newLine();
			bwAnswer.newLine();
		}
		bwAnswer.close();

		// question trends
		FileWriter fwQuestion = new FileWriter("QuestionTopicTrends.txt");
		BufferedWriter bwQuestion = new BufferedWriter(fwQuestion);
		// logic to get year-month in ascending order
		ArrayList<String> alQuestion = new ArrayList<>(hmQuestion.keySet());
		Collections.sort(alQuestion);
		System.out.println("after sorting year and month of answer: "
				+ alQuestion);
		for (String str : alQuestion) {
			bwQuestion.write("YearMonth: " + str);
			bwQuestion.newLine();
			ArrayList<Double> list = hmQuestion.get(str);
			for (Double val : list) {
				double relvalue = val / noDocsQuestionPerMonth.get(str);
				bwQuestion.write(String.valueOf(relvalue) + "  ");
			}
			bwQuestion.newLine();
			bwQuestion.newLine();
		}
		bwQuestion.close();

		// combined trends
		FileWriter fwCombined = new FileWriter("CombinedTopicTrends.txt");
		BufferedWriter bwCombined = new BufferedWriter(fwCombined);
		for (String str : alQuestion) {
			bwCombined.write("YearMonth: " + str);
			bwCombined.newLine();
			ArrayList<Double> listQuestion = hmQuestion.get(str);
			ArrayList<Double> listAnswer = hmAnswer.get(str);
			for (int i = 0; i < listQuestion.size(); i++) {
				double relvalue = (listQuestion.get(i) + listAnswer.get(i))
						/ (noDocsQuestionPerMonth.get(str) + noDocsAnswerPerMonth
								.get(str));
				bwCombined.write(String.valueOf(relvalue) + "  ");
			}
			bwCombined.newLine();
			bwCombined.newLine();
		}
		bwCombined.close();

	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		TopicTrends topicTrends = new TopicTrends(40);
		topicTrends.calculateTrendsAnswerPosts();
		topicTrends.calculateTrendsQuestionPosts();
		topicTrends.writeResultofTends();
	}
}