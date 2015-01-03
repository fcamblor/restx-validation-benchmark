package io.restx;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author fcamblor
 */
@BenchmarkMode({Mode.SingleShotTime})
@Warmup(iterations = 0) // No warmup
public class SingleShotWithNoWarmupValidationBenchmark extends ValidationBenchmark {
}
