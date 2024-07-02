package codesquad.filter;

import java.util.Set;
import java.util.TreeSet;

public class FilterConfig {

    private static final Set<Filter> filters = new TreeSet<>();

    public Filter[] getFilters() {
        return filters.toArray(new Filter[0]);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
