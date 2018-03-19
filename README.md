# QSMVehicleInterface


//LOG
Mar 19 2018
I wanted to design this app with the optimistic view that the background services asynchronously collecting data can actually 
provide us with much more data that we think we need at this time.

For example, we are using a GPS service to recieve Location objects from the GPS module in the android device.
We are only extracting the speed attribute of this Location object to update the speedometer on the HUD.
However, the Location object from the GPS module actually contains tons of other juicy data that potentially 
has a million other uses. 
Say i wanted to make a processes that logs and stores gps location over time. The information is already being gathered by the GPS service, so i just need to add the latitude and longitude values to the message sent by the GPS service, and add a receiver to the logging class to get the data

Concept:

The app runs on the principal that all external data must be collected, processed, and provided ASYNCHRONOUSLY
to the running activity using SERVICES (currently GPS, sensors, and bluetooth).
This app was built fully priotitizing performance over battery life and memory use.


Process:
The SERVICES package needed information into an INTENT with an action corresponding to its contents.
(eg. intent.LOCATION_UPDATE contains speed int, intent.SENSOR_UPDATE contains vehicleIncline)

This intent is broadcasted to the rest of the app
If a process needs information from a service, it must implement a BROADCAST LISTENER to catch intents



Purpose:
The purpose of this implementation is to allow for all incoming data to be processed asynchronously, 
and for that information to be provided to the scope of the application as soon as it is ready.
This allows the UI elemts of the App to be extremely responsive as it eliminates the need for the active activity to process
any data, leaving it the sole responsibility of updating the UI element value as a new value is provided.


This implementation allows for a truly modular and expandable project as it allows for additional processes to be added without distrupting communication between existing components of the project.


Example data flow
1) Physical location changes
2) GPS module outputs new location object

3) GPSHelper asyncronously receives location object from GPS module
3) GPSHelper extracts getSpeed() from Location object
4) GPSHelper makes an intent with action.LOCATION_UPDATE and adds speed value
5) GPSHelper broadcasts intent

6) HUDactivity has broadcast receiver listening for LOCATION_UPDATE intents (HUDActivity.GPSHelperListener)
7) HUDactivity.GPSHelperListener recieves intent
8) HUDactivity.GPSHelperListener extracts Speed value from intent
9) HUDactivity updates speedometer UI element with new speed value





INTENT DEFINITIONS

Intent.action.LOCATION_UPDATE
-float speed
-float longitude
-float latitude
-float bearing

Intent.action.SENSOR_UPDATE
-int inclinationPercent
-int linearAcceleration

Intent.action.EFI_UDPDATE
-int RPM
-int fuelEconomy
-int throttlePosition



COMPONENT DESCRIPTIONS

Main-activity
-verifies and checks permissions for gps bluetooth etc.
-starts gps, sensors, bluetooth etc. background services
-launches config and race HUD Activities


HUD-activity
-provids all necessary information do the driver in a UI(spedometer and stuff)
-catches intents broadcasted from services to asynchronously update its UI


GPSHelper-Service
-Is in operation while the app is open
-Constantly listening to the android gps module for location updates
-processes data prom gps and packages necessary information into an intent
-broadcasts intent to the context of the app for use by other processes

SensorsHelper-service
-same as gps service but litends to various device sensors including accelerometer
-

Bluetoothhelper-Service
-





Adding new services

Adding new processes
IF you are working on this app and you are at a point where you feel that more information