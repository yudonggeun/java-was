package codesquad.handler;

public record RouteEntry(
        URLMatcher urlMatcher,
        HttpHandler httpHandler
) {
}
