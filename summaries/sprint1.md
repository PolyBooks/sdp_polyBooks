# Summary for week 1

## Dario
I designed ER-diagram for the data in our database. I also defined an API to abstract the database for the UI. It's not complete as does not allow accessing/modifying data about the users.

Next time I will complete the API, write tests for it, and do other tasks still to be defined.

## Joshua
I created a prototype of the UI we would need to show our database/query.
The task itself turned out not to be too hard but  took a lot of time due to me being unfamiliar with the setup, alongside a few technical problems (Nicolas helped me a lot with those). 
Later when we actually implement this UI test will have to be made, but right now it would be just testing android studio widgets.

Next time I'll try to spend more time reviewing and reacting to others code

## Nicolas
I mainly focused on the db part this week in multiple ways:
- Discuss potential changes towards DB ER-model with @DGoedtkindtEPFL's base model
- Discuss which DB we should choose with @so7fie
- Create/Setup a Firebase DB + created an activity to easily be able to test it

Next week, we will focus on writing/filling our database access API to have a fully functional db

## Raoul (Scrum Master)
First, I set-up Cirrus, CodeClimate, the PR rules and the badges for our github repo.

Then, I started to work on the activity to add a new sale, but as I was conceptualising it, I realised it needed to be 3-4 activities instead of 1 and would require much more work. Instead of a single task, it should probably be divided between “Set-up the flow logic and basic activities to add a sale” (mostly done), “Scan a barcode and retrieve the ISBN from it”, “Convert an ISBN into book data”, “Take a picture of a book” and later “Improve UI and design”.

I have kept this work locally for now, as it is neither functional, nor tested yet.

Lastly, I also reviewed each PR.

## Sophie
I was not able to work very much on my part this week because of other assignments. I spent some time inspecting the parse framework for the backend but finally we opted to Firebase.

Next week, I will either implement the database API with Nicolas, or create an activity to let the user to filter/sort the books

## Zied
This week:
- I got familiarised with the activity design and code xml of android studio
- I created 3 activities (3 pages front end): Home, registration and log in
- I linked these activities by buttons so that we can navigate between them

Next week: I will do the system for registration and log in of users in the app => Users of the app will be able to register and to log in the app

## Overall team
We mostly layed down the foundations for future work this week:

On the back-end side, we got our API and selected Firebase, so the tasks has been fulfilled.

On the front-end side, we named our tasks "Create an activity for ...". What has been achieved for each task was creating the layout and connecting the activities through buttons, but not really deep functionalities linking with the back-end and external libraries.

We did our two stand-ups meetings by adapting a bit the format: First everyone states where they are as usual, but then instead of adjourning the meeting, we stayed and discussed the issues raised up during the first 5 minutes.
