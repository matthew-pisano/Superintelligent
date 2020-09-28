package com.reactordevelopment.superintelligent;

import android.util.Log;
import android.widget.TabHost;

public class Exp {
    private double num;
    private int pow;

    public Exp(double num, int pow){
        if(num == 0) pow = 0;
        //Log.i("Expconstruct0", num+", "+pow);
        while (Math.abs(num) > 10 || Math.abs(num) < 1 && num != 0){
            //Log.i("Expconstruct1", num+", "+pow);
            if(Math.abs(num) > 10){
                num /= 10;
                pow += 1;
            }
            if(Math.abs(num) < 1){
                num *= 10;
                pow -= 1;
            }
        }
        this.num = num;
        this.pow = pow;
        //Log.i("Expconstruct2", num+", "+pow);
    }
    public Exp copy(){return new Exp(num, pow);}
    public double[] getValues(){
        return new double[]{num, pow};
    }
    public int getPow(){ return pow; }
    public double getNum(){ return num; }

    private Exp adder(Exp b, boolean change) {
        //Log.i("ExpAdd", ""+this+", "+b);
        int neg = (int) (b.getNum() / Math.abs(b.getNum()));
        double numB = Math.abs(b.getNum());
        int powB = b.getPow();
        double numS = num;
        int powS = pow;
        //Log.i("Adder1", num+", "+pow+", "+numB+", "+powB);
        if(Math.abs(pow-powB) < 10) num += neg * numB * Math.pow(10, powB - pow);
        else if(powB > pow && b.getNum() != 0){
            pow = powB;
            num = b.getNum();
        }
        //Log.i("Adder2", num+", "+pow+", "+numB+", "+powB);
        //Log.i("ExpAdd", "1");
        while (Math.abs(num) >= 10) {
            num /= 10;
            pow += 1;
        }
        //Log.i("ExpAdd", "3");
        while (Math.abs(num) < 1 && num > 0) {
            num *= 10;
            pow -= 1;
        }
        //Log.i("Adder2.5", num+", "+pow+", "+numB+", "+powB+", "+this);
        if(!change){
            num = numS;
            pow = powS;
        }
        //Log.i("Adder3", num+", "+pow+", "+numB+", "+powB+", "+this);
        return this;
    }
    private Exp multiplier(Exp b, boolean change){
        //Log.i("ExpMult", ""+new Exp(num, pow)+", "+b);
        int powB = b.getPow();
        double numB = b.getNum();
        double numS = num;
        int powS = pow;
        num *= numB;
        pow += powB;
        //Log.i("ExpMult", "1");
        while (Math.abs(num) >= 10) {
            num /= 10;
            pow += 1;
        }
        //Log.i("ExpMult", "2");
        while (Math.abs(num) < 1 && num > 0) {
            num *= 10;
            pow -= 1;
        }
        //Log.i("ExpMult", ""+new Exp(num, pow));
        Exp save = new Exp(num, pow);
        if(!change){
            num = numS;
            pow = powS;
        }
        return save;
    }
    public static Exp toExp(double num){return new Exp(num, 0);}
    public Exp add(Exp b){ return adder(b, true); }
    public Exp added(Exp b){ return adder(b, false); }
    public Exp multiply(Exp b){ return multiplier(b, true); }
    public Exp multiplied(Exp b){ return multiplier(b, false); }
    private int compare(Exp b){
        //Log.i("Compare", ""+this+", "+b+", pow lt:"+(b.getPow() > pow)+", pow gt:"+(b.getPow() < pow)+", num:"+Double.compare(num, b.getNum()));
       if(b.getPow() > pow) return b.getNum() > 0 ? -1 : 1;
       if(pow > b.getPow()) return num > 0 ? 1 : -1;
       return Double.compare(num, b.getNum());
    }
    public Exp negate(){
        num = -num;
        return new Exp(num, pow);
    }
    public Exp negated(){
        return new Exp(-num, pow);
    }
    public Exp invert(){
        pow = -pow;
        num = 1/num;
        return new Exp(num, pow);
    }
    public Exp inverted(){
        return new Exp(1/num, -pow);
    }
    public boolean greaterThan(Exp b){return compare(b) == 1;}
    public boolean lessThan(Exp b){return compare(b) == -1;}
    public boolean equalTo(Exp b){return compare(b) == 0;}

    @Override
    public String toString(){
        double workingNum = num;
        int workingPow = pow;
        while (Math.abs(workingNum) >= 1000) {
            workingNum /= 10;
            workingPow += 1;
        }
        while (Math.abs(workingNum) < 1 && workingNum > 0) {
            workingNum *= 10;
            workingPow -= 1;
        }
        return ""+(int)(workingNum*10)/10.0+"E"+workingPow;
    }
    public double toDouble(){
        if(pow < 307) return num*Math.pow(10, pow);
        return -1;
    }
    public static Exp sumArray(Exp[] array){
        Exp temp = new Exp(0, 0);
        for(int i=0; i<array.length; i++)
            temp.add(array[i]);
        return temp;
    }
    private String toUnitString(boolean isIllion){
        double workingNum = num;
        int workingPow = pow;
        while (workingPow % 3 != 0) {
            workingNum *= 10;
            workingPow -= 1;
        }
        String unitStr = "";
        if(workingPow == 3) unitStr = isIllion ? "Thousand" : "Kilo";
        if(workingPow == 6) unitStr = isIllion ? "Million" : "Mega";
        if(workingPow == 9) unitStr = isIllion ? "Billion" : "Giga";
        if(workingPow == 12) unitStr = isIllion ? "Trillion" : "Tera";
        if(workingPow == 15) unitStr = isIllion ? "Quadrillion" : "Peta";
        if(workingPow == 18) unitStr = isIllion ? "Quintillion" : "Exa";
        if(workingPow == 21) unitStr = isIllion ? "Sextillion" : "Zetta";
        if(workingPow == 24) unitStr = isIllion ? "Septillion" : "Yotta";
        return ""+(int)(workingNum*10)/10.0+" "+unitStr;
    }
    public String toIllionString(){ return toUnitString(true); }
    public String toPrefixString(){ return toUnitString(false); }
}
