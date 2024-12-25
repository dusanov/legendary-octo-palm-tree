# Flight Path Finder

This app finds the shortest flight path between two airports using three different algorithms: Breadth-First Search (BFS), A* Search, and Dijkstra's algorithm.

## Starting the App (development)
To start the app, simply run the following command:

```bash
docker compose --env-file dev.env up -d --build
```
and go to http://localhost:3000

## Running Selenium tests
To run selenium tests, simply run the following command:

```bash
docker compose --env-file test.env -f docker-compose.test.yml up -d --build
```
currently only way to confirm the result is to check the logs:

```bash
docker logs legendary-octo-palm-tree-testovi-1 --follow
```
or by watching the tests being executed in the selenium grid:
http://localhost:7900/?autoconnect=1&resize=scale&password=secret

## Technologies Used
The app is divided into two parts: 
* frontend D3, ReactJS and Apollo client
* backend Spring WebFlux and GraphQL

Check out a demo of the app in action in the video below:

https://user-images.githubusercontent.com/5050430/209156984-365cdbbe-0cb8-49d2-ba22-d34a1874d796.mp4

### Licensing and disclaimer
This project uses datasets for airports and routes from [OpenFlights](https://openflights.org) and is published under __GNU General Public License v3.0__
