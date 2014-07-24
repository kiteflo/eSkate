package com.sobag.parsetemplate.lists;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.makeramen.RoundedImageView;
import com.parse.ParseException;
import com.sobag.parsetemplate.R;
import com.sobag.parsetemplate.domain.Board;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.util.FontUtility;

import java.util.List;

import roboguice.inject.InjectView;

public class BoardListAdapter extends ArrayAdapter<Board>
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private final FontUtility fontUtility;
    private final Context context;
    private final List<Board> boards;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    public BoardListAdapter(Context context, List<Board> boards,
                            FontUtility fontUtility)
    {
        super(context, R.layout.cell_board, boards);
        this.context = context;
        this.boards = boards;
        this.fontUtility = fontUtility;
    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        Board board = boards.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cell_board, parent, false);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.tv_title);
        tvTitle.setText(board.getTitle());

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);

        // setup image...
        RoundedImageView ivImage = (RoundedImageView)rowView.findViewById(R.id.iv_image);
        try
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(board.getImage().getData(), 0, board.getImage().getData().length);
            ivImage.setImageBitmap(bitmap);
        }
        catch (ParseException ex)
        {
            ex.printStackTrace();
        }

        return rowView;
    }
}