# CMPT276 Project

Team Calcium

Course Project URL: https://opencoursehub.cs.sfu.ca/bfraser/grav-cms/cmpt276/project

## Iteration 2

* Scrum Master: @haoyia
* Repo Manager: @wuhuiw
* Product Manager: @ybl3
* Team Member: @dnix

## User story overview

1. [30] Get updated data

   * [x] When the app start up, if it's been 20 hours or more since data was last updated, I want the application to check with the City of Surrey's server to see if there is more recent data to be downloaded.
   * [x] If there is new data on the server, I want the app to ask the user if they want to update the data so they can choose if now is a good time to do the update.
        * [x] If the user does not want the data updated, then the next time the app starts up it should ask the user again if they want to update.
        * [x] The app must only download the data if an update is available otherwise it will waste the user's bandwidth and battery.
   * [x] I want the application to store downloaded data locally so that it can work offline.
   * [x] I want the application to initially install with the small data set (from iteration 1).
   * [x] Show a please-wait dialog, or a progress dialog, while data is downloading/updating.
        * [x] I need the dialog to have some animation showing the application is working.
        * [x] Plus it must have some way for the user to cancel the download.
        * [x] When the download is cancelled the previously download data should still be used by the app.

2. [30] Map

   * [x] When the application starts up, I want the default view to be a map centred on the user's current location.
   * [x] The map should display pegs showing the location of each restaurant we have data for.
   * [x] I want the map to allow the user to pan (move map around) and pinch to zoom.
   * [x] I want the restaurant pegs to show the hazard level of the most recent inspection report for a restaurant.
        * [x] Each peg must show the hazard level using a colour and an icon. i.e., icon for medium must be different than high hazard.
   * [x] When there are too many pegs in an area, I want the pegs to be clustered intelligently.
   * [x] I want the app to show the user's current GPS position on the map as some form of dot or icon which is distinct from the restaurant pegs.
        * [x] As the user moves, I want the dot on the screen to update to a new location, and have the map follow the user so the display stays relevant as the user moves through the city.
   * [x] I want the user to be able to interact with a peg to get more information.
        * [x] Tapping on a peg should show a small pop-up display of the restaurant name, address, and hazard level of its most recent inspection.
        * [x] The pop-up display must also allow the user to tap again to go to restaurant's full information screen
   * [x] I want the user to have a clear way to toggle between the map screen and the list of restaurants screen (iteration 1) so the user can find a restaurant by either its location on the map, or the list of restaurants.

3. [15] Custom images / icons

   * [x] I want the icon/image for certain restaurants to be specific to that business so that the user can quickly identify business.
        * [x] At least 10 restaurants must have unique icons in the restaurant list screen. Suggested that these icons be the company's logo.
        * [x] At least 5 of these restaurants should have 4 or more locations in Surrey so that the custom icons come up more frequently in the list.

4. [15] Back-button behaviour

   * [x] Toggling between map and restaurant list view is independent of the back button behaviour
        * [x] If a user goes from the map screen to the restaurant list screen and then pressing Android's back button, it exits the application.
        * [x] If a user goes from the restaurant list screen to the map screen and then pressing Android's back button, it exits the application.
   * [x] Pressing a back button (either Android's or the one on the app's screen) from the single restaurant screen takes the user back to either the map screen or the restaurant list screen, whichever the user last visited.
   * [x] On the single restaurant screen, tapping the GPS coords on a restaurant closes the current screen and returns to the map screen, selecting that restaurant and showing the small pop-up info about the restaurant.
## Style Guide

https://google.github.io/styleguide/javaguide.html

If you are unsure about it, you can always ask Android Studio to reformat the code for you.

Code > Reformat Code
