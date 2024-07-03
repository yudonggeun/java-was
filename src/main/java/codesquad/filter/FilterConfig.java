package codesquad.filter;

import java.util.Set;
import java.util.TreeSet;

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
public class FilterConfig {

    private static final Set<Filter> filters = new TreeSet<>();

    public Filter[] getFilters() {
        return filters.toArray(new Filter[0]);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
