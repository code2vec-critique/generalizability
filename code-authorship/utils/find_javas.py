

import os, glob
from collections import defaultdict
import random

def pick_n_authors(n):
    authors = set()
    with open('authors_with_gteq_9_javas.csv', 'r') as infile:
        for i, line in enumerate(infile):
            splitted = line.split(',')
            author = splitted[0]
            authors.add(author)
    return random.sample(authors, n)
    


def get_files_for_authors(authors):
    authors_to_files = defaultdict(list)

    with open('authors_with_gteq_9_javas.csv', 'r') as infile:
        for i, line in enumerate(infile):

            splitted = line.split(',')
            authors_to_files[splitted[0]].append('/'.join(splitted[1:]).strip())
            
            # path look like gcj/10224486/aa2985759/5677604812095488/0/extracted/b.cpp

    return [(author, authors_to_files[author]) for author in authors]


def read_files_as_document(author_files, file_limit, k):
    file_contents = []
    test_contents = []
    for i, f in enumerate(author_files[:file_limit]):

        with open('./gcj/' + f, 'r', encoding="UTF-8", errors='ignore') as infile:
            content = infile.read()
            mod_content = content.replace('\n', ' ')
            mod_content = ' '.join(mod_content.split())
            if i % k == 3:
                test_contents.append(mod_content)
            else:
                file_contents.append(mod_content)
    
    return file_contents, test_contents
