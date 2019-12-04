from utils.find_javas import get_files_for_authors, read_files_as_document, pick_n_authors
import pickle

if __name__ == "__main__":
    authors_we_care_about = pick_n_authors(250)

    with open('res/authors', 'wb+') as f:
        pickle.dump(authors_we_care_about, f)

        
