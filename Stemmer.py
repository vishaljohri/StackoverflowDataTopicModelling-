__author__ = 'VISHAL'

from stemming.porter2 import stem
import glob

def testStemmer():
    inPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\RemovedStopWords\*'
    outPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\AfterStemming\\'
    files = glob.glob(inPath)
    count = 0;
    for fileName in files:
        count += 1
        print "File No = ", count
        actualName = fileName.split('\\')
        print actualName[-1]
        with open(fileName) as f:
            with open(outPath + "Stemming_" + actualName[-1], "w+") as f1:
                text = f.read()
                text = ' '.join(stem(word) for word in text.split())
                f1.write(text)

if __name__ == "__main__":
    testStemmer()
