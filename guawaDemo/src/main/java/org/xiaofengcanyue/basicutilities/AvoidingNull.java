package org.xiaofengcanyue.basicutilities;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * 尽量避免在map的value中使用null，也不要在list和set的元素中使用null。
 */
public class AvoidingNull {

    public static void main(String[] args) {

    }

    public static void useOfOptional(){
        Optional<Integer> possible = Optional.of(5);
        possible.isPresent();
        possible.get();
    }

    public static void convenienceMethods(){
        MoreObjects.firstNonNull("","");

        Strings.emptyToNull("");
        Strings.isNullOrEmpty("");
        Strings.nullToEmpty("");
    }
}
