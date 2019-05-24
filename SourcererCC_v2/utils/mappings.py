
import csv

FULL_IDENT = False

def read_mappings():
    mappings = {}
    block_level_file = 'tokenizers/block-level/file_block_stats/files-stats-0.stats'
    file_level_file = 'ojclones/files-stats-0.stats'
    use_block = False
    

    with open(block_level_file if use_block else file_level_file, 'r') as infile:
        reader = csv.reader(infile)

        current_filename = ''
        for row in reader:
            ident = row[0] +',' + row[1] if FULL_IDENT else row[1]
            if use_block:
                ident = ident.lstrip('b')
                if 'f' in ident:
                    current_filename = row[2]
                    continue
            path = row[2]

            # mappings[ident] = reduce_path_to_subdirectory(path)
            # mappings[ident].append(current_filename)
            mappings[ident] = reduce_path_to_subdirectory(current_filename if use_block else path)
            if not use_block:
                mappings[ident].append(row[-3]) 
                mappings[ident].append(row[-1]) 
            else:
                mappings[ident].append(row[-2]) 
                mappings[ident].append(row[-1]) 


    # print(mappings)
    return mappings

def reduce_path_to_subdirectory(path):
    return path.split('/')[-2:]
