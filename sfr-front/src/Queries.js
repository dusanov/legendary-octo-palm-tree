import gql from 'graphql-tag';

export const BFS = gql`
  query bfs($from: String!, $destination: String!, $algo: String!){
    search(from: $from, destination: $destination, algo: $algo) 
    {
      time
      algo
      numNodes
      distance
      price
      path {
        airportId
        name
        city
        country
        latitude
        longitude
      }
    }
}`;
export const CURR_SEARCH_PATH = gql`
      subscription currentPath{
        currentSearchPath {
          path {
            name
            city
            country
            iata
            latitude
            longitude
          }
        }
      }      
      `;
export const ALL_AIRPORTS = gql`
          {
            getAllAirports {
              airportId
              iata
              name
              city
              country
              longitude
              latitude
            }
          }`;
