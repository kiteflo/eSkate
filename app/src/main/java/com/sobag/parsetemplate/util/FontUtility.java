package com.sobag.parsetemplate.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sobag.parsetemplate.enums.FontApplicableComponent;

import javax.inject.Provider;

import roboguice.inject.ContextSingleton;

/**
 * Common font operations...
 */
@ContextSingleton
public class FontUtility
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Provider<Context> contextProvider;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    @Inject
    public FontUtility(Provider<Context> contextProvider)
    {
        this.contextProvider = contextProvider;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    /**
     * Apply font to component.
     * @param component
     * The component the font should be applied to
     * @param fontResourceID
     * Resource ID of font - check res/values/styleConfig.xml
     * @param componentType
     * Component type - we need to know the type of component in order to cast...
     * stupid Android View does not provide .setTypeface(...)  :(
     */
    public void applyFontToComponent(View component,int fontResourceID,FontApplicableComponent componentType)
    {
        // get default font....
        String desiredFont = contextProvider.get().getString(fontResourceID);
        Typeface typeface = Typeface.createFromAsset(contextProvider.get().getAssets(), desiredFont);

        switch (componentType)
        {
            case BUTTON:
            {
                Button button = (Button)component;
                button.setTypeface(typeface);

                break;
            }
            case EDIT_TEXT:
            {
                EditText editText = (EditText)component;
                editText.setTypeface(typeface);

                break;
            }
            case TEXT_VIEW:
            {
                TextView textView = (TextView)component;
                textView.setTypeface(typeface);

                break;
            }
        }
    }
}
