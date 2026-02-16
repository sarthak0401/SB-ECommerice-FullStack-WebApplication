package org.ecommerce.project.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsService implements MeterBinder {

    private Counter loginSuccess;
    private Counter loginFailure;
    private Counter orderCreated;
    private Counter orderFailed;

    // MeterBinder guarantees metric existence at startup, while PostConstruct depends on bean usage.
    @Override
    public void bindTo(MeterRegistry registry) {
        loginSuccess = Counter.builder("app_login_success_total")
                .description("Successful logins")
                .register(registry);

        loginFailure = Counter.builder("app_login_failure_total")
                .description("Failed logins")
                .register(registry);

        orderCreated = Counter.builder("app_order_created_total")
                .description("Orders successfully created")
                .register(registry);

        orderFailed = Counter.builder("app_order_failed_total")
                .description("Orders failed")
                .register(registry);
    }

    public void loginSuccess(){ loginSuccess.increment(); }
    public void loginFailure(){ loginFailure.increment(); }
    public void orderCreated(){ orderCreated.increment(); }
    public void orderFailed(){ orderFailed.increment(); }
}
