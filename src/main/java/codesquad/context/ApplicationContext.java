package codesquad.context;

import codesquad.config.FilterConfig;
import codesquad.config.RouterConfig;
import codesquad.handler.LoginTable;
import codesquad.template.HtmlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext {

    public static ApplicationContext context = new ApplicationContext();
    public static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    static {
        context.getRouterConfig();
        context.getFilterConfig();
        context.getHtmlManager();

        // router init
        context.getLoginHandler();
    }

    private FilterConfig filterConfig;

    public FilterConfig getFilterConfig() {
        if (filterConfig == null) {
            logger.info("init filter config");
            filterConfig = new FilterConfig(this);
        }
        return filterConfig;
    }

    private RouterConfig routerConfig;

    public RouterConfig getRouterConfig() {
        if (routerConfig == null) {
            logger.info("init router config");
            routerConfig = new RouterConfig();
        }
        return new RouterConfig();
    }

    private HtmlManager htmlManager;
    private LoginTable loginHandler;

    public HtmlManager getHtmlManager() {
        if (htmlManager == null) {
            logger.info("init html manager");
            htmlManager = new HtmlManager();
        }
        return htmlManager;
    }

    public LoginTable getLoginHandler() {
        if (loginHandler == null) {
            logger.info("init login handler");
            loginHandler = new LoginTable(getRouterConfig(), getHtmlManager());
        }
        return loginHandler;
    }
}
