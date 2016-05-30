import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TagsCumulativeAcrossDocuments {
	int noTopics;

	public TagsCumulativeAcrossDocuments(int noTopics) {
		this.noTopics = noTopics;
	}

	void calcCumulative(String path) throws IOException {
		HashMap<String, Double> hm = new HashMap<>();

		// process the topic files
		for (int i = 1; i <= noTopics; i++) {
			File file = new File(path + "\\" + i);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String thisLine = null;
			while ((thisLine = bufferedReader.readLine()) != null) {
				String tagValue[] = thisLine.split("\\s+");
				if (tagValue.length == 2) {
					if (!hm.containsKey(tagValue[0])) {
						hm.put(tagValue[0], 0.0);
					}
					hm.put(tagValue[0],
							hm.get(tagValue[0])
									+ Double.parseDouble(tagValue[1]));
				}
			}
			bufferedReader.close();
		}

		// write result of tags cumulative values in descending order

		// convert map to list
		List<Map.Entry<String, Double>> list = new LinkedList<>(hm.entrySet());

		// sort the list with comparator, based on map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});
		System.out.println(list);

		// write results to output file
		FileWriter fileWriter = new FileWriter(
				"TopicsCumulativeAcrossDocuments.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Map.Entry<String, Double> el : list) {
			bufferedWriter.write(el.getKey() + " " + el.getValue());
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}

	public static void main(String[] args) throws IOException {
		TagsCumulativeAcrossDocuments t = new TagsCumulativeAcrossDocuments(40);
		t.calcCumulative("G:\\Mallet\\Analysis_May25\\DataWithFileStructure\\TopicTags");
	}
}
