__author__ = 'VISHAL'

import os
import errno
from bs4 import BeautifulSoup


def remove_codeSnippets():
    inPath = 'G:\Mallet\DataWithFileStructure\TextualData'
    outPath = 'G:\Mallet\OutputRemovedCodeSnippets\\'
    count = 0
    for root, directories, filenames in os.walk(inPath):
        for fileName in filenames:
            count += 1
            actualName = os.path.join(root, fileName)
            outPostfix = os.path.relpath(actualName, inPath)
            print "File no = ", count, " : ", outPostfix
            with open(actualName) as f:
                outFileNameWithPath = outPath + outPostfix
                if not os.path.exists(os.path.dirname(outFileNameWithPath)):
                    try:
                        os.makedirs(os.path.dirname(outFileNameWithPath))
                    except OSError as exc: # Guard against race condition
                        if exc.errno != errno.EEXIST:
                            raise
                with open(outFileNameWithPath, "w+") as f1:
                    text = f.read()
                    soup = BeautifulSoup(text)
                    [s.extract() for s in soup('code')]
                    f1.write(soup.get_text().encode('utf-8'))


if __name__ == "__main__":
    remove_codeSnippets()