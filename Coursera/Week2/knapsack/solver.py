#!/usr/bin/python
# -*- coding: utf-8 -*-

from collections import namedtuple
import copy
import gc

Item = namedtuple("Item", ['index', 'value', 'weight'])
capacity = 0
item_count = 0
items = []
ansVal = ansWeight = 0
ansTaken = []

def getSol(value, weight, taken):
	global ansVal, ansWeight, ansTaken
	# print("sol", value, weight, taken)
	if value > ansVal:
		ansVal = value
		ansWeight = weight
		ansTaken = copy.copy(taken)
		# print("take", ansTaken)
	elif value == ansVal and weight < ansWeight:
		ansWeight = weight
		ansTaken = copy.copy(taken)
		# print("take", ansTaken)
	return

def BAC(idx, value, weight):
	global item_count, items, capacity, ansTaken, taken
	if weight > capacity:
		# print("over", weight)
		return
	
	if idx == item_count:
		# print(weight, capacity)
		getSol(value, weight, taken)
		return
		
	for i in range(2):
		# print("idx", idx)
		# print("ansTaken", ansTaken, "taken", taken)
		taken[idx] = i
		# print("ansTaken", ansTaken, "taken", taken)
		BAC(idx + 1, value + taken[i] * items[i].value, weight + taken[i] * items[i].weight)

def dynamicProgramming(K, items):
	N = len(items)
	dp = [[0 for i in range(N + 1)] for j in range(K + 1)]
	# print(dp)
	for i in range(1, K + 1):
		for j in range(1, N + 1):
			if items[j - 1].weight > i:
				dp[i][j] = dp[i][j - 1]
			else:
					dp[i][j] = max(
						dp[i][j - 1], dp[i - items[j - 1].weight][j - 1] + items[j - 1].value)

	opt = dp[K][N]
	taken = [0] * N
	i, j2 = (K, N)
	while j2 >= 1:
		if dp[i][j2] == dp[i][j2 - 1]:
			taken[j2 - 1] = 0
		else:
			taken[j2 - 1] = 1
			i -= items[j2 - 1].weight
		j2 -= 1
	# assert_sol(K, items, opt, taken)
	return opt, taken

def cmpByAverageCost(item):
	return -float(item.value / item.weight)

def getExpect(items, start, remainCap):
	expect = 0
	for i in range(start, len(items)):
		if items[i].weight <= remainCap:
			expect += items[i].value
			remainCap -= items[i].weight
		else:
			expect += remainCap * items[i].value / items[i].weight
			break
	return expect

def branchAndBound(capacity, items):
	n = len(items)
	maxValue = 0
	maxTakens = [] 
	items.sort(key=cmpByAverageCost)
	# print(items)
	curRemainCap = capacity
	curValue = 0
	curExpect = 0
	curTaken = [0] * n
	curPos = 0
	firstEle = [curRemainCap, curValue, curExpect, curTaken, curPos]
	stack = []
	stack.append(firstEle)
	while(len(stack) > 0):
		# print(len(stack))
		# print('stack before',stack)
		curEle = stack[-1]
		# print('get ele', curEle)
		curRemainCap = curEle[0]
		curValue = curEle[1]
		curExpect = curEle[2]
		curTaken = curEle[3]
		curPos = curEle[4]
		
		stack = stack[:-1]
		# print('stack after', stack)
		if curPos == n: continue

		if curRemainCap < 0: continue
		
		if curExpect < maxValue: continue

		if(curValue > maxValue):
			maxValue = copy.deepcopy(curValue)
			maxTakens = copy.deepcopy(curTaken)
			# print(maxValue)
		
		for i in range(2):
			newRemainCap = curRemainCap - items[curPos].weight * i
			newValue = curValue + items[curPos].value * i
			newExpect = newValue + getExpect(items, curPos + 1, newRemainCap)
			newTaken = copy.deepcopy(curTaken)
			newTaken[items[curPos].index] = i
			newPos = curPos + 1
			# print([newRemainCap, newValue, newExpect, newTaken, newPos])
			stack.append([newRemainCap, newValue, newExpect, newTaken, newPos])
		gc.collect()
	return maxValue, maxTakens

def assert_sol(K, items, opt_value, sol):
	v = 0
	w = 0
	for i in range(len(sol)):
		if sol[i] == 1:
			v += items[i].value
			w += items[i].weight
	assert(v == opt_value)
	assert(w <= K)

def solve_it(input_data):
	global item_count, capacity, items, taken, ansVal, ansWeight, ansTaken
    # Modify this code to run your optimization algorithm

    # parse the input
	lines = input_data.split('\n')

	firstLine = lines[0].split()
	item_count = int(firstLine[0])
	capacity = int(firstLine[1])
	
	
	for i in range(1, item_count+1):
		line = lines[i]
		parts = line.split()
		items.append(Item(i-1, int(parts[0]), int(parts[1])))
		
	taken = [0]*int(len(items))
	ansTaken = [0]*int(len(items))
	if len(items) * capacity <= 20000000:
		value, taken = dynamicProgramming(capacity, items)
	else:
		value, taken = branchAndBound(capacity, items)
	output_data = str(value) + ' ' + str(1) + '\\n'
	output_data += ' '.join(map(str, taken))
	gc.collect()
	# f = open("result/4.txt", "x")
	# f.write(output_data)
	# f.close()
	return output_data


if __name__ == '__main__':
	import sys
	if len(sys.argv) > 1:
		file_location = sys.argv[1].strip()
		with open(file_location, 'r') as input_data_file:
			input_data = input_data_file.read()
		print(solve_it(input_data))
	else:
		print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/ks_4_0)')

