import re
import sys

if __name__ == '__main__':
    args = sys.argv
    if len(args) < 2:
        print('Not enough arguments')
    original = args[1]
    modified = re.sub(r'(?<!^)(?=[A-Z])', '_', original).upper()
    print(modified)
