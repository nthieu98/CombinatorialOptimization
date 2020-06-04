#!/usr/bin/python
# -*- coding: utf-8 -*-

import math
from collections import namedtuple

from ortools.constraint_solver import routing_enums_pb2
from ortools.constraint_solver import pywrapcp

Point = namedtuple("Point", ['x', 'y'])


def create_data_model(points):
    """Stores the data for the problem."""
    data = {}
    data['distance_matrix'] = []  # yapf: disable
    for i in range(len(points)):
        dis_arr = []
        for j in range(len(points)):
            dis_arr.append(length(points[i], points[j]))
        data['distance_matrix'].append(dis_arr)
    data['num_vehicles'] = 1
    data['depot'] = 0
    return data

def get_solution(manager, routing, solution):
    """Prints solution on console."""
    ans = []
    index = routing.Start(0)
    while not routing.IsEnd(index):
        ans.append(index)
        previous_index = index
        index = solution.Value(routing.NextVar(index))
    return ans    

def length(point1, point2):
    return math.sqrt((point1.x - point2.x)**2 + (point1.y - point2.y)**2)

def solve_it(input_data):
    # Modify this code to run your optimization algorithm

    # parse the input
    lines = input_data.split('\n')

    nodeCount = int(lines[0])

    points = []
    for i in range(1, nodeCount+1):
        line = lines[i]
        parts = line.split()
        points.append(Point(float(parts[0]), float(parts[1])))

    # build a trivial solution
    # visit the nodes in the order they appear in the file
    

    data = create_data_model(points)
    
    manager = pywrapcp.RoutingIndexManager(len(data['distance_matrix']),
                                           data['num_vehicles'], data['depot'])
    
    routing = pywrapcp.RoutingModel(manager)
    
    def distance_callback(from_index, to_index):
        """Returns the distance between the two nodes."""
        # Convert from routing variable Index to distance matrix NodeIndex.
        from_node = manager.IndexToNode(from_index)
        to_node = manager.IndexToNode(to_index)
        return data['distance_matrix'][from_node][to_node]

    transit_callback_index = routing.RegisterTransitCallback(distance_callback)
    
    routing.SetArcCostEvaluatorOfAllVehicles(transit_callback_index)

    # Setting first solution heuristic.
    search_parameters = pywrapcp.DefaultRoutingSearchParameters()
    search_parameters.local_search_metaheuristic = (
    routing_enums_pb2.LocalSearchMetaheuristic.GUIDED_LOCAL_SEARCH)
    search_parameters.time_limit.seconds = 60
    search_parameters.log_search = False

    # Solve the problem.
    solution = routing.SolveWithParameters(search_parameters)
    
    if solution:
        # print_solution(manager, routing, solution)
        ans = get_solution(manager, routing, solution)

    # calculate the length of the tour
    obj = length(points[ans[-1]], points[ans[0]])
    for index in range(0, nodeCount-1):
        obj += length(points[ans[index]], points[ans[index+1]])
    
    # print(data)
    # prepare the solution in the specified output format
    output_data = '%.2f' % obj + ' ' + str(0) + '\n'
    output_data += ' '.join(map(str, ans))
    f = open("result/2.txt", "w")
    f.write(output_data)
    f.close()
    return output_data


import sys

if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_data = input_data_file.read()
        print(solve_it(input_data))
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/tsp_51_1)')

