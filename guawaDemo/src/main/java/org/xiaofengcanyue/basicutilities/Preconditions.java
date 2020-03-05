package org.xiaofengcanyue.basicutilities;

public class Preconditions {

    public static void main(String[] args) {
        com.google.common.base.Preconditions.checkArgument(1 > 2);
        com.google.common.base.Preconditions.checkNotNull("");
        com.google.common.base.Preconditions.checkState(1<2);
        com.google.common.base.Preconditions.checkElementIndex(1,1);
        com.google.common.base.Preconditions.checkPositionIndex(1,1);
        com.google.common.base.Preconditions.checkPositionIndexes(1,1,2);
    }

}
