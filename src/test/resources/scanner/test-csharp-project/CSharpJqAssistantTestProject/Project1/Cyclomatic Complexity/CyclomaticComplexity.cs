
class CyclomaticComplexityExample {
    private int shouldBeOne(int a, int b ) {

        return a + b;
    }

    private void ShouldBeTwo(bool a ) {

        int x = 0;

        if (a) {
            x=1;
        } else {
            x=2;
        }
    }

    private void ShouldBeTwoAsWell() {

        int x = 0;

        for(int i = 0; i < 10; i++) {
            x++;
        }
    }

    private void ShouldBeThree(bool a , bool b ) {

        int x = 0;

        if (a && b) {
            x=1;
        } else {
            x=2;
        }
    }

}