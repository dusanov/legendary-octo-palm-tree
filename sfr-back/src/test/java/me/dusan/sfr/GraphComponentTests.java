package me.dusan.sfr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.dusan.sfr.graph.GraphComponent;

@SpringBootTest(properties = {
        "graphql.servlet.enabled=false",
        "graphql.servlet.websocket.enabled=false"
})
public class GraphComponentTests {

    @Autowired
    private GraphComponent graphComponent;

    @Test
    void testBFS() throws InterruptedException {
        graphComponent.bfs("BEG", "BKK").getPath().forEach(System.out::println);
    }

    @Test
    void testAStar() throws InterruptedException {
        graphComponent.astar("BEG", "BKK").getPath().forEach(System.out::println);
    }

    @Test
    void testDijkstra() throws InterruptedException {
        graphComponent.dijkstra("BEG", "BKK").getPath().forEach(System.out::println);
    }
}
