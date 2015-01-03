package io.restx;

import io.restx.pojos.AbstractPOJO;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author fcamblor
 */
public class SingleShotWith1ValidateWarmupValidationBenchmark extends SingleShotWithNoWarmupValidationBenchmark {
    @Setup
    @Override
    public void setup() {
        super.setup();

        // Making one validate() call on every POJOs, allowing to demonstrate the first validate() call
        // annotation caching (in regards to SingleShotWithNoWarmupValidationBenchmark)
        for(AbstractPOJO pojo : namedPojos.values()){
            validator.validate(pojo);
        }
    }
}
