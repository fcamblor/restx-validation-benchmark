/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.restx;

import io.restx.pojos.*;
import org.openjdk.jmh.annotations.*;
import restx.validation.ValidatorFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@BenchmarkMode({Mode.SingleShotTime, Mode.SampleTime})
@Measurement(iterations = 10)
@Warmup(iterations = 10)
public @State(Scope.Benchmark) class ValidationBenchmark {

    Validator validator;

    POJOWithNoConstraint noConstraintPOJO;
    POJOWithFewConstraints fewConstraintsPOJO;
    POJOWithLotsOfConstraints lotsOfConstraintsPOJO;

    POJOWithNoConstraint noConstraintEmptyPOJO;
    POJOWithFewConstraints fewConstraintsEmptyPOJO;
    POJOWithLotsOfConstraints lotsOfConstraintsEmptyPOJO;

    POJOWithNoConstraint noConstraintFullPOJO;
    POJOWithFewConstraints fewConstraintsFullPOJO;
    POJOWithLotsOfConstraints lotsOfConstraintsFullPOJO;

    Map<String, AbstractPOJO> namedPojos;

    @Setup
    public void setup(){
        validator = new ValidatorFactory().validator(Boolean.TRUE);

        noConstraintFullPOJO = new POJOWithNoConstraint();
        fewConstraintsFullPOJO = new POJOWithFewConstraints();
        lotsOfConstraintsFullPOJO = new POJOWithLotsOfConstraints();

        fullyFillPOJOs(noConstraintFullPOJO, fewConstraintsFullPOJO, lotsOfConstraintsFullPOJO);

        noConstraintPOJO = new POJOWithNoConstraint();
        fewConstraintsPOJO = new POJOWithFewConstraints();
        fewConstraintsFullPOJO.setF0(new SimpleAggregatablePOJO("p1")).setF2("an_email2@acme.fr");
        lotsOfConstraintsPOJO = new POJOWithLotsOfConstraints();
        fillPOJOWithMandatoryValuesOnly(lotsOfConstraintsPOJO);

        noConstraintEmptyPOJO = new POJOWithNoConstraint();
        fewConstraintsEmptyPOJO = new POJOWithFewConstraints();
        lotsOfConstraintsEmptyPOJO = new POJOWithLotsOfConstraints();

        namedPojos = new HashMap<String, AbstractPOJO>();
        namedPojos.put("noConstraintPOJO", noConstraintPOJO);
        namedPojos.put("fewConstraintsPOJO", fewConstraintsPOJO);
        namedPojos.put("lotsOfConstraintsPOJO", lotsOfConstraintsPOJO);
        namedPojos.put("noConstraintEmptyPOJO", noConstraintEmptyPOJO);
        namedPojos.put("fewConstraintsEmptyPOJO", fewConstraintsEmptyPOJO);
        namedPojos.put("lotsOfConstraintsEmptyPOJO", lotsOfConstraintsEmptyPOJO);
        namedPojos.put("noConstraintFullPOJO", noConstraintFullPOJO);
        namedPojos.put("fewConstraintsFullPOJO", fewConstraintsFullPOJO);
        namedPojos.put("lotsOfConstraintsFullPOJO", lotsOfConstraintsFullPOJO);
    }

    private static void fullyFillPOJOs(AbstractPOJO... pojos) {
        for(AbstractPOJO pojo : pojos){
            fillPOJOWithMandatoryValuesOnly(pojo).setF1(1).setF3(3).setF4(true).setF6(6).setF8(8).setF9(false);
        }
    }

    private static AbstractPOJO fillPOJOWithMandatoryValuesOnly(AbstractPOJO pojo) {
        pojo.setF0(new SimpleAggregatablePOJO("p1")).setF2("an_email2@acme.fr").setF5(new SimpleAggregatablePOJO("p5")).setF7("an_email7@acme.fr");
        return pojo;
    }

    // Just for local tests...
    public static void main(String[] args) {
        final ValidationBenchmark b = new ValidationBenchmark();
        b.setup();

        System.out.println("validate() first occurence...");
        for(Map.Entry<String,AbstractPOJO> pojoEntry : b.namedPojos.entrySet()){
            long t = System.currentTimeMillis();
            b.validator.validate(pojoEntry.getValue());
            System.out.println(String.format("%s: %s", pojoEntry.getKey(), System.currentTimeMillis()-t));
        }

        // Starting a second occurence, it will take far less time !..
        // Supposing first validate() made validation annotations in cache
        System.out.println("validate() second occurence...");
        for(Map.Entry<String,AbstractPOJO> pojoEntry : b.namedPojos.entrySet()){
            long t = System.currentTimeMillis();
            b.validator.validate(pojoEntry.getValue());
            System.out.println(String.format("%s: %s", pojoEntry.getKey(), System.currentTimeMillis()-t));
        }

        // Just to be sure validate() works...
        Set<ConstraintViolation<POJOWithFewConstraints>> errors = b.validator.validate(b.fewConstraintsEmptyPOJO);
        if(errors.size() == 0){
            throw new IllegalStateException("Expected at least 1 errors after validate()");
        }
    }

    @Benchmark
    public void testValidatorWithNoConstraintOnFullPOJO() {
        validator.validate(noConstraintFullPOJO);
    }

    @Benchmark
    public void testValidatorWithFewConstraintOnFullPOJO() {
        validator.validate(fewConstraintsFullPOJO);
    }

    @Benchmark
    public void testValidatorWithLotsOfConstraintOnFullPOJO() {
        validator.validate(lotsOfConstraintsFullPOJO);
    }

    @Benchmark
    public void testValidatorWithNoConstraintOnEmptyPOJO() {
        validator.validate(noConstraintEmptyPOJO);
    }

    @Benchmark
    public void testValidatorWithFewConstraintOnEmptyPOJO() {
        validator.validate(fewConstraintsEmptyPOJO);
    }

    @Benchmark
    public void testValidatorWithLotsOfConstraintOnEmptyPOJO() {
        validator.validate(lotsOfConstraintsEmptyPOJO);
    }

    @Benchmark
    public void testValidatorWithNoConstraintOnPOJO() {
        validator.validate(noConstraintPOJO);
    }

    @Benchmark
    public void testValidatorWithFewConstraintOnPOJO() {
        validator.validate(fewConstraintsPOJO);
    }

    @Benchmark
    public void testValidatorWithLotsOfConstraintOnPOJO() {
        validator.validate(lotsOfConstraintsPOJO);
    }
}
