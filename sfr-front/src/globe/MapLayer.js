import React, { useMemo } from "react";
import * as d3 from "d3";

function MapLayer({ geoData, rx, ry, x, y, zoom, className, updateFrom, updateDestination, start, end }) {
    const projection = useMemo(
        () =>
            d3
                .geoOrthographic()//.geoMercator()
                .scale(zoom)
                .translate([x, y])
                .rotate([rx, -ry]),
        [rx, ry, x, y, zoom]
    );

    const path = d3.geoPath().projection(projection);
    const Tooltip = d3.select("#node-info-popup")

    if (className !== "aerodromi") {
        return (
            <g>
                <path d={path(geoData)} className={className} />
                <path
                    key={"start.properties.iata"}
                    d={path(start)}
                    className={"start"}
                />
                <path
                    key={"end.properties.iata"}
                    d={path(end)}
                    className={"finish"}
                />
            </g>
        );
    } else {
        return (
            <g>
                <path
                    key={"start.properties.iata"}
                    d={path(start)}
                    className={"start"}
                />
                <path
                    key={"end.properties.iata"}
                    d={path(end)}
                    className={"finish"}
                />
                {geoData.features.map(
                    (feature => {
                        return <path
                            key={feature.properties.iata}
                            d={path(feature)}
                            className={"aerodromi"}
                            onMouseOver={() => onMouseOver(Tooltip)}
                            onMouseLeave={() => onMouseLeave(Tooltip)}
                            onMouseMove={(e, d) => onMouseMove(e, feature, Tooltip)}
                            onClick={(e, d) => onMouseLeftClick(e, feature)}
                            onContextMenu={(e, d) => onMouseRightClick(e, feature)}
                        />
                    })
                )}
            </g>
        )
    }

    function onMouseOver(Tooltip) { Tooltip.style("opacity", 1);}
    function onMouseLeave(Tooltip) { Tooltip.style("opacity", 0) }
    function onMouseMove(e, d, Tooltip) {
        console.log(e)
        Tooltip.html(
            "<b> " + d.properties.name + ",</b><br/>" + d.properties.city + ", " + d.properties.country + "<br/>" + d.properties.iata)
            .style("left", (d3.pointer(e)[0] + 20) + "px")
            .style("top", (d3.pointer(e)[1] - 528) + "px");
    };
    function onMouseLeftClick(e, d) {
        updateFrom(d);
    }
    function onMouseRightClick(e, d) {
        updateDestination(d);
    }
}

export default MapLayer;
