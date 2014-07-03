package com.sobag.parsetemplate.util;

import java.lang.reflect.Field;

/**
 * Resource related helpers such as retrieving resource ID from string etc.
 */
public class ResourceUtility
{
    public static int getId(String resourceName, Class<?> c)
    {
        try
        {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        }
        catch (Exception e)
        {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }
}
