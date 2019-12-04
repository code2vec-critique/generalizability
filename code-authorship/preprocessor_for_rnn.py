from random import shuffle

import numpy as np 

import pickle
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
import csv

from utils.find_javas import get_files_for_authors, read_files_as_document, pick_n_authors
import re
import string

if __name__ == "__main__":
    
    with open('res/authors', 'rb') as f:
        authors_we_care_about = pickle.load(f)

    corpus = [(author, read_files_as_document(afs, 9, k = 10)) for author, afs in get_files_for_authors( authors_we_care_about )]
    shuffle(corpus)

    answers = [a for a, c_pair in corpus for  c in c_pair[0] ]
    test_answers = [a for a, c_pair in corpus for c in c_pair[1]]

    test_corpus = [c for a, c_pair in corpus for c in c_pair[1]]
    corpus = [c for a, c_pair in corpus for c in c_pair[0] ]

    labelencoder = LabelEncoder()
    labels = labelencoder.fit_transform(answers)

    labels_test = labelencoder.transform(test_answers)

    print(labels)


    with open('train.tsv', 'w', encoding='utf-8') as fw:
        writer = csv.writer(fw, delimiter='\t')
        writer.writerow(['label','body'])
        for i, line in enumerate(corpus):
            tokens = [x.lower() for x in re.findall(r'\w+|\W+', line) if not x.isspace()]
            label = labels[i]
            body = ' '.join(tokens)
            writer.writerow([label, body])

            
    with open('test.tsv', 'w', encoding='utf-8') as fw:
        writer = csv.writer(fw, delimiter='\t')
        writer.writerow(['label','body'])
        for i, line in enumerate(test_corpus):
            tokens = [x.lower() for x in re.findall(r'\w+|\W+', line) if not x.isspace()]
            label = labels_test[i]
            body = ' '.join(tokens)
            writer.writerow([label, body])




