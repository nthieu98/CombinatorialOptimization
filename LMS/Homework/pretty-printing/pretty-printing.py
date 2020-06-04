def align(paragraph, L, f):
  n = 0 # Number of words
  words = [] # words of input
  dp = [] # Optimal value at word ith
  trace = [] # Trace output

  words = paragraph.split(" ")

  for word in words:
    if len(word) > L:
      f.write("Length of \'"+ word + "\'" + " is greater than L(" + str(L) + ")\n\n")
      return

  n = len(words)
  dp = [1000000000 for x in range(n+1)]
  trace = [0 for x in range(n+1)]
  dp[0] = 0

  for i in range(1, n + 1):
    lineLen = 0
    for j in range(i, 0, -1):
      lineLen += len(words[j - 1])
      if lineLen > L:
        break
      newValue = dp[j - 1] + pow(L - lineLen, 2)
      if(dp[i] > newValue):
        dp[i] = newValue
        trace[i] = j - 1
      lineLen += 1

  # print(dp[n])

  # trace result
  lineEnd = []
  t = n
  while t != 0:
    lineEnd.append(t)
    t = trace[t]

  
  # print paragraph
  cur = 0
  curLine = ""
  for i in reversed(lineEnd):
    while cur != i-1:
      curLine += words[cur] + " "
      cur += 1
    curLine += words[cur] + "\n"
    # print(curLine)
    f.write(curLine)
    cur += 1
    curLine = ""
  # print("\n")
  # f.write("\nSum of the squares of the slacks: " + str(dp[n]))
  f.write("\n")

def main():
  print("Please input L: ")
  L = int(input())
  # L = 5 # maximum length of line
  f = open("input.txt", "r", encoding="utf-8")
  paragraphs = f.read().split("\n\n")
  f.close()
  f = open("output.txt", "w", encoding="utf-8")
  for para in paragraphs:
    align(para, L, f)
  f.close()

main()