package codesquad.context;

import codesquad.config.FilterConfig;
import codesquad.config.RouterConfig;

public class ApplicationContext {

    public static ApplicationContext context = new ApplicationContext();

    public FilterConfig getFilterConfig() {
        return new FilterConfig(this);
    }

    public RouterConfig getRouterConfig() {
        return new RouterConfig();
    }
}
