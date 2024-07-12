package codesquad.util.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 해당 어노테이션이 붙은 클래스는 단 하나만 존재해야하는 클래스임을 나타냅니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Solo {
}
