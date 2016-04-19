__author__ = 'VISHAL'

import re
import glob

def removeUrls():
    inPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\AfterStemming\*'
    outPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\CleanedFiles\\'
    files = glob.glob(inPath)
    count = 0
    for fileName in files:
        count += 1
        print "File No = ", count
        actualName = fileName.split('\\')
        print actualName[-1]
        with open(fileName) as f:
            with open(outPath + "Cleaned_" + actualName[-1].split("_")[-1], "w+") as f1:
                text = f.read()
                text = re.sub(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', '', text, flags=re.MULTILINE)
                f1.write(text)

if __name__ == "__main__":
    removeUrls()