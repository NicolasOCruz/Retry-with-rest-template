package com.nicolascruz.restTemplate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class RetryConfig {

	private final NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

	private final DefaultListenerSupport listener;

	private final Logger logger = LoggerFactory.getLogger(RetryConfig.class);

	public RetryConfig(DefaultListenerSupport listener) {
		this.listener = listener;
	}

	@Bean
	RestTemplateCustomizer retryRestTemplateCustomizer() {
		return restTemplate -> restTemplate.getInterceptors().add((request, body, execution) -> {

			RetryTemplate retryTemplate = new RetryTemplate();

			ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
			policy.setExceptionClassifier(getClassifier());

			FixedBackOffPolicy back = new FixedBackOffPolicy();
			back.setBackOffPeriod(3000);

			retryTemplate.setRetryPolicy(policy); // para número de tentativas e quando tentar novamente
			retryTemplate.setBackOffPolicy(back); // para intervalo entre as tentativas
			retryTemplate.registerListener(listener); //executa uma ação (ao iniciar, finalizar ou errar em cada tentativa)

			try {
				return retryTemplate.execute(context -> {
					logger.info("Trying .... + " + context.getRetryCount());
					return execution.execute(request, body);
				});
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		});
	}

	private Classifier<Throwable, RetryPolicy> getClassifier() {
		/*
		 * Definição de nova tentativa. Nesse caso, quando o StatusCode da resposta for
		 * 5xx, ou seja, algum erro no lado do servidor
		 */
		SimpleRetryPolicy policy = new SimpleRetryPolicy();
		policy.setMaxAttempts(3);

		return throwable -> {
			if (throwable instanceof HttpStatusCodeException) {
				HttpStatusCodeException exception = (HttpStatusCodeException) throwable;

				switch (exception.getStatusCode()) {
					case BAD_GATEWAY:
					case SERVICE_UNAVAILABLE:
					case GATEWAY_TIMEOUT:
						return policy;
					default:
						return neverRetryPolicy;
				}
			}
			return neverRetryPolicy;
		};

	}
}