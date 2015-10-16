# TimeCycle

### Description:
Enhanced stopwatch that is designed to meet the requirements of time measurement for a given workout routine.
For the workout purposes it would be helpful if the stopwatch could monitor the workout time and the rest time
automatically without any user interaction during the routine.

>**Use-Case:**
- *Title*: Measure time
- *Actor*: User
- *Scenario*: The user should be able to set up the desired plan (e.g. 1 min of exercice, 30 sec of rest with 5 repetitions). App should then handle the time monitoring and switch between pause/exercise times automaticaly with notification that is  easy to see (or hear).



### FURPS+ Requirements:
* *Functional*:
  - simple StopWatch feature (start/stop/lap/reset)
  - enhanced StopWatch where time for the activity and time for the rest is measured independently
* *Usability*:
  - basic UI description should be provided when the user launches the app for the first time
* *Reliability*:
  - workout plans should be safely saved
* *Performance*:
  - should not be an issue because of the app simplicity
* *Supportability*:
  - translation to english, czech, spanish
  - screen sizes optimization
  - tablet support will be considered
* *Design*:
  - UI should be simple and clear (even in distance should be obvious how much time is left)
  - Buttons should be designed for easy access

#### TODO:
- [ ] Wake lock
- [ ] ListView for workout history  
- [ ] Graph of the progress at landscape reorientation  
- [ ] Swipe view as navigation between activities (main in the middle) 
- [ ] Make the background color change according to time left (http://stackoverflow.com/questions/2614545/animate-change-of-view-background-color-in-android) 
- [ ] Button "Pause" which will start the "Pause timer" 
- [ ] Playlists for workout/pause time 
- [ ] Tint the buttons 
- [ ] Vibration for buttons 
- [ ] Notifications for training plan
- [ ] Rating after workout (Did it, Almost, Killed it) 
- [ ] Notify/ask (when launching next time) to change the routine if the user killed it previously (save the previous one as an option) 
- [ ] Turn timer on by clicking on time and pop up time picker to the user 
- [ ] Give UI hints on First launch (consider same action in menu) 
- [ ] Cink when workout/pause times change 
- [ ] Change color of the timer when finishing  
