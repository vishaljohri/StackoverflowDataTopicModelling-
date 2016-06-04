import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class InterpretResults {
	int noTopics;
	String topicName[];

	InterpretResults(int noTopics) {
		this.noTopics = noTopics;
		topicName = new String[this.noTopics];
	}

	void readTopics(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String thisLine = null;
		int i = 0;
		while ((thisLine = bufferedReader.readLine()) != null) {
			topicName[i++] = thisLine.trim();
		}
		bufferedReader.close();
	}

	void interpretTopicShare(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		FileWriter fw = new FileWriter("InterpretedTopicShare.txt");
		BufferedWriter bw = new BufferedWriter(fw);

		String thisLine = null;
		int i = 0;
		while (i < noTopics) {
			thisLine = bufferedReader.readLine();
			thisLine = thisLine.trim();
			bw.write(topicName[i] + ", " + thisLine);
			bw.newLine();
			i++;
		}
		bw.close();
		bufferedReader.close();
	}
	
	void interpretTopicSpread(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		FileWriter fw = new FileWriter("InterpretedTopicSpreadPerDocument.txt");
		BufferedWriter bw = new BufferedWriter(fw);

		String thisLine = null;
		int i = 0;
		while (i < noTopics) {
			thisLine = bufferedReader.readLine();
			thisLine = thisLine.trim();
			bw.write(topicName[i] + ", " + Double.parseDouble(thisLine) * 100);
			bw.newLine();
			i++;
		}
		bw.close();
		bufferedReader.close();
	}

	void interpretTopicRelationship(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		FileWriter fw = new FileWriter("InterpretedTopicRelationship.txt");
		BufferedWriter bw = new BufferedWriter(fw);

		String thisLine = null;
		while ((thisLine = bufferedReader.readLine()) != null) {
			if (thisLine.contains("Topic")) {
				thisLine = thisLine.trim();
				String topicNoManip[] = thisLine.split("\\s+");
				bw.write(topicName[Integer.parseInt(topicNoManip[1]) - 1]);
				bw.newLine();

				String results[] = bufferedReader.readLine().split("\\s+");
				double tempSort[] = new double[results.length];
				for (int i = 0; i < results.length; i++) {
					tempSort[i] = Double.parseDouble(results[i]);
				}
				Arrays.sort(tempSort);
				for (int i = tempSort.length - 1; i >= 0; i--) {
					int j = 0;
					for (j = 0; j < results.length; j++) {
						if (results[j].equals(String.valueOf(tempSort[i])))
							break;
					}
					bw.write(topicName[j] + " " + results[j]);
					bw.newLine();
				}
			}
			bw.newLine();
			bw.newLine();
		}
		bw.close();
		bufferedReader.close();
	}

	public static void main(String[] args) throws IOException {
		InterpretResults ir = new InterpretResults(40);
		ir.readTopics("TopicNames.txt");
		ir.interpretTopicShare("TopicShareAcrossTopics.txt");
		ir.interpretTopicSpread("TopicSpreadPerDocument.txt");
		ir.interpretTopicRelationship("TopicRelationship.txt");
	}
}
