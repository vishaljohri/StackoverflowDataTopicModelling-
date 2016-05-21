__author__ = 'VISHAL'

from nltk.corpus import stopwords
import os
import errno

cachedStopWords = stopwords.words("english")
print cachedStopWords


def removeStopWords():
    inPath = 'G:\Mallet\OutputRemovedCodeSnippets'
    outPath = 'G:\Mallet\OutputRemovedStopWords\\'
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
                    text = ' '.join([word for word in text.split() if word.lower() not in cachedStopWords])
                    f1.write(text)


if __name__ == "__main__":
    removeStopWords()
