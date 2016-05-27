import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.jdbc.PreparedStatement;

public class TechnologyTrends {
	int noTopics;
	HashMap<String, HashMap<String, Double>> hmTechImpactTags;
	HashMap<String, Double> hmTechImpactTopic;

	public TechnologyTrends(int noTopics) {
		this.noTopics = noTopics;
		hmTechImpactTags = new HashMap<>();
		hmTechImpactTopic = new HashMap<>();
	}

	void identifyTags(String dataPath) throws IOException,
			ClassNotFoundException, SQLException {
		String topicTags = dataPath + "\\" + "TopicTags";
		String tagPath = dataPath + "\\" + "Tags";
		Path dir = FileSystems.getDefault().getPath(tagPath);
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		long count = 1;

		// database connection to QuestionTopic table
		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");
		String selectQuestion = "SELECT * FROM questiontopic WHERE Document_Id = ?";
		PreparedStatement ps = (PreparedStatement) con
				.prepareStatement(selectQuestion);

		for (Path path : stream) {
			double cumTopic[] = new double[noTopics + 1];
			System.out.println("no = " + count++ + " file = "
					+ path.getFileName());

			// process the tag file
			File file = new File(tagPath + "\\" + path.getFileName());
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String thisLine = null;
			while ((thisLine = bufferedReader.readLine()) != null) {
				int docId = Integer.parseInt(thisLine);
				ps.setInt(1, docId);
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {
					System.out.println("question post not present for: "
							+ docId);
					rs.close();
					continue;
				}
				rs.beforeFirst();

				for (int i = 3; i <= noTopics + 2; i++) {
					Double topicValue = Double.parseDouble(rs.getString(i));
					if (topicValue >= 0.1)
						cumTopic[i - 2] += topicValue;
				}

			}
			bufferedReader.close();

			// check for which all topics tag can be added
			for (int i = 1; i <= noTopics; i++) {
				if (cumTopic[i] >= 0.1) {
					FileWriter foutTag = new FileWriter(topicTags + "\\" + i,
							true);
					BufferedWriter broutTag = new BufferedWriter(foutTag);
					broutTag.append(String.valueOf(path.getFileName()));
					broutTag.newLine();
					broutTag.close();
				}
			}
		}
		stream.close();
		ps.close();
		con.close();
	}

	void technologyTrendsHelper(String dataPath, int topicNumber,
			HashMap<String, ArrayList<String>> groupTags)
			throws ClassNotFoundException, SQLException, IOException {
		String tagPath = dataPath + "\\" + "Tags";

		// database connection to QuestionTopic table
		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");

		// build hmTechImpactTags
		String selectQuestionForTags = "SELECT CreationMonth, topic"
				+ topicNumber + " FROM questiontopic WHERE Document_Id = ?";
		PreparedStatement psTag = (PreparedStatement) con
				.prepareStatement(selectQuestionForTags);
		long seqNumber = 1;
		for (String tech : groupTags.keySet()) {
			System.out.println("Building hmTechImpactTags, sequence no = " + seqNumber++);
			HashMap<String, Double> hm = new HashMap<>();
			ArrayList<String> list = groupTags.get(tech);
			for (String tag : list) {
				File file = new File(tagPath + "\\" + tag);
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(file));
				String thisLine = null;
				while ((thisLine = bufferedReader.readLine()) != null) {
					int docId = Integer.parseInt(thisLine);
					psTag.setInt(1, docId);
					ResultSet rs = psTag.executeQuery();
					if (!rs.next()) {
						System.out.println("question post not present for: "
								+ docId);
						rs.close();
						continue;
					}
					rs.beforeFirst();

					if (!hm.containsKey(rs.getString(1))) {
						hm.put(rs.getString(1), 0.0);
					}
					hm.put(rs.getString(1),
							hm.get(rs.getString(1))
									+ Double.parseDouble(rs.getString(2)));
					rs.close();
				}
				bufferedReader.close();
			}
			hmTechImpactTags.put(tech, hm);
		}
		psTag.close();

		// build hmTechImpactTopic
		int noRows = 1000;
		long iter = 0;
		long start = 0;
		long count = 1;
		while (true) {
			Statement st = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			st.setFetchSize(1000);
			start = iter * noRows;
			ResultSet rs = st.executeQuery("SELECT CreationMonth, topic"
					+ topicNumber + " FROM questiontopic LIMIT " + start + ","
					+ noRows);
			if (!rs.next()) {
				System.out.println("done with all the data");
				st.close();
				rs.close();
				break;
			}
			rs.beforeFirst();

			while (rs.next()) {
				System.out.println("Building hmTechImpactTopic, Sequence No = "
						+ count++);
				if (!hmTechImpactTopic.containsKey(rs.getString(1))) {
					hmTechImpactTopic.put(rs.getString(1), 0.0);
				}
				hmTechImpactTopic.put(
						rs.getString(1),
						hmTechImpactTopic.get(rs.getString(1))
								+ Double.parseDouble(rs.getString(2)));
			}
			rs.close();
			st.close();
			iter++;
		}
		con.close();
	}

	void technologyTrends(String dataPath, int topicNumber,
			HashMap<String, ArrayList<String>> groupTags)
			throws ClassNotFoundException, SQLException, IOException {
		technologyTrendsHelper(dataPath, topicNumber, groupTags);

		// write technology trends to file
		FileWriter fwTechnology = new FileWriter("TechnologyTrends.txt", true);
		BufferedWriter bwTechnology = new BufferedWriter(fwTechnology);
		bwTechnology.append("Technology topic number is : " + String.valueOf(topicNumber));
		bwTechnology.newLine();
		bwTechnology.newLine();
		for (String str : hmTechImpactTags.keySet()) {
			bwTechnology.append("Technology name: " + str);
			bwTechnology.newLine();
			HashMap<String, Double> hm = hmTechImpactTags.get(str);
			for(String month : hm.keySet()) {
				bwTechnology.append(month + " " + String.valueOf((hm.get(month) / hmTechImpactTopic.get(month))));
				bwTechnology.newLine();
			}
			bwTechnology.newLine();
			bwTechnology.newLine();
		}
		bwTechnology.newLine();
		bwTechnology.newLine();
		bwTechnology.newLine();
		bwTechnology.newLine();
		bwTechnology.close();
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {
		TechnologyTrends technologyTrends = new TechnologyTrends(40);
		technologyTrends
				.identifyTags("G:\\Mallet\\Analysis_May25\\DataWithFileStructure");
	}
}
