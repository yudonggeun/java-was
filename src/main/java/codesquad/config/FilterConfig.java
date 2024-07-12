package codesquad.config;

import codesquad.context.ApplicationContext;
import codesquad.filter.Filter;
import codesquad.filter.FilterChain;
import codesquad.filter.Router;
import codesquad.util.scan.Solo;

import java.util.Arrays;

/**
 * <h2>
 * FilterChain 생성을 위한 설정 정보를 담고 있는 클래스입니다.
 * </h2>
 *
 * <p>
 * Filter는 어플리케이션의 가동 시점에서 결정되어야하는 설정 정보이고 전역으로 적용되는 되어야 하는 요구사항을 가지고 있습니다.
 * 이 요구사항을 만족하기 위해서 FilterConfig는 생성할 FilterChain에 대한 필요한 정보를 가지고 있습니다.
 * Filter의 우선순위에 따라서 실행 순서가 결정 되기 때문에 추가된 필터의 우선순위를 고려하여 필터를 입력해주세요.
 * </p>
 *
 * @author yudonggeun
 * @see FilterChain
 * @see Filter
 */
@Solo
public class FilterConfig {

    private final Filter[] filters;
    private final Router router;

    public FilterConfig(Router router, ApplicationContext context) {
        this.filters = context.getSoloObjects(Filter.class).toArray(new Filter[0]);
        this.router = router;
        Arrays.sort(this.filters);
    }

    public Filter[] getFilters() {
        return filters;
    }

    /**
     * 가장 마지막에 실행되어야 하는 필터를 반환합니다. 해당 필터를 통해서 handler를 호출합니다.
     *
     * @return
     */
    public Router getRouter() {
        return router;
    }
}
