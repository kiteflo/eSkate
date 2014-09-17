package com.sobag.parsetemplate.lists.items;

/**
 * Created by tzhmufl2 on 03.09.14.
 */
public class NavigationListItem
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private String label;
    private int drawable;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public NavigationListItem(String label, int drawable)
    {
        this.label = label;
        this.drawable = drawable;
    }

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getDrawable()
    {
        return drawable;
    }

    public void setDrawable(int drawable)
    {
        this.drawable = drawable;
    }
}
