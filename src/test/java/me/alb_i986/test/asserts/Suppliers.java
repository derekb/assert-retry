package me.alb_i986.test.asserts;

import me.alb_i986.test.asserts.retry.Supplier;

public class Suppliers {

    /**
     * Generate an ascending sequence of integers, starting from the given integer.
     *
     * @param initialInt the first element of the sequence
     */
    public static Supplier<Integer> ascendingIntegersStartingFrom(int initialInt) {
        return new AscendingIntegersSupplier(initialInt);
    }

    private static class AscendingIntegersSupplier implements Supplier<Integer> {
        private int i;

        public AscendingIntegersSupplier(int initialInt) {
            this.i = initialInt;
        }

        @Override
        public Integer get() {
            return i++;
        }
    }

    /**
     * Generate a descending sequence of integers, starting from the given integer.
     *
     * @param initialInt the first element of the sequence
     */
    public static Supplier<Integer> descendingIntegers(int initialInt) {
        return new DescendingIntegersSupplier(initialInt);
    }

    private static class DescendingIntegersSupplier implements Supplier<Integer> {
        private int i;

        public DescendingIntegersSupplier(int initialInt) {
            this.i = initialInt;
        }

        @Override
        public Integer get() {
            return i--;
        }
    }
}
