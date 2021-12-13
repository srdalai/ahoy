package com.sdtechnocrat.ahoy.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;

import com.sdtechnocrat.ahoy.R;

public class DetailsCtaButton extends LinearLayout {

    private ImageView mIcon;
    TextView mTitle;

    Drawable iconDrawable = null;

    public DetailsCtaButton(Context context) {
        super(context, null);
    }

    public DetailsCtaButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DetailsCtaButton, 0, 0);

        String titleText = a.getString(R.styleable.DetailsCtaButton_titleText);

        //Setting Parent View
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.details_cta_item_bg, null));
        setClickable(true);

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        /*layoutParams.setMargins(
                Util.convertDpToPixel(context, 4),
                Util.convertDpToPixel(context, 60),
                Util.convertDpToPixel(context, 4),
                Util.convertDpToPixel(context, 60)
        );*/
        setLayoutParams(layoutParams);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_view_details_cta_button_layout, this, true);

        mIcon = (ImageView) getChildAt(0);
        mTitle = (TextView) getChildAt(1);

        setText(titleText);
        mTitle.setMaxLines(1);
        try {
            int drawableResId = a.getResourceId(R.styleable.DetailsCtaButton_iconDrawable, -1);
            iconDrawable = AppCompatResources.getDrawable(context, drawableResId);
            setIconDrawable(iconDrawable);
        } finally {
            a.recycle();
        }

    }

    public void setIconDrawable(Drawable iconDrawable) {
        mIcon.setImageDrawable(iconDrawable);
    }

    public void setText(String titleTxt) {
        if (mTitle != null) {
            mTitle.setText(titleTxt);
        }
    }

    public void setTextSize(int textSize) {
        if (mTitle != null) {
            mTitle.setTextSize(textSize);
        }
    }

    public void setButtonActive(boolean isActive) {
        setClickable(isActive);
        if (isActive) {
            if (mIcon != null) {
                ImageViewCompat.setImageTintList(mIcon, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.cta_item_activated)));
                //mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_item_color), PorterDuff.Mode.MULTIPLY);
            }
            if (mTitle != null) {
                mTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.cta_item_activated));
            }
        } else {
            if (mIcon != null) {
                ImageViewCompat.setImageTintList(mIcon, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.cta_item_de_activated)));
                //mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_item_color), PorterDuff.Mode.MULTIPLY);
            }
            if (mTitle != null) {
                mTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.cta_item_de_activated));
            }
        }
    }


}
