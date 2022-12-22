import {useState, useCallback, useLayoutEffect} from "react";

function getDimensions(node) {
    const rect = node.getBoundingClientRect();

    return {
        width: rect.width,
        height: rect.height,
        top: rect.top || rect.y,
        left: rect.left || rect.x,
        x: rect.x || rect.left,
        y: rect.y || rect.top,
        right: rect.right,
        bottom: rect.bottom
    };
}

function useDimensions() {
    const [dimensions, setDimensions] = useState({});
    const [node, setNode] = useState(null);

    const ref = useCallback(node => {
        setNode(node);
    }, []);

    useLayoutEffect(
        () => {
            if (node) {
                const measure = () =>
                    window.requestAnimationFrame(() =>
                        setDimensions(getDimensions(node))
                    );
                measure();
                window.addEventListener("resize", measure);

                return () => {
                    window.removeEventListener("resize", measure);
                };
            }
        },
        [node]
    );

    return [ref, dimensions, node];
}

export default useDimensions;
