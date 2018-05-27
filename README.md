# CodeforcesTools
### Seeding
Preproccessing the information from the given data, to be used in the datastructures used in the tasks.
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
   plus his hacking score this contest. Then, we form contest preformance object for each user consists of user penalty,          user's old rank, user's new points and save them in separate files.
      
 - #### Query:
  we read the contest preformance file of all contests that the user participate in. we binary search
  on the user's new rank based on his new points in this contest and save it. Finally we plot the oldRank
  versus the new rank in each contest.
  
 - #### TimeComplexity:
  The number of the contests in which the user participate * log(the particpant in the contest).
  let Number of contests is c and particpant is n so time complexity is O(c*log(n)).
