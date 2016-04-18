__author__ = 'VISHAL'

from nltk.corpus import stopwords
import glob

cachedStopWords = stopwords.words("english")
print cachedStopWords

def testFuncNew():
    inPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\RemovedSnippetsAndTags\*'
    outPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\RemovedStopWords\\'
    files = glob.glob(inPath)
    count = 0;
    for fileName in files:
        count += 1
        print "File No = ", count
        actualName = fileName.split('\\')
        print actualName[-1]
        with open(fileName) as f:
            with open(outPath + "RemovedStopWords_" + actualName[-1], "w+") as f1:
                text = f.read()
                text = ' '.join([word for word in text.split() if word.lower() not in cachedStopWords])
                f1.write(text)

if __name__ == "__main__":
    testFuncNew()
