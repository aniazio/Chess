/*
https://stackoverflow.com/questions/67048033/prevent-jpopupmenu-from-closing-on-click
 */

package szachy;

import javax.swing.*;

class MyPopupMenu extends JPopupMenu {
    private boolean isHideAllowed;

    public MyPopupMenu(){
        super();
        this.isHideAllowed = false;
    }

    public MyPopupMenu(String text){     //dodane
        super(text);
        this.isHideAllowed = false;
    }


    @Override
    public void setVisible(boolean visibility){
        if(isHideAllowed && !visibility)
            super.setVisible(false);
        //else if(!isHideAllowed && visibility)
        //    super.setVisible(true);
        else super.setVisible(true);    //dodane
    }

    public void closePopup(){
        this.isHideAllowed = true;
        this.setVisible(false);
    }
}
