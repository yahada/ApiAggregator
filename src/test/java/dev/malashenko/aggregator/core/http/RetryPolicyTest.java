package dev.malashenko.aggregator.core.http;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

	@Test
	void constructor_validParameters() {
		RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), Duration.ofSeconds(10));
		assertEquals(3, policy.getMaxAttempts());
	}

	@Test
	void constructor_throwsOnZeroAttempts() {
		assertThrows(IllegalArgumentException.class,
			() -> new RetryPolicy(0, Duration.ofSeconds(1), Duration.ofSeconds(10)));
	}

	@Test
	void constructor_throwsOnNegativeAttempts() {
		assertThrows(IllegalArgumentException.class,
			() -> new RetryPolicy(-1, Duration.ofSeconds(1), Duration.ofSeconds(10)));
	}

	@Test
	void constructor_throwsOnNegativeInitialDelay() {
		assertThrows(IllegalArgumentException.class,
			() -> new RetryPolicy(3, Duration.ofSeconds(-1), Duration.ofSeconds(10)));
	}

	@Test
	void constructor_throwsOnInitialGreaterThanMax() {
		assertThrows(IllegalArgumentException.class,
			() -> new RetryPolicy(3, Duration.ofSeconds(10), Duration.ofSeconds(1)));
	}

	@Test
	void getDelay_exponentialBackoff() {
		RetryPolicy policy = new RetryPolicy(5, Duration.ofSeconds(1), Duration.ofSeconds(60));
		assertEquals(Duration.ofSeconds(1), policy.getDelay(1));
		assertEquals(Duration.ofSeconds(2), policy.getDelay(2));
		assertEquals(Duration.ofSeconds(4), policy.getDelay(3));
		assertEquals(Duration.ofSeconds(8), policy.getDelay(4));
	}

	@Test
	void getDelay_capsAtMaxDelay() {
		RetryPolicy policy = new RetryPolicy(5, Duration.ofSeconds(10), Duration.ofSeconds(30));
		assertEquals(Duration.ofSeconds(10), policy.getDelay(1));
		assertEquals(Duration.ofSeconds(20), policy.getDelay(2));
		assertEquals(Duration.ofSeconds(30), policy.getDelay(3));
		assertEquals(Duration.ofSeconds(30), policy.getDelay(4));
	}

	@Test
	void getDelay_handlesLargeAttemptNumbers() {
		RetryPolicy policy = new RetryPolicy(100, Duration.ofSeconds(1), Duration.ofSeconds(60));
		Duration delay = policy.getDelay(50);
		assertEquals(Duration.ofSeconds(60), delay);
	}
}
