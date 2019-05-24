from sklearn.feature_extraction.text import TfidfVectorizer

from sklearn.feature_selection import chi2, SelectKBest
from random import shuffle

import numpy as np 
from gensim.models import KeyedVectors as word2vec

from sklearn.model_selection import cross_val_score
import pickle
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
from IPython import embed

from utils.find_javas import get_files_for_authors, read_files_as_document, pick_n_authors


if __name__ == "__main__":
    
    with open('res/authors', 'rb') as f:
        authors_we_care_about = pickle.load(f)

    corpus = [(author, read_files_as_document(afs, 9, k = 10)) for author, afs in get_files_for_authors( authors_we_care_about )]
    shuffle(corpus)

    answers = [a for a, c_pair in corpus for  c in c_pair[0] ]
    test_answers = [a for a, c_pair in corpus for c in c_pair[1]]

    test_corpus = [c for a, c_pair in corpus for c in c_pair[1]]
    corpus = [c for a, c_pair in corpus for c in c_pair[0] ]

    # print(corpus)
    # print(answers)


    #vectorizer = TfidfVectorizer()
    #X = vectorizer.fit_transform(corpus)
    # choose between 'code2vec' or 'glove'
    model = word2vec.load_word2vec_format('./code2vec.txt', binary=False)
    X = np.zeros((len(corpus), 256))
    for i, row in enumerate(corpus):
        one_row_of_X = np.zeros((1,256))
        for j, token in enumerate(row):
            if token not in model:
                continue
            vector = model[token]
            if j % 2 == 0:
                one_row_of_X[0, :128] += vector
            else:
                one_row_of_X[0, 128:] += vector

        X[i] = one_row_of_X

    X_test = np.zeros((len(test_corpus), 256))
    for i, row in enumerate(test_corpus):
        one_row_of_X = np.zeros((1,256))
        for j, token in enumerate(row):
            if token not in model:
                continue
            vector = model[token]
            if j % 2 == 0:
                one_row_of_X[0, :128] += vector
            else:
                one_row_of_X[0, 128:] += vector
        X_test[i] = one_row_of_X

    labelencoder = LabelEncoder()
    labels = labelencoder.fit_transform(answers)

    labels_test = labelencoder.transform(test_answers)

    # print(vectorizer.get_feature_names())
    # print(len(vectorizer.get_feature_names()))
   #
    # print(X.shape)
    # print(X)

    print(labels)


    with open('res/code2vec.pickle', 'wb+') as f:
        pickle.dump(X, f)

    with open('res/code2vec.pickle.test', 'wb+') as f:
        pickle.dump(X_test, f)

    with open('res/code2vec_authors.pickle', 'wb+') as f:
        pickle.dump(labels, f)

    with open('res/code2vec_authors.pickle.test', 'wb+') as f:
        pickle.dump(labels_test, f)


