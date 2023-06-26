package it.polimi.ingsw.view.GraphicalUI;

public class FXRunnable implements Runnable{

    public void run(){

    }
    private Object var1;
    private Object var2;
    private Object var3;

    FXRunnable(Object ob1){
        var1 = ob1;
    }

    FXRunnable(Object ob1, Object ob2){
        var1 = ob1;
        var2 = ob2;
    }

    FXRunnable(Object ob1, Object ob2, Object ob3){
        var1 = ob1;
        var2 = ob2;
        var3 = ob3;
    }

    public Object getFirstParameter(){
        return var1;
    }

    public Object getSecondParameter(){
        return var2;
    }

    public Object getThirdParameter(){
        return var3;
    }
}
