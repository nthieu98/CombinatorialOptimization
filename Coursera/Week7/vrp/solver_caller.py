from solver import solve_it
import json
from datetime import datetime

get_result_json = True
files = open('_coursera').read().split('\n')[2:8]

test = []
for f in files:
	tmp = f.split(', ')
	test.append((tmp[0], tmp[1]))

all_result_file = open('all_result.txt', 'a')
all_result_file.write('Time: ' + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + '\n')
result_json = open('results.json', 'w')
result = dict()

curRes = 1
for t in test:
	print('solving', t[1])
	# output = solve_it(open(t[1]).read())
	resFile = open('result/{}.txt'.format(curRes), 'r')
	curRes = curRes + 1
	output = resFile.read()
	output = output.strip() + '\n1.000'
	print(output)
	result[t[0]] = {'output': output}

all_result_file.write(json.dumps(result, indent=4, separators=(',', ':')) + "\n\n")
if get_result_json:
	result_json.write(json.dumps(result, indent=4, separators=(',', ':')) + "\n\n")