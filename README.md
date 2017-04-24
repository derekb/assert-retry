# Assert Retry

An extension to JUnit/Hamcrest providing assertions with _tolerance_, featuring a __retry__ mechanism.

## Usage

    import static me.alb_i986.testing.hamcrest.Matchers.*;
    
    [..]

    @Test
    public void test() {
        [..]
        assertThat(actual, customMatches(expected)); // assume customMatches() comes from our me.alb_i986.testing.hamcrest.Matchers
        assertThat(actualString, contains("blabla")); // contains() comes from the official org.hamcrest.Matchers
    }
    
By importing `me.alb_i986.testing.hamcrest.Matchers`, the matchers defined
in the official Hamcrest project (`org.hamcrest.Matchers`) are automatically imported as well.