__author__ = 'VISHAL'

import re
import os
import errno


def removeUrls():
    inPath = 'G:\Mallet\OutputStemming'
    outPath = 'G:\Mallet\CleanedData\\'
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
                    text = re.sub(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', '', text, flags=re.MULTILINE)
                    f1.write(text)

if __name__ == "__main__":
    removeUrls()