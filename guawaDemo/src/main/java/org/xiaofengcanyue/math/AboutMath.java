package org.xiaofengcanyue.math;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;

import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Basic standalone math functions are divided into the classes IntMath, LongMath, DoubleMath, and BigIntegerMath based on the primary numeric type involved.
 * A variety of statistical calculations (mean, median, etc.) are provided, for both single and paired data sets.(https://github.com/google/guava/wiki/StatsExplained)
 * LinearTransformation represents a linear conversion between double values of the form y = mx + b; for example, a conversion between feet and meters, or between Kelvins and degrees Fahrenheit.
 *
 */
public class AboutMath {
    public static void main(String[] args) {

    }

    /**
     * fail fast on overflow instead of silently ignoring it.
     */
    public static void aboutCheckedArithmetic(){
        IntMath.checkedAdd(Integer.MAX_VALUE,Integer.MAX_VALUE);
    }

    /**
     * IntMath, LongMath, and BigIntegerMath have support for a variety of methods with a "precise real value," but that round their result to an integer.
     * These methods accept a java.math.RoundingMode. This is the same RoundingMode used in the JDK, and is an enum with the following values:
     *
     * DOWN: round towards 0. (This is the behavior of Java division.)
     * UP: round away from 0.
     * FLOOR: round towards negative infinity.
     * CEILING: round towards positive infinity.
     * UNNECESSARY: rounding should not be necessary; if it is, fail fast by throwing an ArithmeticException.
     * HALF_UP: round to the nearest half, rounding x.5 away from 0.
     * HALF_DOWN: round to the nearest half, rounding x.5 towards 0.
     * HALF_EVEN: round to the nearest half, rounding x.5 to its nearest even neighbor.
     */
    public static void aboutRealValuedMethods(){
        BigIntegerMath.sqrt(BigInteger.TEN.pow(99), RoundingMode.HALF_EVEN);
    }

    /**
     * Operation	                                        IntMath	            LongMath	        BigIntegerMath
     * Greatest common divisor	                            gcd(int, int)	    gcd(long, long)	    In JDK: BigInteger.gcd(BigInteger)
     * Modulus (always nonnegative, -5 mod 3 is 1)	        mod(int, int)	    mod(long, long)	    In JDK: BigInteger.mod(BigInteger)
     * Exponentiation (may overflow)	                    pow(int, int)	    pow(long, int)	    In JDK: BigInteger.pow(int)
     * Power-of-two testing	                                isPowerOfTwo(int)	isPowerOfTwo(long)	isPowerOfTwo(BigInteger)
     * Factorial (returns MAX_VALUE if input too big)	    factorial(int)	    factorial(int)	    factorial(int)
     * Binomial coefficient (returns MAX_VALUE if too big)	binomial(int, int)	binomial(int, int)	binomial(int, int)
     */
    public static void aboutAdditionalFunctions(){

    }

    public static void aboutFloatingPointArithmetic(){
        DoubleMath.isMathematicalInteger(1d);
        DoubleMath.roundToInt(1d,RoundingMode.HALF_EVEN);
        DoubleMath.roundToLong(1d,RoundingMode.HALF_EVEN);
        DoubleMath.roundToBigInteger(1d,RoundingMode.HALF_EVEN);
        DoubleMath.log2(1d,RoundingMode.HALF_EVEN);
    }


}
