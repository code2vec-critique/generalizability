from utils import mappings
import sys

def map_results_pair_to_eval_format(results, ident_mappings = None):
    if ident_mappings is None:
        ident_mappings = mappings.read_mappings()
    for line in results:
        splitted = line.strip().split(',')
        first_ident = ','.join(splitted[:2])  if mappings.FULL_IDENT else splitted[0]
        second_ident = ','.join(splitted[2:]) if mappings.FULL_IDENT else splitted[-1]

        if first_ident not in ident_mappings:
            print('missing ' + first_ident)
            continue
        first = ident_mappings[first_ident]

        if second_ident not in ident_mappings:
            print('missing ' + second_ident)
            continue
        second = ident_mappings[second_ident]

        # print(first)
        first_subdir = first[0]
        first_filename = first[1]
        first_end_line = first[2]
        first_start_line = '0'
        #first_start_line = first[2]
        #first_end_line = first[3]

        second_subdir = second[0]
        second_filename = second[1]
        second_end_line = second[2]
        second_start_line = '0'
        #second_start_line = second[2]
        #second_end_line = second[3]

        print(first_subdir + "," + first_filename + "," + first_start_line + "," + first_end_line + "," + second_subdir + "," + second_filename + "," + second_start_line + "," + second_end_line)


if __name__ == "__main__":
    # print('hi')
    
    with open(
            sys.argv[1]
            #'results.pairs'
            , 'r') as infile:
        lines = []
        for line in infile:
            lines.append(line)
    map_results_pair_to_eval_format(lines)


    #selected 470700.java selected 474372.java
