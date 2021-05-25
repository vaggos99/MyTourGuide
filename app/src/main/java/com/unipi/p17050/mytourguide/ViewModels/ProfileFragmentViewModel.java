package com.unipi.p17050.mytourguide.ViewModels;

import androidx.lifecycle.ViewModel;

public class ProfileFragmentViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private boolean cultureButton,religionButton,sportsButton,entertainmentButton;

    public boolean isCultureButton() {
        return cultureButton;
    }

    public void setCultureButton(boolean cultureButton) {
        this.cultureButton = cultureButton;
    }

    public boolean isReligionButton() {
        return religionButton;
    }

    public void setReligionButton(boolean religionButton) {
        this.religionButton = religionButton;
    }

    public boolean isSportsButton() {
        return sportsButton;
    }

    public void setSportsButton(boolean sportsButton) {
        this.sportsButton = sportsButton;
    }

    public boolean isEntertainmentButton() {
        return entertainmentButton;
    }

    public void setEntertainmentButton(boolean entertainmentButton) {
        this.entertainmentButton = entertainmentButton;
    }
}
