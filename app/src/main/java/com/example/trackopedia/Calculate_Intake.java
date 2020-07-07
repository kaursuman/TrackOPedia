package com.example.trackopedia;

import java.text.DecimalFormat;


public class Calculate_Intake
{
    int cweights[]=new int[]{10,11,21,31,41,42,43,44,46,48,49,50,52,54,55,57,59,60,62,64,66,68,69,71,73,75,77,80,100};
    int c1720[]=new int[]{70,80,90,110,119,124,132,136,141,145,150,155,160,165,170,175,180,185,190,195,201,206,212,218,223,229,234,240,250};
    int c2127[]=new int[]{70,80,90,110,119,124,136,140,144,149,154,159,163,169,174,179,185,189,195,200,206,212,217,223,229,235,240,240,250};
    int c2839[]=new int[]{70,80,90,110,119,124,139,144,148,153,158,163,168,174,179,184,189,194,200,205,211,217,223,229,235,241,247,240,250};
    int c41[]=new int[]{70,80,90,110,119,124,141,146,150,155,160,165,170,176,181,186,192,197,203,208,214,220,226,232,238,244,250,250,250};

    int weights[]=new int[]{45,49,54,58,63,68,72,77,81,86,90,95,99,104,108,113};
    double w_intake[]=new double[]{1.9,2.1,2.3,2.5,2.7,2.9,3.1,3.3,3.5,3.7,3.9,4.1,4.3,4.5,4.7,4.9};

    int age,weight,height;
    String gender;

    int water_intake=0;
    int protien_intake=0;
    int fats_intake=0;

    double calconvert=0.12959782;

    public Calculate_Intake(int a, int w, int h, String gen)
    {
        age=a;
        weight=w;
        height=h;
        gender=gen;
    }


    //Water intake as per age
    public void cal_age()
    {
        double p_intake=0;
        if(age==0)
        {
            p_intake=0.8;
        }
        else if(age>=1 && age<=3)
        {
            p_intake=1.3;
        }
        else if(age>=4 && age<=8)
        {
            p_intake=1.7;
        }
        else if(age>=9 && age<=13 && gender.compareTo("Male")==0)
        {
            p_intake=2.4;
        }
        else if(age>=14 && age<=18 && gender.compareTo("Male")==0)
        {
            p_intake=3.3;
        }
        else if(age>=9 && age<=13 && gender.compareTo("Female")==0)
        {
            p_intake=2.1;
        }
        else if(age>=14 && age<=18 && gender.compareTo("Female")==0)
        {
            p_intake=2.3;
        }
        else if(age>18 && gender.compareTo("Male")==0)
        {
            p_intake=3.7;
        }
        else if(age>18 && gender.compareTo("Female")==0)
        {
            p_intake=2.7;
        }

        cal_weight(p_intake);
    }

