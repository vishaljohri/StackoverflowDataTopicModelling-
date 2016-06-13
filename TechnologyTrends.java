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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
				// rs.beforeFirst();

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
					broutTag.append(String.valueOf(path.getFileName()) + " "
							+ cumTopic[i]);
					broutTag.newLine();
					broutTag.close();
				}
			}
		}
		stream.close();
		ps.close();
		con.close();
	}

	void sortTagsFrequencyPerTopic(String dataPath) throws IOException {
		String topicPath = dataPath + "\\" + "TopicTags";
		String sortedTagsPath = dataPath + "\\" + "SortedTags";
		for (int i = 1; i <= noTopics; i++) {
			System.out.println("Starting with topic number " + i);
			File file = new File(topicPath + "\\" + i);
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<String> list = new ArrayList<>();
			String thisLine = null;
			while ((thisLine = br.readLine()) != null) {
				list.add(thisLine.trim());
			}
			br.close();
			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					String first[] = o1.split("\\s+");
					String second[] = o2.split("\\s+");
					if (Double.parseDouble(second[1]) > Double
							.parseDouble(first[1]))
						return 1;
					else
						return -1;
				}

			});

			FileWriter fw = new FileWriter(sortedTagsPath + "\\" + i);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String str : list) {
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}
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
			System.out.println("Building hmTechImpactTags, "
					+ " sequence no = " + seqNumber++ + " tech name = " + tech);
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
					// rs.beforeFirst();

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
		FileWriter fwTechnology = new FileWriter("TechnologyTrends"
				+ topicNumber + ".txt");
		BufferedWriter bwTechnology = new BufferedWriter(fwTechnology);
		/*
		 * bwTechnology.append("Technology topic number is : " +
		 * String.valueOf(topicNumber)); bwTechnology.newLine();
		 * bwTechnology.newLine();
		 */
		for (String str : hmTechImpactTags.keySet()) {
			// bwTechnology.append("Technology name: " + str);
			// bwTechnology.newLine();
			HashMap<String, Double> hm = hmTechImpactTags.get(str);

			// logic to get year-month in ascending order
			ArrayList<String> al = new ArrayList<>(hm.keySet());
			Collections.sort(al);
			System.out.println("after sorting year and month: " + al);

			for (String month : al) {
				bwTechnology.write(month
						+ " "
						+ String.valueOf((hm.get(month) / hmTechImpactTopic
								.get(month))) + " " + str);
				bwTechnology.newLine();
			}
			// bwTechnology.newLine();
			// bwTechnology.newLine();
		}
		// bwTechnology.newLine();
		// bwTechnology.newLine();
		// bwTechnology.newLine();
		// bwTechnology.newLine();
		bwTechnology.close();
	}

	void groupTagsCreateTrends(String dataPath) throws IOException,
			ClassNotFoundException, SQLException {

		// give topic number for trends
		int topic = 8;
		// create map for tech name and corresponding keywords
		HashMap<String, ArrayList<String>> hm = new HashMap<>();

		/*
		 * hm.put("javascript", new ArrayList<>(Arrays.asList("javascript")));
		 * hm.put("python", new ArrayList<>(Arrays.asList("python")));
		 * hm.put("php", new ArrayList<>(Arrays.asList("php"))); hm.put("perl",
		 * new ArrayList<>(Arrays.asList("perl"))); hm.put("ruby", new
		 * ArrayList<>(Arrays.asList("ruby"))); hm.put("vbscript", new
		 * ArrayList<>(Arrays.asList("vbscript")));
		 */

		hm.put("java", new ArrayList<>(Arrays.asList("java")));
		hm.put("c++", new ArrayList<>(Arrays.asList("c++")));
		hm.put("c#", new ArrayList<>(Arrays.asList("c#")));
		hm.put("php", new ArrayList<>(Arrays.asList("php")));
		hm.put("python", new ArrayList<>(Arrays.asList("python")));
		hm.put("javascript", new ArrayList<>(Arrays.asList("javascript")));

		/*
		 * hm.put("svn", new ArrayList<>(Arrays.asList("svn"))); hm.put("git",
		 * new ArrayList<>(Arrays.asList("git"))); hm.put("clearcase", new
		 * ArrayList<>(Arrays.asList("clearcase"))); hm.put("perforce", new
		 * ArrayList<>(Arrays.asList("perforce")));
		 */

		/*
		 * hm.put("android", new ArrayList<>(Arrays.asList("android")));
		 * hm.put("iphone", new ArrayList<>(Arrays.asList("iphone")));
		 * hm.put("blackberry", new ArrayList<>(Arrays.asList("blackberry")));
		 */

		/*
		 * hm.put("javascript", new ArrayList<>(Arrays.asList("javascript")));
		 * hm.put("jquery", new ArrayList<>(Arrays.asList("jquery")));
		 * hm.put("php", new ArrayList<>(Arrays.asList("php")));
		 * hm.put("asp.net", new ArrayList<>(Arrays.asList("asp.net")));
		 */

		/*
		 * hm.put("mysql", new ArrayList<>(Arrays.asList("mysql")));
		 * hm.put("postgresql", new ArrayList<>(Arrays.asList("postgresql")));
		 * hm.put("oracle", new ArrayList<>(Arrays.asList("oracle")));
		 * hm.put("sqlite", new ArrayList<>(Arrays.asList("sqlite")));
		 */

		// search for the keywords of tech in the given topic
		String topicTags = dataPath + "\\" + "TopicTags" + "\\" + topic;
		HashMap<String, ArrayList<String>> groupTags = new HashMap<>();
		groupTags.put("java", new ArrayList<String>());
		groupTags.put("c++", new ArrayList<String>());
		groupTags.put("c#", new ArrayList<String>());
		groupTags.put("php", new ArrayList<String>());
		groupTags.put("python", new ArrayList<String>());
		groupTags.put("javascript", new ArrayList<String>());

		File file = new File(topicTags);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String thisLine = null;
		while ((thisLine = bufferedReader.readLine()) != null) {
			for (String tech : hm.keySet()) {
				ArrayList<String> keywords = hm.get(tech);

				// special matching for java to avoid javascript
				if (tech.equals("java")) {
					for (String k : keywords) {
						if (!thisLine.toLowerCase().contains("javascript")
								&& thisLine.toLowerCase().contains(
										k.toLowerCase())) {
							groupTags.get(tech).add(thisLine.split("\\s+")[0]);
						}
					}
				} else {
					for (String k : keywords) {
						if (thisLine.toLowerCase().contains(k.toLowerCase())) {
							groupTags.get(tech).add(thisLine.split("\\s+")[0]);
						}
					}
				}
			}
		}
		bufferedReader.close();

		// print group of tags for technology
		for (String tech : groupTags.keySet()) {
			System.out.println("Tech : " + tech);
			System.out.println("tags : " + groupTags.get(tech));
		}

		// call technologyTrends with datapath, topic number and group of tags
		technologyTrends(dataPath, topic, groupTags);
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {
		TechnologyTrends technologyTrends = new TechnologyTrends(40);
		// technologyTrends
		// .identifyTags("G:\\Mallet\\Analysis_May25\\DataWithFileStructure");
		// technologyTrends
		// .sortTagsFrequencyPerTopic("G:\\Mallet\\Analysis_May25\\DataWithFileStructure");
		technologyTrends
				.groupTagsCreateTrends("G:\\Mallet\\Analysis_May25\\DataWithFileStructure");
	}
}
