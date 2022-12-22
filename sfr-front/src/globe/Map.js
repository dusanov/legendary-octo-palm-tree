import React from "react";

import MapLayer from "./MapLayer";

class Map extends React.PureComponent {
    state = {
        x: this.props.x,
        y: this.props.y,
        rx: this.props.rx,
        ry: this.props.ry,
        initialZoom: this.props.zoom,
        zoom: this.props.zoom,
        geoData: this.props.geoData
    };

    static getDerivedStateFromProps(props, state) {
        let {x, y, rx, ry, zoom, initialZoom, geoData, Tooltip} = state;
        const {delta, rotate} = props;

        x = x || props.x;
        y = y || props.y;

        zoom = initialZoom - props.zoom / 10;
        if (rotate) {
            rx += delta.x;
            ry += delta.y;
        } else {
            x += delta.x;
            y += delta.y;
        }

        geoData = props.geoData;
            // props.geoData.length !== geoData.length ? props.geoData : geoData;

        return {...state, geoData, zoom, x, y, rx, ry};
    }

    render() {
        const {x, y, rx, ry, zoom, geoData} = this.state;        
        const maps = geoData.map(item => {
            return (
                <MapLayer
                    className={item.className}
                    geoData={item.geoJson}
                    rx={rx}
                    ry={ry}
                    x={x}
                    y={y}
                    zoom={zoom}
                    key={item.key}
                    updateFrom={this.props.updateFrom}
                    updateDestination={this.props.updateDestination}
                    start={this.props.start}
                    end={this.props.end}
                />
            );
        });
        
        return <g> {maps} </g>;
    }
}

export default Map;
