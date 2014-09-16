Pub Android
===========

Android client for the Vanderbilt University's Pub online order monitoring system.

## Overview

The functionality of this app is fairly straight-forward.

- Every 30 seconds or so, the app should pull the list of orders from a cloud-based server.
- The app should display these orders, and optionall allow the user to specify their specific order number.
- If the user's order number appears in the list, the app should notify them using a ringtone.

The server protocol we're using is RESTful. You can see an overview of the api here:

http://vandyapps.com:7070/

To implement web functionality, we will rely in part on the Retrofit library created by Square, which turns RESTful API's into Java interfaces.

http://square.github.io/retrofit/

We will also be using ButterKnife, which supports dependency injection specifically for Android GUI things.

http://jakewharton.github.io/butterknife/

## TODO

- Create a Retrofit interface for our service
- Modify the Service class to use the Retrofit interface
- Enhance the GUI or add new features

