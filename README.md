# QSMVehicleInterface

This app was built fully priotitizing performance over battery life and memory use
This 

Main activity
-verifies and checks permissions for gps bluetooth etc.
-starts gps, sensors, bluetooth etc. background services
-launches config and race HUD Activities
-

HUD activity
-provids all necessary information do the driver in a UI(spedometer and stuff)
-catches intents broadcasted from services to asynchronously update its UI
-

GPS service
-
