package codesquad.util.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriesTest {

    @Test
    void stringTriesTest1() {
        Tries<String> tries = new Tries<>();

        tries.insert("/users/test", "one");
        tries.insert("/test", "two");
        tries.insert("/index.html", "three");

        assertThat(tries.search("/users/tes")).isEmpty();
        assertThat(tries.search("/users/test")).get().isEqualTo("one");
        assertThat(tries.search("/test/users")).isEmpty();
    }

    @Test
    void pathVariable() {

        Tries<String> tries = new Tries<>();

        tries.insert("/users/test/test", "test");
        tries.insert("/users/{path}/test", "one");

        assertThat(tries.search("/users/hello/test")).get().isEqualTo("one");
        assertThat(tries.search("/users/test/test")).get().isEqualTo("test");
        assertThat(tries.search("/users/hello")).isEmpty();

    }
}