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
