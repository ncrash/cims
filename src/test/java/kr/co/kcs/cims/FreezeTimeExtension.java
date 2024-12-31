package kr.co.kcs.cims;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class FreezeTimeExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == Clock.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        FreezeTime annotation = extensionContext.getRequiredTestMethod().getAnnotation(FreezeTime.class);
        Instant instant = Instant.parse(annotation.value());
        return Clock.fixed(instant, ZoneId.systemDefault());
    }
}
