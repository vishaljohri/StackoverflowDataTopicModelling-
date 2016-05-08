import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TopicShare {
	double topicShare[];
	int noTopics;
	int noDocs;

	public TopicShare(int noTopics, int noDocs) {
		this.noTopics = noTopics;
		this.topicShare = new double[this.noTopics];
		this.noDocs = noDocs;
	}

	void calcTopicAggregate(String fileName) {
		try {
			File file = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String thisLine = null;
			while ((thisLine = bufferedReader.readLine()) != null) {
				String str[] = thisLine.split("\\s+");
				for (int i = 0; i < noTopics; i++) {
					double val = Double.parseDouble(str[i + 2]);
					if (val >= 0.10) {
						topicShare[i] = val;
					}
				}
				System.out.println(thisLine);
			}
			bufferedReader.close();

			// write output
			File fileOutput = new File("Topic_Aggregate.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					fileOutput));
			for (int i = 0; i < noTopics; i++) {
				bufferedWriter.write(String.valueOf(topicShare[i]));
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void calTopicProportion() {
		for (int i = 0; i < noTopics; i++) {
			topicShare[i] = topicShare[i] / noDocs;
		}
		// write output
		try {
			File fileOutput = new File("Final_TopicShare.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					fileOutput));
			for (int i = 0; i < noTopics; i++) {
				bufferedWriter.write(String.valueOf(topicShare[i]));
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TopicShare ts = new TopicShare(30, 489907);
		ts.calcTopicAggregate("doc-topics-30");
		ts.calTopicProportion();
	}

}