    //Water intake as per weight
    public void cal_weight(double p_intake)
    {
        for(int i=0;i<weights.length;i++)
        {
            if(i==0)
            {
                if(weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
            else if(i==weights.length-1)
            {
                if(weight>weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
                else if(weight>weights[i-1] && weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
            else
            {
                if(weight>weights[i-1] && weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
        }
    }

    //Water intake average of both
    public void final_cal(double p_intake,double p1_intake)
    {
        water_intake=(int)(p_intake+p1_intake)/2;
    }


    //Protein intake as per age
    public int calc_prot()
    {
        if(age>=1 && age<=3)
        {
            protien_intake=13;
        }
        else if(age>=4 && age<=8)
        {
            protien_intake=19;
        }
        else if(age>=9 && age<=13)
        {
            protien_intake=34;
        }
        else if(age>=14 && age<=70 && gender.compareTo("Female")==0)
        {
            protien_intake=37;
        }
        else if(age>=14 && age<=70 && gender.compareTo("Male")==0)
        {
            protien_intake=52;
        }
        else if(age>70 && gender.compareTo("Male")==0)
        {
            protien_intake=46;
        }
        else if(age>70 && gender.compareTo("Female")==0)
        {
            protien_intake=65;
        }
        return (int)(protien_intake/calconvert);
    }

    //Fats intake as per weight
    public int calc_weights()
    {
        int pos=0;
        for(int i=0;i<cweights.length;i++)
        {
            if(i==0)
            {
                if(weight<=cweights[i])
                {
                    pos=i;
                    return i;
                }
            }
            else if(i==cweights.length-1)
            {
                if(weight>cweights[i])
                {
                    pos=i;
                    return i;
                }
                else if(weight>cweights[i-1] && weight<=cweights[i])
                {
                    pos=i;
                    return i;
                }
            }
            else
            {
                if(weight>cweights[i-1] && weight<=cweights[i])
                {
                    pos=i;
                    return i;
                }
            }
        }
        return pos;
    }

    //Fats intake as per age & Weight
    public int calc_fats()
    {
        int pos=calc_weights();
        if(age>=15 && age<=20)
        {
            fats_intake=c1720[pos];
        }
        else if(age>=21 && age<=27)
        {
            fats_intake=c2127[pos];
        }
        else if(age>=28 && age<=39)
        {
            fats_intake=c2839[pos];
        }
        else if(age>40)
        {
            fats_intake=c41[pos];
        }
        return (int)(fats_intake/calconvert);
    }

    //Carb Intake as per BMI
    public int calc_carbs()
    {
        double ans_bmi=cal_bmi();
        if (ans_bmi >= 19.00 && ans_bmi <= 25.00)
        {
            return (int)(125/calconvert);
        }
        else if (ans_bmi < 19.00)
        {
            return (int)(150/calconvert);
        }
        else if (ans_bmi >= 30.00)
        {
            return (int)(50/calconvert);
        }
        else if (ans_bmi > 25.00)
        {
            return (int)(100/calconvert);
        }
        return 0;
    }

    //Calculate BMI
    public double cal_bmi()
    {
        double weight_lbs = weight * 2.2046226218;
        double height_inches = height / 2.54;
        double bmi_w = weight_lbs * 0.45;
        double bmi_h = height_inches * 0.025;
        double bmi_square = bmi_h * bmi_h;
        double ans_bmi = bmi_w / bmi_square;
        return ans_bmi;
    }

    //BMI Status
    public String bmi()
    {
        String ans="";
        double weight_lbs = weight * 2.2046226218;
        double height_inches = height / 2.54;

        double bmi_w = weight_lbs * 0.45;
        double bmi_h = height_inches * 0.025;
        double bmi_square = bmi_h * bmi_h;
        double ans_bmi = bmi_w / bmi_square;

        DecimalFormat df=new DecimalFormat("#.00");
        ans=df.format(ans_bmi);
        if (ans_bmi >= 19.00 && ans_bmi <= 25.00)
        {
            ans+="*"+"Healthy";
        }
        else if (ans_bmi < 19.00)
        {
            ans+="*"+"Under Weight";
        }
        else if (ans_bmi >= 30.00)
        {
            ans+="*"+"Over Weight";
        }
        else if (ans_bmi > 25.00)
        {
            ans+="*"+"Slightly Over Weight";
        }
        return ans;
    }

    public String calculate_all()
    {
        int prot=calc_prot();
        int fats=calc_fats();
        int carbs=calc_carbs();

        double ans_bmi=cal_bmi();

        if (ans_bmi >= 19.00 && ans_bmi <= 25.00)
        {
            prot-=200;
            fats-=100;
            carbs-=150;
        }
        else if (ans_bmi < 19.00)
        {
            prot-=50;
            fats-=50;
            carbs-=50;
        }
        else if (ans_bmi >= 30.00)
        {
            prot-=250;
            fats-=500;
            carbs-=200;
        }
        else if (ans_bmi > 25.00)
        {
            prot-=200;
            fats-=200;
            carbs-=200;
        }

        return prot+"*"+fats+"*"+carbs;
    }
}
