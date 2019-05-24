from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_selection import chi2, SelectKBest
from random import shuffle

from sklearn.model_selection import cross_val_score
import pickle
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
from IPython import embed

from utils.find_javas import get_files_for_authors, read_files_as_document, pick_n_authors


if __name__ == "__main__":
    authors_we_care_about = pick_n_authors(250)
    
    with open('res/authors', 'wb+') as f:
        pickle.dump(authors_we_care_about, f)

    corpus = [(author, read_files_as_document(afs, 9, k = 10)) for author, afs in get_files_for_authors( authors_we_care_about )]
    shuffle(corpus)

    answers = [a for a, c_pair in corpus for  c in c_pair[0] ]
    test_answers = [a for a, c_pair in corpus for c in c_pair[1]]

    test_corpus = [c for a, c_pair in corpus for c in c_pair[1]]
    corpus = [c for a, c_pair in corpus for c in c_pair[0] ]

    # print(corpus)
    # print(answers)


    vectorizer = TfidfVectorizer()
    X = vectorizer.fit_transform(corpus)

    X_test = vectorizer.transform(test_corpus)

    labelencoder = LabelEncoder()
    labels = labelencoder.fit_transform(answers)


    labels_test = labelencoder.transform(test_answers)

    # print(vectorizer.get_feature_names())
    # print(len(vectorizer.get_feature_names()))
    #
    # print(X.shape)
    # print(X)

    print(labels)

    selector = SelectKBest(chi2, k=1000)
    X_new = selector.fit_transform(X, answers)

    X_test_new =  selector.transform(X_test)

    # print(X_new.shape)

    with open('res/tfidf.pickle', 'wb+') as f:
        pickle.dump(X_new, f)

    with open('res/tfidf.pickle.test', 'wb+') as f:
        pickle.dump(X_test_new, f)

    with open('res/authors.pickle', 'wb+') as f:
        pickle.dump(labels, f)

    with open('res/authors.pickle.test', 'wb+') as f:
        pickle.dump(labels_test, f)


    # print(X_new)

