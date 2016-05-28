import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TopicSpread {
	int noTopics;
	double cumTopicSpread[];
	long noDocs;

	public TopicSpread(int noTopics) {
		this.noTopics = noTopics;
		this.cumTopicSpread = new double[this.noTopics];
		noDocs = 0;
	}

	void calcTopicSpread(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String thisLine = null;
		while ((thisLine = bufferedReader.readLine()) != null) {
			String str[] = thisLine.split("\\s+");
			for (int i = 0; i < noTopics; i++) {
				double val = Double.parseDouble(str[i + 2]);
				if (val >= 0.10) {
					cumTopicSpread[i]++;
				}
			}
			System.out.println(thisLine);
			noDocs++;
		}
		bufferedReader.close();
	}

	void writeResults() throws IOException {
		for (int i = 0; i < noTopics; i++) {
			cumTopicSpread[i] = cumTopicSpread[i] / noDocs;
		}

		// write output
		File fileOutput = new File("TopicSpreadPerDocument.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				fileOutput));
		for (int i = 0; i < noTopics; i++) {
			bufferedWriter.write(String.valueOf(cumTopicSpread[i]));
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}

	public static void main(String[] args) throws IOException {
		TopicSpread ts = new TopicSpread(40);
		ts.calcTopicSpread("topic-docs-May-26");
		ts.writeResults();
	}
}
