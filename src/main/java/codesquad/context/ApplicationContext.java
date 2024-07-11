package codesquad.context;

import codesquad.config.FilterConfig;
import codesquad.config.RouterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext {

    public static ApplicationContext context = new ApplicationContext();
    public static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    static {
        context.getRouterConfig();
        context.getFilterConfig();
    }

    private FilterConfig filterConfig;
    private RouterConfig routerConfig;

    public FilterConfig getFilterConfig() {
        if (filterConfig == null) {
            logger.info("init filter config");
            filterConfig = new FilterConfig(this);
        }
        return filterConfig;
    }

    public RouterConfig getRouterConfig() {
        if (routerConfig == null) {
            logger.info("init router config");
            routerConfig = new RouterConfig();
        }
        return new RouterConfig();
    }
}
