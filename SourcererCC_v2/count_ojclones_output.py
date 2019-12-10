
import sys

correct = 0
wrong = 0


total_number_per_problem = 124750  # 500 choose 2
total_number_of_pairs = 124750 * 104

with open(sys.argv[1], 'r') as infile:
    for line in infile:
        splitted = line.split(',')
        
        # 99,21.c,0,31,99,894.c,0,24
        # 99,2091.c,0,35,99,2371.c,0,30

        problem_1 = splitted[0]
        file_1 = splitted[1]
        problem_2 = splitted[4]
        file_2 = splitted[5]

        if problem_1 == problem_2 and file_1 != file_2:
            correct += 1
        else:
            wrong += 1

print('true positives', correct)
print('false positives', wrong)
print('false negatives', total_number_of_pairs)

print('precision', correct / (correct  + wrong))
print('recall', correct / (total_number_of_pairs))
