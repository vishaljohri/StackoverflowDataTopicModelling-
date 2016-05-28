import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import com.mysql.jdbc.PreparedStatement;

public class TopicRelationship {
	int noTopics;
	HashMap<Integer, ArrayList<Double>> hm;

	public TopicRelationship(int noTopics) {
		this.noTopics = noTopics;
		hm = new HashMap<>();
	}

	void calculateRelationship() throws ClassNotFoundException, SQLException,
			IOException {
		String url = "jdbc:mysql://localhost:3306/stackoverflow";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, "root", "root");
		String selectQuestion = "SELECT * FROM questiontopic WHERE Document_Id = ?";
		PreparedStatement ps = (PreparedStatement) con
				.prepareStatement(selectQuestion);

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
			ResultSet rs = st.executeQuery("select * from answertopic LIMIT "
					+ start + "," + noRows);
			if (!rs.next()) {
				System.out.println("done with all the data");
				st.close();
				rs.close();
				break;
			}
			rs.beforeFirst();

			while (rs.next()) {
				System.out.println("Sequence No = " + count++ + " and id = "
						+ rs.getInt("Document_Id"));

				int parent = rs.getInt("Parent_Id");
				ps.setInt(1, parent);
				ResultSet rq = ps.executeQuery();
				if (!rq.next()) {
					System.out.println("question post not present for: "
							+ rs.getInt("Document_Id"));
					rq.close();
					continue;
				}
				//rq.beforeFirst();

				int topicNumber = 0;
				for (int qc = 3; qc <= noTopics + 2; qc++) {
					topicNumber++;
					if (!hm.containsKey(topicNumber)) {
						ArrayList<Double> list = new ArrayList<>();
						for (int i = 1; i <= noTopics; i++) {
							list.add(0.0);
						}
						hm.put(topicNumber, list);
					}
					double questionTopic = Double.parseDouble(rq.getString(qc));
					if (questionTopic < 0.1)
						continue;
					for (int ac = 4; ac <= noTopics + 3; ac++) {
						double answerTopic = Double.parseDouble(rs
								.getString(ac));
						if (answerTopic < 0.1)
							continue;
						double relProduct = questionTopic * answerTopic;
						hm.get(topicNumber).set(ac - 4,
								hm.get(topicNumber).get(ac - 4) + relProduct);
					}
				}
				rq.close();
			}
			st.close();
			rs.close();
			iter++;
		}
		ps.close();
		con.close();
	}

	void writeRelationship(String fileName) throws IOException {
		FileWriter fw = new FileWriter(fileName);
		BufferedWriter bw = new BufferedWriter(fw);

		for (int i = 1; i <= noTopics; i++) {
			bw.write("Topic: " + i);
			bw.newLine();
			ArrayList<Double> list = hm.get(i);
			for (Double val : list) {
				bw.write(String.valueOf(val) + "  ");
			}
			bw.newLine();
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		TopicRelationship tr = new TopicRelationship(40);
		tr.calculateRelationship();
		tr.writeRelationship("TopicRelationship.txt");
	}
}