package dijkstra.parallel.long_task;

public interface ExchangerFactory {
    Exchanger create(int parallelism);
}
