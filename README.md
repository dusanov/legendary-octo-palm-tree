# Flight Path Finder

This app finds the shortest flight path between two airports using three different algorithms: Breadth-First Search (BFS), A* Search, and Dijkstra's algorithm.

## Starting the App
To start the app, simply run the following command:

```bash
docker-compose up -d --bui
and go to http://localhost:3000
```

## Using the App
To select start and destination airport, use the start and finish dropdowns or use left and right click on the globe while hovering mouse over the airport. 
To select the different algorithm use the algo dropdown. To execute the search use the Go button.

Zoom in and out is performed by holding down left and right mouse buttons while dragging the mouse up and down or using the mouse wheel.

Rotation of the globe is performed by holding down left mouse button while moving the mouse.

## Technologies Used
The app is divided into two parts: 
* frontend D3, ReactJS and Apollo client
* backend Spring WebFlux and GraphQL

Check out a demo of the app in action in the video below:

https://user-images.githubusercontent.com/5050430/209156984-365cdbbe-0cb8-49d2-ba22-d34a1874d796.mp4

### Licensing and disclaimer
This project uses datasets for airports and routes from [OpenFlights](https://openflights.org) and is published under __GNU General Public License v3.0__
