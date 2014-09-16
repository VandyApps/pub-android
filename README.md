Pub Android
===========

Android client for the Vanderbilt University's Pub online order monitoring system.

## Overview

The functionality of this app is fairly straight-forward.

- Every 30 seconds or so, the app should pull the list of orders from a cloud-based server.
- The app should display these orders, and optionally allow the user to specify their specific order number.
- If the user's order number appears in the list, the app should notify them using a ringtone.

The server protocol we're using is RESTful. You can see an overview of the api here:

http://vandyapps.com:7070/

And you can manipulate the server data here:

http://vandyapps.com:7070/console

To implement web functionality, we will rely in part on the Retrofit library created by Square, which turns RESTful API's into Java interfaces.

http://square.github.io/retrofit/

We will also be using ButterKnife, which supports dependency injection specifically for Android GUI things.

http://jakewharton.github.io/butterknife/

## TODO

- Enhance the GUI or add new features
- Find out when the Service actually quits querying for data (because right now it looks like it never stops)
