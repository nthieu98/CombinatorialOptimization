#!/usr/bin/python
# -*- coding: utf-8 -*-

from collections import namedtuple
import math
from datetime import datetime
import pdb
import numpy
from ortools.linear_solver import pywraplp

Point = namedtuple("Point", ['x', 'y'])
Facility = namedtuple("Facility", ['index', 'setup_cost', 'capacity', 'location'])
Customer = namedtuple("Customer", ['index', 'demand', 'location'])

def length(point1, point2):
    return math.sqrt((point1.x - point2.x)**2 + (point1.y - point2.y)**2)

def solve_it(input_data):
    # Modify this code to run your optimization algorithm

    # parse the input
    lines = input_data.split('\n')

    parts = lines[0].split()
    facility_count = int(parts[0])
    customer_count = int(parts[1])
    
    facilities = []
    for i in range(1, facility_count+1):
        parts = lines[i].split()
        facilities.append(Facility(i-1, float(parts[0]), int(parts[1]), Point(float(parts[2]), float(parts[3])) ))

    customers = []
    for i in range(facility_count+1, facility_count+1+customer_count):
        parts = lines[i].split()
        customers.append(Customer(i-1-facility_count, int(parts[0]), Point(float(parts[1]), float(parts[2]))))
    
    if len(customers) == 2000: # instance 8
        obj, solution = trivial_solve(facilities, customers)
    else:
        # test the or-tools from google
        #obj, solution = ortools_solve(facilities, customers)
        if len(customers) < 100 or (len(customers)==100 and len(facilities)==100): # instances 1,2,3
            time_limit = 1800
        # test the scip suite
        if len(customers) == 1000 and len(facilities) == 100: # instance 4
            time_limit = 600000
        elif len(customers) == 800 and len(facilities) == 200: # instance 5
            time_limit = 6000
        else: # instances 6,7
            time_limit = 3000
        obj, solution = ortools_solve(facilities, customers, time_limit=time_limit)

    # prepare the solution in the specified output format
    output_data = '%.2f' % obj + ' ' + str(0) + '\n'
    output_data += ' '.join(map(str, solution))

    return output_data


def trivial_solve(facilities, customers):
    # build a trivial solution
    # pack the facilities one by one until all the customers are served
    solution = [-1]*len(customers)
    capacity_remaining = [f.capacity for f in facilities]

    facility_index = 0
    for customer in customers:
        if capacity_remaining[facility_index] >= customer.demand:
            solution[customer.index] = facilities[facility_index].index
            capacity_remaining[facility_index] -= customer.demand
        else:
            facility_index += 1
            assert capacity_remaining[facility_index] >= customer.demand
            solution[customer.index] = facilities[facility_index].index
            capacity_remaining[facility_index] -= customer.demand

    # calculate the cost of the solution
    obj = sol_value(solution, facilities, customers)

    return obj, solution

def sol_value(solution, facilities, customers):
    used = [0]*len(facilities)
    for facility_index in solution:
        used[facility_index] = 1

    obj = sum([f.setup_cost*used[f.index] for f in facilities])

    for customer in customers:
        obj += length(customer.location, facilities[solution[customer.index]].location)
    return obj

def ortools_solve(facilities, customers, time_limit=None):
    print('Num facilities {}'.format(len(facilities)))
    print('Num customers {}'.format(len(customers)))

    if time_limit is None:
        time_limit = 1000 * 60 # 1 minute

    solver = pywraplp.Solver('SolveIntegerProblem',
                             pywraplp.Solver.CBC_MIXED_INTEGER_PROGRAMMING)
    # x_i = 1 iff facility i is chosen
    x = [] # 1xN
    # y_ij = 1 iff custome j is assigned to facility i
    y = [[] for x in range(len(facilities))] # NxM

    for i in range(len(facilities)):
        x.append(solver.BoolVar('x{}'.format(i)))
        for j in range(len(customers)):
            y[i].append(solver.BoolVar('y{},{}'.format(i,j)))

    print('x variable with dim {}'.format(len(x)))
    print('y variable with dim {}x{}'.format(len(y), len(y[0])))

    # total demand to 1 facility <= its capacity
    for i in range(len(facilities)):
        constraint = solver.Constraint(0.0, facilities[i].capacity)
        for j in range(len(customers)):
            constraint.SetCoefficient(y[i][j], customers[j].demand)

    # exactly one facility per customer
    for j in range(len(customers)):
        constraint = solver.Constraint(1.0, 1.0)
        for i in range(len(facilities)):
            constraint.SetCoefficient(y[i][j], 1.0)

    # y_ij can be 1 only x_i is 1
    for i in range(len(facilities)):
        for j in range(len(customers)):
            constraint = solver.Constraint(-solver.infinity(), 0.0)
            constraint.SetCoefficient(y[i][j], 1.0)
            constraint.SetCoefficient(x[i], -1.0)

    # objective
    objective = solver.Objective()
    objective.SetMinimization()
    for i in range(len(facilities)):
        objective.SetCoefficient(x[i], facilities[i].setup_cost)
        for j in range(len(customers)):
            objective.SetCoefficient(y[i][j], length(customers[j].location, facilities[i].location))

    print('Number of variables =', solver.NumVariables())
    print('Number of constraints =', solver.NumConstraints())

    solver.set_time_limit(time_limit)
    print('OR-Tools starts at {}'.format(datetime.now().time()))
    result_status = solver.Solve()
    print(result_status)
    # The problem has an optimal solution.
    assert result_status == pywraplp.Solver.OPTIMAL
    assert solver.VerifySolution(1e-7, True)
    
    # if result_status == pywraplp.Solver.MPSOLVER_NOT_SOLVED:
    #     print("leu")

    val = solver.Objective().Value()
    y_val = [[] for x in range(len(facilities))] # NxM
    assignment = []
    for i in range(len(facilities)):
        for j in range(len(customers)):
            y_val[i].append(int(y[i][j].solution_value()))
    y_val = numpy.array(y_val)
    for j in range(len(customers)):
        assignment.append(numpy.where(y_val[:,j]==1)[0][0])

    return val, assignment

import sys

if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_data = input_data_file.read()
        print(solve_it(input_data))
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/fl_16_2)')

