# QSMVehicleInterface





Concept:

The app runs on the principal that all external data must be collected, processed, and provided ASYNCHRONOUSLY
to the scope of the application using SERVICES (currently GPS, sensors, and blue tooth) on an as-available basis. This data is used on an on-needed basis by other processes such as activities, fragments, and loggers;

This app was built fully prioritizing performance over battery life and memory use.

Process:
On start, the SERVICES start listening for new data. When there is new data, the SERVICE processes it and puts the needed information into an INTENT with an action corresponding to its contents.
(eg. intent.LOCATION_UPDATE contains speed int, intent.SENSOR_UPDATE contains vehicleIncline)

This intent is broadcast to the rest of the app
If a process needs information from a service, it must implement a BROADCAST LISTENER to catch intents

	Data Flow
	1) New data is available
	2) Service processes data
	3) Service sends processed data in a filtered intent to the context of the application
	4) Broadcast receiver sees intent with same filter
	5) Broadcast receiver extracts data and passes to function


	Example data flow
	1) Physical location changes
	2) GPS module outputs new location object

	3) GPSHelper asynchronously receives location object from GPS module
	3) GPSHelper extracts getSpeed() from Location object
	4) GPSHelper makes an intent with action.LOCATION_UPDATE and adds speed value
	5) GPSHelper broadcasts intent

	6) HUDactivity has broadcast receiver listening for LOCATION_UPDATE intents (HUDActivity.GPSHelperListener)
	7) HUDactivity.GPSHelperListener receives intent
	8) HUDactivity.GPSHelperListener extracts Speed value from intent
	9) HUDactivity updates speedometer UI element with new speed value


Purpose:
The purpose of this implementation is to allow for all incoming data to be processed asynchronously, 
and for that information to be provided to the scope of the application as soon as it is ready.
This allows the UI elements of the App to be extremely responsive as it eliminates the need for the active activity to process
any data, leaving it the sole responsibility of updating the UI element value as a new value is provided.
This implementation allows for a truly modular and expandable project as it allows for additional processes to be added without disrupting communication between existing components of the project.



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
-kills all other running applications
-verifies and checks permissions for gps bluetooth etc.
-starts gps, sensors, bluetooth etc. background services
-launches config and race HUD Activities



HUD-activity
-provides all necessary information do the driver in a UI(speedometer and stuff)
-catches intents broadcast from services to asynchronously update its UI


GPSHelper-Service
-Is in operation while the app is open
-Constantly listening to the android gps module for location updates
-processes data prom gps and packages necessary information into an intent
-broadcasts intent to the context of the app for use by other processes

SensorsHelper-service
-same as gps service but listens to various device sensors including accelerometer


Bluetoothelper-Service
-communicates with EFI and turns
-converts byte array into real values



------------------------------------------------------------------------------------------------------------------------------
LOG
Mar 19 2018 -Aidan McKenna
First Upload
The initial intended purpose of this application is a car telemetry display for use by the driver of the QSM vehicle. Some required values to be shown on this display were defined by QSM team (km/h, incline, lap count, etc). Note that the requirements for this application allow it to prioritize performance over memory and power consumption.

After coming up with a concept for the structure of the app, Mitchell gave me the piece of advice to ensure that the UI elements of the display were updated asynchronously. I looked into Services and Intents to provide asynchronous process support to the app. I began using services to asynchronously collect and process the data from external sources (GPS module) and intents to send the data to the UI elements on the display as soon as the data is ready. I then found that this data could just be broadcast to the scope of the application and grabbed out of the air by active Broadcast Receivers in active processes.

The data we are collecting and processing has the potential to serve many more purposes than we've already defined for the car this year.
For a simple example, we are using a GPS service to receive Location objects from the GPS module in the android device. We are only extracting the speed attribute of this Location object to update the speedometer on the HUD. However, the Location object from the GPS module actually contains tons of other juicy data that potentially has plenty other uses. Say I wanted to make a processes that logs and stores gps location over time. The information is already being gathered and processed by the GPS service so I just add a broadcast receiver to the logging class to extract the data from the intent.
If more uses for the data are defined, it should be so that the solutions can be implemented without affecting the manner at which the data is collected. This app structure aims to allow the addition of processes without affecting the process at which data is acquired. Services gathering and providing data will act independently of the processes using the data.

These design decisions were mainly focus around ease of development and scalability. They allow for further development of the UI, but also development in analytics and optimization. I will do what I can in my time working on this app to lay a foundation by setting up data providing services for those developing the application to draw from. Providing them data in this manner is pretty inefficient on contrast to an on-request system, but it allows for simple and easy scalability. Remember to take the opportunity of being able to prioritize performance over ram and power consumption to make some sweet code. 


Development for this application can be broken up into a few parts:
	
	Data acquisition
	-find new methods of gathering any data from the vehicle or device
	-initial processing of data into useful forms
	-may use peripheral processor like arduino for physical sensor support

	Runtime data processing
	-find new methods of getting as much information out of the gathered data as possible
	-defining packages to be sent to scope of application

	UI
	-defining UI elements that would aide the drivers ability to perform
	-managing 

	Data storage and analytics
	-storage and organization of gathered data
	-EFI tuning
	-nerdy matlab stuff


Examples of new uses of data
GPS
-making a virtual GPS gate for automatic lap incrementation
-algorithm for finding optimal route
-finding optimal acceleration profile vs fuel consumption

EFI
-taking the FUELPW values from the EFI to have a UI element showing live fuel economy
-finding ideal fuel consumption to acceleration ratio

Sensors
-effects of incline on performance















