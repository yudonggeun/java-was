package codesquad.config;

import codesquad.http.HttpRequest;
import codesquad.http.Method;
import codesquad.router.RouteTableRow;
import codesquad.router.handler.HttpHandler;
import codesquad.util.collections.Tries;
import codesquad.util.scan.Solo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * http 요청을 매핑 설정입니다.
 */
@Solo
public class RouterConfig {

    private final Map<Method, Tries<RouteTableRow>> methodTries = new HashMap<>();

    public void addRouteTable(List<RouteTableRow> table) {
        for (RouteTableRow routeInfo : table) {
            for (Method method : routeInfo.getMethods()) {
                methodTries.putIfAbsent(method, new Tries<>());
                methodTries.get(method).insert(routeInfo.getUrlTemplate(), routeInfo);
            }
        }
    }

    public Optional<HttpHandler> findHandler(HttpRequest request) {
        return methodTries.getOrDefault(request.method, new Tries<>())
                .search(request.path)
                .filter(routeTableRow -> routeTableRow.isMatch(request))
                .map(RouteTableRow::getHandler);
    }

    public Optional<HttpHandler> findHandler(Method method, String path) {
        return methodTries.getOrDefault(method, new Tries<>())
                .search(path)
                .map(RouteTableRow::getHandler);
    }
}
