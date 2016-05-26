import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TopicShare {
	double topicShare[];
	int noTopics;
	long noDocs;

	public TopicShare(int noTopics) {
		this.noTopics = noTopics;
		this.topicShare = new double[this.noTopics];
		this.noDocs = 0;
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
				noDocs++;
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

	void calTopicSharePerDocument() {
		for (int i = 0; i < noTopics; i++) {
			topicShare[i] = topicShare[i] / noDocs;
		}
		// write output
		try {
			File fileOutput = new File("TopicSharePerDocument.txt");
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
	
	void calcTopicShareAcrossTopics() {
		double sum = 0;
		double totalPerc = 0;
		for (int i = 0; i < noTopics; i++) {
			sum += topicShare[i];
		}
		try {
			File fileOutput = new File("TopicShareAcrossTopics.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					fileOutput));
			for (int i = 0; i < noTopics; i++) {
				bufferedWriter.write(String.valueOf((topicShare[i] / sum) * 100) + "%");
				totalPerc += (topicShare[i] / sum) * 100;
				bufferedWriter.newLine();
			}
			bufferedWriter.write("Total Percentage = " + String.valueOf(totalPerc) + "%");
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TopicShare ts = new TopicShare(40);
		ts.calcTopicAggregate("topic-docs-23-May");
		ts.calTopicSharePerDocument();
		ts.calcTopicShareAcrossTopics();
	}

}