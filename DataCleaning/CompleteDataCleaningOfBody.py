__author__ = 'VISHAL'

import os
import errno
from bs4 import BeautifulSoup
from stemming.porter2 import stem
import re
from nltk.corpus import stopwords

cachedStopWords = stopwords.words("english")
print cachedStopWords

def dataCleaning():
    inPath = 'G:\Mallet\DataWithFileStructure\TextualData'
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
                    except OSError as exc:  # Guard against race condition
                        if exc.errno != errno.EEXIST:
                            raise
                with open(outFileNameWithPath, "w+") as f1:
                    # remove code snippets
                    textOriginal = f.read()
                    soup = BeautifulSoup(textOriginal)
                    [s.extract() for s in soup('code')]
                    textRemovedCodeSnippets = soup.get_text().encode('utf-8')

                    # remove stop words
                    textRemovedStopWords = ' '.join([word for word in textRemovedCodeSnippets.split() if word.lower() not in cachedStopWords])

                    # perform stemming
                    textStemming = ' '.join(stem(word) for word in textRemovedStopWords.split())

                    # remove urls
                    textRemovedUrls = re.sub(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', '', textStemming, flags=re.MULTILINE)

                    # write cleaned data to output file
                    f1.write(textRemovedUrls)


if __name__ == "__main__":
    dataCleaning()