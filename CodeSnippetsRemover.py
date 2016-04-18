__author__ = 'VISHAL'
import glob
from bs4 import BeautifulSoup


def remove_codeSnippets():
    inPath = 'C:\Users\VISHAL\workspace\StackOverFlow\src\*'
    outPath = 'G:\StackoverflowData\DataPreprocessing\ProcessedDataOnlyBody\RemovedSnippetsAndTags\\'
    files = glob.glob(inPath)
    count = 0;
    for fileName in files:
        count += 1
        print "File no = ", count
        actualName = fileName.split('\\')
        print actualName[-1]
        with open(fileName) as f:
            with open(outPath + "RemovedTagsAndSnippets_" + actualName[-1], "w+") as f1:
                text = f.read()
                soup = BeautifulSoup(text)
                [s.extract() for s in soup('code')]
                f1.write(soup.get_text().encode('utf-8'))


if __name__ == "__main__":
    remove_codeSnippets()