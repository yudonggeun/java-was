package codesquad.handler;

import codesquad.config.RouterConfig;

import java.util.List;

public abstract class RouteTable {

    public RouteTable(RouterConfig config) {
        config.setRoute(table());
    }

    protected abstract List<RouteEntry> table();
}
