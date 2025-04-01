# architect-coders
Repository to manage Ignacio Nicolas app's development during the architects coders Course

This Branch contains the first practical task of the course.

## About the App
This app is a very simple implementation of the usage of the Location Google Play services. It is
composed of two screens.

### Screens
The first screen `LocationRequesterScreen` is there to fetch in the foreground the current location
of the user, displaying it in a list with some basic information. If the user clicks on any of the
displayed location they will be directed to the second screen.

The second screen `LocationDetailScreen` will display the selected location in a detailed view. As 
the screen is loading the app will attempt to request that location's height from the sea level.
That is achieved through an HTTP request to an elevation API.

### ViewModels
Both screens count with their individual view models in charge of processing the user inputs and
making changes to the Screen's state. They are as of this version being instantiated in the
`MainActivity` navigation Graph, with their parameters being injected through a homebrew dependency
injection.

### Repositories
For fetching the Location the app uses an implementation of the `LocationRepository`, which can
control location updates through its `requestLocationUpdates`, and receive them through a callback
lambda, `removeLocationUpdates` methods as well as request a single one through `requestSingleLocation`.

Lastly, the elevation is fetched through the implementation of `ElevationRepository`, which its
`getElevationForLocations` receive a list of locations and returns a list of double values
corresponding to that set of locations.

## Running the app
The app can be run directly after building and installing it. There is at this moment a slight
limitation, the user must grant the precise location permission or the app won't run properly.

