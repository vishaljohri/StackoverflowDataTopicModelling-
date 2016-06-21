import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FindPostsPerMonth {
	HashMap<String, Integer> hmAnswer;
	HashMap<String, Integer> hmQuestion;

	public FindPostsPerMonth() {
		hmAnswer = new HashMap<>();
		hmQuestion = new HashMap<>();
	}

	void countPosts(String filePath) throws IOException {
		// answer posts
		File fileAnswer = new File(filePath + "\\" + "Answers");
		for (File answerMonth : fileAnswer.listFiles()) {
			hmAnswer.put(answerMonth.getName(), answerMonth.listFiles().length);
		}

		// question posts
		File filequestion = new File(filePath + "\\" + "Questions");
		for (File questionMonth : filequestion.listFiles()) {
			hmQuestion.put(questionMonth.getName(),
					questionMonth.listFiles().length);
		}

		List<String> list = new ArrayList<>(hmAnswer.keySet());
		Collections.sort(list);

		File fileOutput = new File("NumberPostsPerMonth");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				fileOutput));
		for (String s : list) {
			bufferedWriter.write(s + ", " + hmAnswer.get(s) + ", " + "Answer");
			bufferedWriter.newLine();
		}
		for (String s : list) {
			bufferedWriter.write(s + ", " + hmQuestion.get(s) + ", "
					+ "Question");
			bufferedWriter.newLine();
		}
		for (String s : list) {
			bufferedWriter.write(s + ", "
					+ (hmAnswer.get(s) + hmQuestion.get(s)) + ", "
					+ "Cumulative");
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}

	public static void main(String[] args) throws IOException {
		FindPostsPerMonth fp = new FindPostsPerMonth();
		fp.countPosts("G:\\Mallet\\Analysis_Final\\CleanedData");
	}
}
