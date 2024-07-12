package codesquad.context;

import codesquad.config.RouterConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextTest {

    @Test
    @DisplayName("등록된 객체를 조회하면 싱글톤이다.")
    public void make() {
        ApplicationContext context = new ApplicationContext();
        context.scan("codesquad");
        Object soloObject1 = context.getSoloObject(RouterConfig.class);
        RouterConfig soloObject2 = (RouterConfig) context.getSoloObject(RouterConfig.class);

        assertThat(soloObject2 == soloObject1).isTrue();
    }
}