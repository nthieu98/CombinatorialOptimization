def greedy(set_count, item_count, sets):
  is_optimal = 0
  solution = [0]*set_count
  covered = set()
  
  while len(covered) < item_count:
      sorted_sets = sorted(sets, key=lambda s: -s.cost*len(set(s.items)-covered) if len(set(s.items)-covered) > 0 else s.cost)
      #print covered
      #print [-s.cost*len(set(s.items)-covered) for s in sorted_sets]
      #print sorted_sets
      for s in sorted_sets:
          if solution[s.index] < 1:
              solution[s.index] = 1
              covered |= set(s.items)
              break
  return is_optimal, solution