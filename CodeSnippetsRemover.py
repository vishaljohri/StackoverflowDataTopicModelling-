__author__ = 'VISHAL'
import glob
from bs4 import BeautifulSoup


def remove_codeSnippets():
    inPath = 'G:\StackoverflowData\DataPreprocessing\OriginalFiles\src\*'
    outPath = 'G:\StackoverflowData\DataPreprocessing\RemovedTagsAndSnippets\\'
    files = glob.glob(inPath)
    for fileName in files:
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