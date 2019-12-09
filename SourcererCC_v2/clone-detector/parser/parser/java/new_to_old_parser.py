

with open('/Users/kanghongjin/repos/SourcererCC_70_mil_1e/tokenizers/file-level/blocks.file', 'r') as infile:
    for line in infile:
        preamble = line.split('@#@')[0]
        splitted = preamble.split(',')
        new_output = ','.join(splitted[:2]) + '@#@' + line.split('@#@')[1].rstrip()
        print(new_output)
