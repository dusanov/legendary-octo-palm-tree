import React from 'react';

class SearchResult extends React.Component {
    render(){
        return (
            <div className="row-fluid">
                {
                    (this.props.result)?
                    this.props.result.path.map((airport, arrayItemIndex, wholeArray) => {
                    return (
                            <div className="alert-transparent" key={airport.airportId}>
                                <b>{airport.name}</b>
                                <p>{airport.city},{airport.country}
                                {/* <br />
                                [{airport.latitude},{airport.longitude}] */}
                                </p>
                            </div>   
                        );
                    }):null
                } 
            </div>
        );
    }
}

export default SearchResult;