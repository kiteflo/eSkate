package com.sobag.parsetemplate.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * global stuff such as date format etc...
 */
public class GlobalUtility
{
    public static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    public static DecimalFormat decimalFormat = new DecimalFormat("#.##");
}
