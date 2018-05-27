# CodeforcesTools
### Seeding
Preproccessing the information from the given data in a desired way for creating useful objects used in the tasks. These methods are called once at the beginning of the program.
- void preprocessHandles() 
  getting all the user handles exist in the data to be used in reading user informations.
- void preprocessContests()
  process all contests to get contestants scores and rankings.
- void preprocessUsersRatings()
  getting all the current ratings of the users and write them in a json file.
- void preprocessStatus()
  read the submissions of each user, then filter them to get:
  - the real time contest first submission for each problem
  - the accepted submissions.
  
### Contest Performance Task:
 - #### Prerocessing:
   First, we pass through the user's submission which found in status files filtering them by contests in which the user
   particpate in. We calculate the penalty of each contest by the time of the first sumbission in each submitted problem
   plus his hacking score this contest. Then, we form contest preformance object for each user consists of user penalty,          user's old rank, user's new points and we save the points of all users who participate in this contest. save it in separate   files.
      
 - #### Query:
  we read the contest preformance file of all contests that the user participate in. we binary search
  on the user's new rank based on his new points and user's points in this contest and save it. Finally we plot the oldRank
  versus the new rank in each contest.
  
 - #### TimeComplexity:
  The number of the contests in which the user participate * log(the particpant in the contest).
  let Number of contests is c and particpant is n so time complexity is O(c*log(n)).
  
### GetActiveUsers
- void processUsersRatings()
  contrusting a treemap of the user ratings where the keys in it is the ratings and 
  the value are the users which have these ratings. 
-  ArrayList<String> execute(int t1, int t2, int rLo, int rHi, int cnt)
   - iterate over the ratings in the range [rlo,rHi]. complexity: O(n) where 0 < n < 4000.
     - for each rating value we get the users in this rating. complexity: O(m) where m is the number of users.
        - for each user get the record of the accpepted submissions sorted and represented by its creation time. complexity: log(n)
        - for each user submissions we retieve the number of submissions in the range [t1,t2] by using binary search.
          complexity 2*log(k) where k is the number of submissions.
   - sort the users according to the number of retrieved submissions. complexity: O(m*log(m))
   - return the first cnt users from the sorted array.
  Total complexity:O(MAX(m*log(k),m*log(m))).
### Problems Selection
  - void process()
   - get the problems sorted according to the solved count. 
   - read the first accepted submissions for solved problems of every user in the given handles. O(size(handles)
   - iterate over the accepted submissions to count the number where problem x is solved before problem y. 
     O(n) where n is the  number of problems
   - iterate over the constructed map of the problems priority and form an edge between problem x and problem y if and only if
     the count[x][y] is more than or equal percentCount (size(handles) * p/100). O(n^2)
   - total complexity: O((n^2)*size(handles)).
  -  void sortDeadlocks():
    run strongly connected component to group deadlocks on the graph of adjacency list. 
    Then construct importance and  lexicographical order arrays to sort deadlocks and run topological ordering.
    O(n*log(n)) where n is the number of problems.
  - void sortProblems():
    sort the problems inside each deadlock. O(n*log(n)).
  - ArrayList<ArrayList<String>> execute(String[] handles, String tag, int minSolved, int maxSolved, int p, int cnt):
   - run scc on the adjacency list to get the graph of deadlocks.
   - sort problems inside each deadlock.
   - return fist cnt problem from the sorted deadlocks.
   - Total complexity: O((n^2)*size(handles)) where n is the size of problems.
  
  
  

