/**
 *
 */
package com.aceql.client.jdbc.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ArrayTransporter {

    private static final String SEPARATOR = "|!|";

    /**
     * Static class
     */
    protected ArrayTransporter() {


    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	int [] intArray = { 1, 2, 3};
	String join = arrayToString(intArray);
	int [] intArray2 = stringToIntArray(join);

	for (int i = 0; i < intArray2.length; i++) {
	    System.out.println(intArray2[i]);
	}

	String [] stringArray = {"one", "two", "threee"};
	join = arrayToString(stringArray);

	String [] stringArray2 = stringToStringtArray(join);
	System.out.println(Arrays.asList(stringArray2));
    }



    public static String arrayToString(String[] stringArray) {
	if (stringArray == null) {
	    return "NULL";
	}

	List<String> listArray = Arrays.asList(stringArray);
	String join = StringUtils.join(listArray, SEPARATOR);
	return join;
    }


    public static String[] stringToStringtArray(String join) {
	if (join == null || join.equals("NULL")) {
	    return null;
	}

	String [] split = StringUtils.split(join, SEPARATOR);
	return split;
    }

    public static String arrayToString(int[] intArray) {
	if (intArray == null) {
	    return "NULL";
	}

	List<Integer> listArray = Arrays.stream(intArray).boxed().collect(Collectors.toList());
	String join = StringUtils.join(listArray, SEPARATOR);
	return join;
    }

    public static int[] stringToIntArray(String join) {

	if (join == null || join.equals("NULL")) {
	    return null;
	}

	String [] split = StringUtils.split(join, SEPARATOR);
	int [] intArray2 = new int [split.length];

	for (int i = 0; i < split.length; i++) {
	    intArray2[i]= Integer.parseInt(split[i]);
	}

	return intArray2;

    }
}