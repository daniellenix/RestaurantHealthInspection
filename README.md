# CMPT276 Project

Team Calcium

Course Project URL: https://opencoursehub.cs.sfu.ca/bfraser/grav-cms/cmpt276/project

## Iteration 1

* Scrum Master: @dnix
* Repo Manager: @haoyia
* Product Manager: @wuhuiw
* Team Member: @ybl3

## User story overview

1. Display list of all restaurants

    * [x] I want the app to be installed with some restaurant and inspection report data for Surrey.
    * [x] [5] I want to see a list of all restaurants sorted alphabetically so I can find a restaurant I am interested in. 
    * [x] [25] I want each item in the list of restaurants to clearly show me some brief information about the restaurant so I can see info at a glance.
    Each restaurant in the list must include:
        * [x] Restaurant name
        * [x] An icon for the restaurant (generic OK)
        * [ ] Info on most the restaurant's most recent inspection report:
            * [ ] number of issues found (sum of crit and non-crit issues)
            * [ ] colour for hazard level (low, medium, high)
            * [ ] icon for the hazard level
            * [ ] how long ago was the inspection done (see next point)
    * [ ] [5] I want to be told when something happened in an intelligent format so that it's easier to understand than dates:
        * [ ] If it was within 30 days, tell me the number of days ago it was (such as "24 days")
        * [ ] Otherwise, if it was less than a year ago, tell me the month and day (such as "May 12")
        * [ ] Otherwise, tell me just the month and year (such as "May 2018")
    * [x] I want to be able to click a restaurant in the list and have it show me the details about that restaurant (see below).

2. Display details of single restaurant

    * [x] I want to see a screen showing the details about a single restaurant so I can understand the history of inspections at that restaurant.
    * [x] [5] I want the restaurant details screen to include:
        * [x] Restaurant name
        * [x] Restaurant address
        * [x] Restaurant GPS coords
    * [x] I want it to list all inspections (in a scrollable list), with the most recent at the top, so I can easily understand the timeline of inspections.
    * [ ] [15] I want each listing for an inspection report to show:
        * [x] critical issues found
        * [x] non-critical issues found
        * [ ] How long ago the inspection occurred (see formatting notes from above)
        * [x] Change the colour, and display an icon, for the hazard level

3. Display details of single inspection

    * [x] I want to see the details of a single inspection report to learn all I can about what the inspection found.
    * [x] [10] I want the inspection details screen to show: [x]
        * [x] Full date of the inspection (such as "May 12, 2019")
        * [x] Inspection type (routine / follow-up)
        * [x] critical issues found
        * [x] non-critical issues found
        * [x] Hazard level icon, hazard level in words, and colour coded.
        * [x] Scrollable list of violations. 
    * [15] I want each violation in the list to show:
        * [ ] An icon reflecting the nature of the violation (food, pest, equipment, ...)
        * [ ] A brief description of the violation which **fits on one line**
        * [x] The severity of the violation: Use an icon and colour to make it easy to see if it's critical or not-critical
    * [x] I want to be able to tap on a single violation to see the long description. It's OK if this is shown on the screen for a moment (such as a toast or snackbar).
