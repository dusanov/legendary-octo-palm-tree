import React from 'react';
import SearchResult from './SearchResult';
import Globe from './globe/Globe';
import client from './apolloSetup';
import * as d3 from "d3";
import worldMap from './data/world-map.geojson';
import Select from 'react-select';
import { CURR_SEARCH_PATH, ALL_AIRPORTS, BFS } from './Queries';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      result: null,
      current: {},
      airportOptions: [],
      from: null,
      destination: null,
      selectedFrom: {},
      selectedDestination: {},
      isLoaded: false,
      algo: "BFS",
      geoData: [],
      start: {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [0, 0] },
        "properties": {
          "name": "airport.name",
          "city": "airport.city",
          "country": "airport.country",
          "iata": "airport.iata",
        }
      },
      end: {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [0, 0] },
        "properties": {
          "name": "airport.name",
          "city": "airport.city",
          "country": "airport.country",
          "iata": "airport.iata",
        }
      }
    };

  }

  componentDidMount = () => {
    this.loadData();    
    this.subsribeToSearchPath();
  }

  subsribeToSearchPath = () => {
    const subscription = client.subscribe({
      query: CURR_SEARCH_PATH
    }).subscribe((data) => {

      this.drawSearchPath(data.data.currentSearchPath);


    })
  }

  loadData = () => {
    Promise.all([
      d3.json(worldMap),
      client.query({
        query: ALL_AIRPORTS
      })
    ]).then(([regions, airports]) => {
      this.setState({
        airports: airports,
        airportOptions: airports.data.getAllAirports.map((airport) => ({ value: airport.iata, label: airport.name + ", " + airport.city + ", " + airport.country })),
        isLoaded: true,
        geoData: [
          { key: "regions", className: "regions", geoJson: regions },
          {
            key: "airports",
            className: "aerodromi",
            geoJson: {
              "type": "FeatureCollection",
              "features": airports.data.getAllAirports.map(airport => {
                return {
                  "type": "Feature",
                  "geometry": { "type": "Point", "coordinates": [airport.longitude, airport.latitude] },
                  "properties": {
                    "name": airport.name,
                    "city": airport.city,
                    "country": airport.country,
                    "iata": airport.iata,
                    "longitude": airport.longitude,
                    "latitude": airport.latitude
                  }
                };
              })
            }
          },
        ]
      });
    });
  }

  onFromDropdownChange = (e) => {
    this.updateFrom(
      this.state.geoData[1].geoJson.features.filter(airport => airport.properties.iata === e.value)[0]
    );
  }

  updateFrom = (e) => {
    this.setState({
      from: e,
      selectedFrom: { value: e, label: e.properties.name + ", " + e.properties.city + ", " + e.properties.country },
      start: {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [e.properties.longitude, e.properties.latitude] },
        "properties": {
          "name": e.properties.name,
          "city": e.propertiescity,
          "country": e.properties.country,
          "iata": e.properties.iata,
        }
      }
    });
  }

  onDestinationDropdownChange = (e) => {
    this.updateDestination(
      this.state.geoData[1].geoJson.features.filter(airport => airport.properties.iata === e.value)[0]
    );
  }
  updateDestination = (e) => {
    this.setState({
      destination: e,
      selectedDestination: { value: e, label: e.properties.name + ", " + e.properties.city + ", " + e.properties.country },
      end: {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [e.properties.longitude, e.properties.latitude] },
        "properties": {
          "name": e.properties.name,
          "city": e.propertiescity,
          "country": e.properties.country,
          "iata": e.properties.iata,
        }
      }
    });
  }

  onSearch = () => {
    this.setState({
      result: null,
      current: {},
      geoData: [
        ...this.state.geoData.filter((geo, index) => {
          return geo.key !== "search" && geo.key !== "current";
        }),
        { key: "search", className: "path", geoJson: {} },
        { key: "current", className: "current", geoJson: {} },
      ]
    });
    client.query({
      query: BFS,
      variables: { from: this.state.from.properties.iata, destination: this.state.destination.properties.iata, algo: this.state.algo }
    })
      .then(({ data }) => {
        this.setState({
          current: {},
          result: data.search
        })

        this.drawSearchPath(data.search);

      }).catch((error) => { console.log("error in load data", error) });
  }

  drawSearchPath(data) {
    let searchLineString = {
      'type': 'Feature',
      'geometry': {
        'type': 'LineString',
        'coordinates': []
      }
    };
    this.setState({
      geoData: [
        ...this.state.geoData.filter((geo, index) => {
          return geo.key !== "current-path" && geo.key !== "current";
        }),
        {
          key: "current-path", className: "path", geoJson: {
            "type": "FeatureCollection",
            "features": [
              ...data.path.map(airport => {
                searchLineString.geometry.coordinates.push([airport.longitude, airport.latitude]);
                return {
                  "type": "Feature",
                  "geometry": { "type": "Point", "coordinates": [airport.longitude, airport.latitude] },
                  "properties": { "name": airport.name }
                };
              }), searchLineString
            ]
          }
        },
      ]
    });
  }

  render() {
    const { isLoaded, current, result, selectedFrom, selectedDestination, geoData, start, end } = this.state;
    return (
      <div className="container-fluid">
        <div className="row">
          <div className="float position-absolute col-4 mt-2">
            <div className="row">
              <div className="input-group">
                <div className="col-2">
                  <span className="input-group-text" id="">start</span>
                  <span className="input-group-text" id="">finish</span>
                  <span className="input-group-text" id="">algo</span>
                </div>
                <div className="col-8">
                  {isLoaded ?
                    <Select value={selectedFrom} onChange={this.onFromDropdownChange} isSearchable={true} options={this.state.airportOptions} />
                    : <p>...</p>}
                  {isLoaded ?
                    <Select value={selectedDestination} onChange={this.onDestinationDropdownChange} isSearchable={true} options={this.state.airportOptions} />
                    : <p>...</p>}
                  <select className="form-select" onChange={e => this.setState({ algo: e.target.value })}>
                    <option key="BFS" value="BFS">BFS</option>
                    <option key="astar" value="astar">AStar</option>
                    <option key="dijkstra" value="dijkstra">Dijkstra</option>
                  </select>
                </div>
                <div className="row">
                  <button
                    onClick={this.onSearch}
                    type="button"
                    className="btn btn-outline-primary"
                  >GO</button>
                </div>
              </div>
            </div>
            <div className="addScroll row-fluid col-11">
              <SearchResult result={result} />
            </div>
          </div>
          <div className="row">
            {(geoData) ?
              <Globe
                geoData={geoData}
                updateFrom={this.updateFrom}
                updateDestination={this.updateDestination}
                start={start}
                end={end} 
              />
              : <p> ..loading..</p>}
          </div>
          <div className="fixed-bottom">
            {(current.airportId) ?
              <div className="alert alert-transparent">
                <p><b>{current.name}, </b>{current.city}, {current.country}</p>
              </div> : null
            }
            {(result) ?
              <div className="alert alert-transparent">
                <p> execution time: {result.time}ms,
                  algo: {result.algo},
                  num of nodes: {result.numNodes},
                  distance: {result.distance}km,
                  price: {result.price}</p>
              </div> : null
            }
          </div>
        </div>
      </div>
    );
  }
}

export default App;
